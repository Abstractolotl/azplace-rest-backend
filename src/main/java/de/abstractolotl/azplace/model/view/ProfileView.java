package de.abstractolotl.azplace.model.view;

import de.abstractolotl.azplace.model.user.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileView {

    private String name;
    private long timestampRegistered;

    public static ProfileView fromUser(User user) {
        return ProfileView.builder()
                .name(user.getFirstName() + " " + user.getLastName())
                .timestampRegistered(user.getTimestampRegistered())
                .build();
    }

}
