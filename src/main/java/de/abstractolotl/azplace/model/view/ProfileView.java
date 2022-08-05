package de.abstractolotl.azplace.model.view;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.abstractolotl.azplace.model.user.User;
import de.abstractolotl.azplace.model.user.UserSettings;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileView {

    private String name;

    @JsonProperty("timestamp_registered")
    private long timestampRegistered;

    @JsonProperty("user_settings")
    private UserSettings userSettings;

    public static ProfileView fromUser(User user) {
        return ProfileView.builder()
                .userSettings(new UserSettings(user.getRoles().contains("anonymous")))
                .name(user.getFirstName() + " " + user.getLastName())
                .timestampRegistered(user.getTimestampRegistered())
                .build();
    }

}
