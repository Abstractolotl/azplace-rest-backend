package de.abstractolotl.azplace.repositories;

import de.abstractolotl.azplace.model.user.User;
import de.abstractolotl.azplace.model.user.UserBotToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface BotRepo extends CrudRepository<UserBotToken, Long> {

    Optional<UserBotToken> findByUser(User user);

    Optional<UserBotToken> findByToken(String token);

}
