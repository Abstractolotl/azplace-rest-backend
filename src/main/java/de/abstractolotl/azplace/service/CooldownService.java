package de.abstractolotl.azplace.service;

import de.abstractolotl.azplace.model.board.Canvas;
import de.abstractolotl.azplace.model.user.User;
import de.abstractolotl.azplace.model.user.UserCooldown;
import de.abstractolotl.azplace.repositories.CooldownRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CooldownService {

    @Autowired
    private CooldownRepo cooldownRepo;

    public boolean isOnCooldown(User user, Canvas canvas){
        Optional<UserCooldown> cooldownResponse = cooldownRepo.findByUserAndCanvas(user, canvas);

        if(cooldownResponse.isEmpty())
            return false;

        return System.currentTimeMillis() < (cooldownResponse.get().getLastPixelTimestamp() + canvas.getCooldown());
    }

    public long getLastPixelTimestamp(User user, Canvas canvas){
        Optional<UserCooldown> userCooldownOptional = cooldownRepo.findByUserAndCanvas(user, canvas);

        if(userCooldownOptional.isEmpty())
            return 0L;

        return userCooldownOptional.get().getLastPixelTimestamp();
    }

    public void reset(User user, Canvas canvas){
        Optional<UserCooldown> userCooldownOptional = cooldownRepo.findByUserAndCanvas(user, canvas);

        userCooldownOptional.ifPresentOrElse(userCooldown -> {
            userCooldown.setLastPixelTimestamp(System.currentTimeMillis());
            cooldownRepo.save(userCooldown);
        }, () -> {
            UserCooldown userCooldown = UserCooldown.builder()
                    .user(user)
                    .canvas(canvas)
                    .lastPixelTimestamp(System.currentTimeMillis())
                    .build();
            cooldownRepo.save(userCooldown);
        });
    }

}
