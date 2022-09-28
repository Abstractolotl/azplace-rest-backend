package de.abstractolotl.azplace.rest.controller;

import de.abstractolotl.azplace.model.user.UserRoles;
import de.abstractolotl.azplace.repositories.CanvasRepo;
import de.abstractolotl.azplace.rest.api.PunishmentAPI;
import de.abstractolotl.azplace.model.requests.BanRequest;
import de.abstractolotl.azplace.model.user.User;
import de.abstractolotl.azplace.model.user.UserBan;
import de.abstractolotl.azplace.repositories.BanRepo;
import de.abstractolotl.azplace.repositories.UserRepo;
import de.abstractolotl.azplace.service.AuthenticationService;
import de.abstractolotl.azplace.service.ResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
public class PunishmentController implements PunishmentAPI {

    @Autowired private AuthenticationService authService;
    @Autowired private ResetService resetService;

    @Autowired private BanRepo banRepo;
    @Autowired private UserRepo userRepo;
    @Autowired private CanvasRepo canvasRepo;

    @Override
    public UserBan banUser(BanRequest banRequest) {
        authService.authUserWithRole(UserRoles.ADMIN);

        Optional<User> userOptional = userRepo.findById(banRequest.getUserId());
        if(userOptional.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exists");

        long end = -1L;
        if(banRequest.getTime() != -1L)
            end = System.currentTimeMillis() + banRequest.getTime();

        if(banRequest.isResetPixels()) {
            canvasRepo.findAll().forEach(canvas -> {
                resetService.resetPixels(canvas, userOptional.get());
            });
        }

        return banRepo.save(UserBan.builder()
                .created(System.currentTimeMillis())
                .end(end)
                .reason(banRequest.getReason())
                .user(userOptional.get())
                .build());
    }

    @Override
    public UserBan pardon(Long banId) {
        authService.authUserWithRole(UserRoles.ADMIN);

        Optional<UserBan> userBanOptional = banRepo.findById(banId);
        if(userBanOptional.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User is currently not banned");

        UserBan userBan = userBanOptional.get();
        userBan.setPardoned(true);

        return banRepo.save(userBan);
    }

}
