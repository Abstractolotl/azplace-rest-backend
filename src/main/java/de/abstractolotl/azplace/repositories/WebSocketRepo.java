package de.abstractolotl.azplace.repositories;

import de.abstractolotl.azplace.model.utility.WebSocketServerInfo;
import org.springframework.data.repository.CrudRepository;

public interface WebSocketRepo extends CrudRepository<WebSocketServerInfo, Integer> {

}
