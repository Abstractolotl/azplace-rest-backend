package de.abstractolotl.azplace.repositories;

import org.springframework.data.repository.CrudRepository;

import de.abstractolotl.azplace.model.Session;

public interface SessionRepo extends CrudRepository<Session, Integer>  {
}