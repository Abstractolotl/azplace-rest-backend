package de.abstractolotl.azplace.model.utility;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import de.abstractolotl.azplace.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder @Data
@AllArgsConstructor @NoArgsConstructor
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

    public static AuthUser fromOauthResponse(JsonNode node) {
        return AuthUser.builder()
                .firstName(node.get("given_name").asText())
                .lastName(node.get("family_name").asText())
                .insideNetIdentifier(node.get("preferred_username").asText())
                .build();
    }

}
