package de.abstractolotl.azplace.model.user;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import lombok.Data;

import java.io.Serializable;

@Data
@Component
@SessionScope
public class UserSession implements Serializable {

    private Session session;

}