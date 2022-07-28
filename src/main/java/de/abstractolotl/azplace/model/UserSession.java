package de.abstractolotl.azplace.model;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import lombok.Data;

@Data
@Component
@SessionScope
public class UserSession {

    private Session session;

}