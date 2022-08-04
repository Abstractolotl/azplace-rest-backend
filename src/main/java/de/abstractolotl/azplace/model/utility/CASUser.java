package de.abstractolotl.azplace.model.utility;

import de.abstractolotl.azplace.model.user.User;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CASUser {

    private String firstName;
    private String lastName;
    private String insideNetIdentifier;

    public User createUser() {
        return User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .insideNetIdentifier(insideNetIdentifier)
                .timestampRegistered(System.currentTimeMillis())
                .build();
    }

}
