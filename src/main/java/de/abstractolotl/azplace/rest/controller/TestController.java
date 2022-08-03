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

    private final static User testUser = User.builder()
            .firstName("Bobb")
            .lastName("Bebb")
            .insideNetIdentifier("stinkekäse")
            .roles("admin")
            .timestampRegistered(System.currentTimeMillis())
            .build();



    @Autowired private UserSession session;
    @Autowired private UserRepo userRepo;


    @PostConstruct
    private void init() {
        userRepo.save(testUser);
    }

    @GetMapping("/login")
    public String testLogin() {
        session.setUser(testUser);

        return "logged in with Test User";
    }

}
