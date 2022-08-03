package de.abstractolotl.azplace.model.user;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.io.Serial;
import java.io.Serializable;

@Component
@SessionScope
public class UserSession {

    @Serial
    private static final long serialVersionUID = 7156526077883281623L;

    private String sessionKey;
    private String expirationDate;

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

}
