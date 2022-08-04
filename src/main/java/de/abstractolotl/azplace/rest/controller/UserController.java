package de.abstractolotl.azplace.rest.controller;

import de.abstractolotl.azplace.model.user.User;
import de.abstractolotl.azplace.model.user.UserSettings;
import de.abstractolotl.azplace.model.view.ProfileView;
import de.abstractolotl.azplace.repositories.UserRepo;
import de.abstractolotl.azplace.rest.api.UserAPI;
import de.abstractolotl.azplace.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

@RestController
public class UserController implements UserAPI {

    @Autowired private AuthenticationService authService;
    @Autowired private UserRepo userRepo;

    @Override
    public ProfileView profile(){
        User user = authService.authUser();
        return ProfileView.fromUser(user);
    }

    @Override
    public void settings(UserSettings userSettings) {
        User user = authService.authUser();

        if(userSettings.isAnonymize() && !authService.hasRole(user, "anonymous")){
            List<String> roles = Arrays.asList(user.getRoleArray());

            roles.add("anonymous");
            user.setRoles(String.join(",", roles));

            userRepo.save(user);
        }else if(!userSettings.isAnonymize() && authService.hasRole(user, "anonymous")){
            List<String> roles = Arrays.asList(user.getRoleArray());

            roles.remove("anonymous");
            user.setRoles(String.join(",", roles));

            userRepo.save(user);
        }
    }
}
