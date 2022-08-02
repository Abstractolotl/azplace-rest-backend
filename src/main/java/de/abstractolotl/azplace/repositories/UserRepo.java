package de.abstractolotl.azplace.repositories;


import de.abstractolotl.azplace.model.user.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepo extends CrudRepository<User, Integer> {

    boolean existsByInsideNetIdentifier(String insideNetIdentifier);

    User findByInsideNetIdentifier(String insideNetIdentifier);

}