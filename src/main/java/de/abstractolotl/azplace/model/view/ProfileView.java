package de.abstractolotl.azplace.model.view;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.abstractolotl.azplace.model.user.User;
import de.abstractolotl.azplace.model.user.UserSettings;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@Builder
public class ProfileView {

    private String name;

    @JsonProperty("person_id")
    private String personId;

    @JsonProperty("timestamp_registered")
    private long timestampRegistered;

    private List<String> roles;

    @JsonProperty("user_settings")
    private UserSettings userSettings;

    public static ProfileView fromUser(User user) {
        return ProfileView.builder()
                .personId(user.getInsideNetIdentifier())
                .roles(Arrays.asList(user.getRoleArray()))
                .userSettings(new UserSettings(user.getRoles().contains("anonymous")))
                .name(user.getFirstName() + " " + user.getLastName())
                .timestampRegistered(user.getTimestampRegistered())
                .build();
    }

}
