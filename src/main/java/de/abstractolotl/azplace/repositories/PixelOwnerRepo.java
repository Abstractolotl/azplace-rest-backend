package de.abstractolotl.azplace.repositories;

import de.abstractolotl.azplace.model.PixelOwner;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PixelOwnerRepo extends CrudRepository<PixelOwner, Integer> {

    Optional<PixelOwner> findByXAndY(int x, int y);

}
