package de.abstractolotl.azplace.repositories;


import de.abstractolotl.azplace.model.user.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepo extends CrudRepository<User, Integer> {

    List<User> findAllByInsideNetIdentifier(String insideNetIdentifier);
}