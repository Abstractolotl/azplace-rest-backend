package de.abstractolotl.azplace.service;

import de.abstractolotl.azplace.model.user.User;
import de.abstractolotl.azplace.model.user.UserBan;
import de.abstractolotl.azplace.repositories.BanRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PunishmentService {

    @Autowired
    private BanRepo banRepo;

    public boolean isBanned(User user){
        if(!banRepo.existsByUserAndPardonedIsFalse(user))
            return false;

        UserBan userBan = banRepo.findByUserAndPardonedIsFalse(user);

        if(System.currentTimeMillis() >= userBan.getEnd()){
            userBan.setPardoned(true);
            banRepo.save(userBan);
            return false;
        }

        return true;
    }

}
