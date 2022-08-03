package de.abstractolotl.azplace.model.user;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.io.Serial;
import java.io.Serializable;

@Component
@SessionScope
@Data
public class UserSession implements Serializable {

    @Serial
    private static final long serialVersionUID = 7156526077883281623L;

    private User user;

}
