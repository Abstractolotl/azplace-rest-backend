package de.abstractolotl.azplace.model.view;

import de.abstractolotl.azplace.model.user.UserBotToken;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BotView {

    private Integer rateLimit;
    private String token;

    public static BotView fromUserBotToken(UserBotToken userBotToken){
        return BotView.builder()
                .token(userBotToken.getToken())
                .rateLimit(userBotToken.getRateLimit())
                .build();
    }

}
