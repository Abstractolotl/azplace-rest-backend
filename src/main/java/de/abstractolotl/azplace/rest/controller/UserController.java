package de.abstractolotl.azplace.rest.controller;

import de.abstractolotl.azplace.model.user.User;
import de.abstractolotl.azplace.model.view.ProfileView;
import de.abstractolotl.azplace.rest.api.UserAPI;
import de.abstractolotl.azplace.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = { "http://localhost:8080" }, allowCredentials = "true")
public class UserController implements UserAPI {

    @Autowired private AuthenticationService authService;

    @Override
    public ProfileView profile(){
        User user = authService.authUser();
        return ProfileView.fromUser(user);
    }

}
