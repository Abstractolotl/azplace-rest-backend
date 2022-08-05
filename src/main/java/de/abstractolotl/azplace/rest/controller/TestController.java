package de.abstractolotl.azplace.rest.controller;


import de.abstractolotl.azplace.model.user.User;
import de.abstractolotl.azplace.model.user.UserSession;
import de.abstractolotl.azplace.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@RestController
@RequestMapping("/test")
public class TestController {

    private static User testUser = User.builder()
            .firstName("Bobb")
            .lastName("Bebb")
            .insideNetIdentifier("stinkekäse")
            .roles("default")
            .timestampRegistered(System.currentTimeMillis())
            .build();

    private static User testUserAdmin = User.builder()
            .firstName("Super Bebb")
            .lastName("Super Bebb")
            .insideNetIdentifier("wurstkuchen")
            .roles("admin,statistics")
            .timestampRegistered(System.currentTimeMillis())
            .build();

    @Autowired private UserSession session;
    @Autowired private UserRepo userRepo;

    @PostConstruct
    private void init() {
        userRepo.findByInsideNetIdentifier(testUser.getInsideNetIdentifier()).ifPresentOrElse(user -> testUser = user,
                () -> testUser = userRepo.save(testUser));

        userRepo.findByInsideNetIdentifier(testUserAdmin.getInsideNetIdentifier()).ifPresentOrElse(user -> testUserAdmin = user,
                () -> testUserAdmin = userRepo.save(testUserAdmin));
    }

    @GetMapping("/login")
    public String testLogin() {
        session.setUser(testUser);
        return "logged in with Test User";
    }

    @GetMapping("/loginAdmin")
    public String testLoginAdmin() {
        session.setUser(testUserAdmin);
        return "logged in with Test Admin User";
    }

    @GetMapping("/session")
    public UserSession testSession() {
        UserSession session = new UserSession();
        session.setUser(this.session.getUser());
        return session;
    }

}
