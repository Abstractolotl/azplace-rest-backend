package de.abstractolotl.azplace.repositories;

import de.abstractolotl.azplace.model.board.Canvas;
import de.abstractolotl.azplace.model.statistic.PixelOwner;
import de.abstractolotl.azplace.model.user.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PixelOwnerRepo extends CrudRepository<PixelOwner, Integer> {

    Optional<PixelOwner> findByXAndYAndCanvas(int x, int y, Canvas canvas);

    Iterable<PixelOwner> findAllByCanvas(Canvas canvas);

    void deleteByXAndYAndCanvas(int x, int y, Canvas canvas);

}
