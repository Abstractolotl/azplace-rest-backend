package de.abstractolotl.azplace.repositories;

import de.abstractolotl.azplace.model.user.UserBan;
import org.springframework.data.repository.CrudRepository;

public interface BanRepo extends CrudRepository<UserBan, Long> {

}
