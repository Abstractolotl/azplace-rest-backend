package de.abstractolotl.azplace.repositorys;

import de.abstractolotl.azplace.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepo extends CrudRepository<User, Integer> {
}
