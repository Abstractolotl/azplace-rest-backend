package de.abstractolotl.azplace.repositories;

import de.abstractolotl.azplace.model.board.Canvas;
import de.abstractolotl.azplace.model.user.User;
import de.abstractolotl.azplace.model.user.UserCooldown;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CooldownRepo extends CrudRepository<UserCooldown, Long> {

    boolean existsByUserAndCanvasAndLastPixelTimestampBefore(User user, Canvas canvas, long lastPixelTimestamp);

    Optional<UserCooldown> findByUserAndCanvas(User user, Canvas canvas);

}
