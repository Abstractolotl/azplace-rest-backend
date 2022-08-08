package de.abstractolotl.azplace.repositories;

import de.abstractolotl.azplace.model.board.Canvas;
import de.abstractolotl.azplace.model.user.User;
import de.abstractolotl.azplace.model.user.UserCooldown;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
public interface CooldownRepo extends CrudRepository<UserCooldown, Long> {

    @Transactional
    Optional<UserCooldown> findByUserAndCanvas(User user, Canvas canvas);

}
