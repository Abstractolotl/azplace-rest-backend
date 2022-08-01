package de.abstractolotl.azplace.repositories;

import java.util.List;

import de.abstractolotl.azplace.model.user.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepo extends CrudRepository<User, Integer> {

    List<User> findAllByInsideNetIdentifier(String insideNetIdentifier);
}