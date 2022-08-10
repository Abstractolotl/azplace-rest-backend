package de.abstractolotl.azplace.rest.controller;

import de.abstractolotl.azplace.exceptions.board.CanvasNotFoundException;
import de.abstractolotl.azplace.exceptions.bot.BotLimitReachedException;
import de.abstractolotl.azplace.exceptions.bot.BotSystemBannedException;
import de.abstractolotl.azplace.exceptions.bot.InvalidTokenException;
import de.abstractolotl.azplace.exceptions.punishment.UserBannedException;
import de.abstractolotl.azplace.model.board.Canvas;
import de.abstractolotl.azplace.model.requests.PlaceRequest;
import de.abstractolotl.azplace.model.user.User;
import de.abstractolotl.azplace.model.user.UserBotToken;
import de.abstractolotl.azplace.model.view.BotView;
import de.abstractolotl.azplace.repositories.BotRepo;
import de.abstractolotl.azplace.repositories.CanvasRepo;
import de.abstractolotl.azplace.rest.api.BotAPI;
import de.abstractolotl.azplace.service.AuthenticationService;
import de.abstractolotl.azplace.service.BoardService;
import de.abstractolotl.azplace.service.PunishmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@RestController
public class BotController implements BotAPI {

    @Autowired private AuthenticationService authenticationService;
    @Autowired private PunishmentService punishmentService;
    @Autowired private BoardService boardService;

    @Autowired private BotRepo botRepo;
    @Autowired private CanvasRepo canvasRepo;

    @Value("${bot.per-user:3}")
    private Integer MAX_BOT_PER_USER;

    @Value("${bot.rate-limit:500}")
    private Integer DEFAULT_RATE_LIMIT;

    @Override
    public BotView createBotToken() {
        User user = authenticationService.authUser();

        if(authenticationService.hasRole(user, "nobots"))
            throw new BotSystemBannedException();

        List<UserBotToken> botTokenList = botRepo.findAllByUser(user);

        if(botTokenList.size() >= MAX_BOT_PER_USER)
            throw new BotLimitReachedException();

        UserBotToken userBotToken = UserBotToken.builder()
                .user(user)
                .token(generateToken(20))
                .rateLimit(DEFAULT_RATE_LIMIT)
                .build();
        userBotToken = botRepo.save(userBotToken);

        return BotView.fromUserBotToken(userBotToken);
    }

    @Override
    public List<BotView> getBotTokens() {
        User user = authenticationService.authUser();

        if(authenticationService.hasRole(user, "nobots"))
            throw new BotSystemBannedException();

        List<UserBotToken> botTokenList = botRepo.findAllByUser(user);
        return botTokenList.stream().map(BotView::fromUserBotToken).toList();
    }

    @Override
    public void placePixel(String token, int canvasId, PlaceRequest placeRequest) {
        Optional<Canvas> canvasResp = canvasRepo.findById(canvasId);
        if (canvasResp.isEmpty())
            throw new CanvasNotFoundException(canvasId);

        Optional<UserBotToken> tokenResponse = botRepo.findByToken(token);
        if(tokenResponse.isEmpty())
            throw new InvalidTokenException();

        Canvas canvas = canvasResp.get();
        UserBotToken botToken = tokenResponse.get();

        if(punishmentService.isBanned(botToken.getUser()))
            throw new UserBannedException();

        if(authenticationService.hasRole(botToken.getUser(), "nobots"))
            throw new BotSystemBannedException();

        boardService.placePixel(botToken, canvas, placeRequest);
    }

    private String generateToken(int length){
        SecureRandom secureRandom = new SecureRandom();
        Base64.Encoder base64Encoder = Base64.getUrlEncoder();

        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);

        return base64Encoder.encodeToString(randomBytes);
    }

}
