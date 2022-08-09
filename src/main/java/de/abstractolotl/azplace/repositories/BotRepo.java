package de.abstractolotl.azplace.repositories;

import de.abstractolotl.azplace.model.user.User;
import de.abstractolotl.azplace.model.user.UserBotToken;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface BotRepo extends CrudRepository<UserBotToken, Long> {

    List<UserBotToken> findAllByUser(User user);

    Optional<UserBotToken> findByToken(String token);

}
