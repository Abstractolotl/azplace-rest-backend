package de.abstractolotl.azplace.repositories;


import de.abstractolotl.azplace.model.user.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepo extends CrudRepository<User, Integer> {

    boolean existsByInsideNetIdentifier(String insideNetIdentifier);

    Optional<User> findByInsideNetIdentifier(String insideNetIdentifier);

}