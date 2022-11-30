package de.abstractolotl.azplace.repositories;

import de.abstractolotl.azplace.model.board.Canvas;
import de.abstractolotl.azplace.model.statistic.PixelOwner;
import de.abstractolotl.azplace.model.user.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PixelOwnerRepo extends CrudRepository<PixelOwner, Integer> {

    Optional<PixelOwner> findFirstByXAndYAndCanvas(int x, int y, Canvas canvas);

    Iterable<PixelOwner> findAllByCanvas(Canvas canvas);

    List<PixelOwner> findAllByCanvasAndUser(Canvas canvas, User user);

    List<PixelOwner> findAllByCanvasAndUserAndTimestampGreaterThan(Canvas canvas, User user, long timestamp);

    void deleteByXAndYAndCanvas(int x, int y, Canvas canvas);

}
