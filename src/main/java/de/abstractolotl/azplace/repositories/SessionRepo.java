package de.abstractolotl.azplace.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import de.abstractolotl.azplace.model.Session;

public interface SessionRepo extends CrudRepository<Session, Integer>  {

    List<Session> findSessionBySessionKey(String sessionKey);
}