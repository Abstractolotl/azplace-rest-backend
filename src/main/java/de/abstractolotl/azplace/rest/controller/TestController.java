package de.abstractolotl.azplace.rest.controller;


import de.abstractolotl.azplace.model.user.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired private UserSession session;

    @GetMapping("/session-set")
    private void setDataInSession(){
        session.setSessionKey("Bobbert");
    }

    @GetMapping("/session-get")
    private String getDataInSession() {
        return session;
    }

}
