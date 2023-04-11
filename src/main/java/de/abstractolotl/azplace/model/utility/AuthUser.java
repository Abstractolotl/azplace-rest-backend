package de.abstractolotl.azplace.model.utility;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.abstractolotl.azplace.model.user.User;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthUser {

    @JsonProperty("given_name")
    private String firstName;
    @JsonProperty("family_name")
    private String lastName;
    @JsonProperty("preferred_username")
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
