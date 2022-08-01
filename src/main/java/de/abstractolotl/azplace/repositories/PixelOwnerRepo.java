package de.abstractolotl.azplace.repositories;

import de.abstractolotl.azplace.model.canvas.Canvas;
import de.abstractolotl.azplace.model.logging.PixelOwner;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PixelOwnerRepo extends CrudRepository<PixelOwner, Integer> {

    Optional<PixelOwner> findByXAndY(int x, int y);

    Iterable<PixelOwner> findAllByCanvas(Canvas canvas);

}
