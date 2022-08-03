package de.abstractolotl.azplace.repositories;

import de.abstractolotl.azplace.model.board.Canvas;
import de.abstractolotl.azplace.model.statistic.PixelOwner;
import de.abstractolotl.azplace.model.user.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PixelOwnerRepo extends CrudRepository<PixelOwner, Integer> {

    Optional<PixelOwner> findByXAndY(int x, int y);

    Iterable<PixelOwner> findAllByCanvas(Canvas canvas);

    long countAllByTimestampBetween(long start, long end);

    @Query(value = "SELECT user.id, user.first_name, user.inside_net_identifier, user.last_name, user.roles, user.timestamp_registered FROM user,pixel_owner as po WHERE user.id = po.user_id AND po.timestamp BETWEEN :start AND :end group by po.user_id ORDER BY COUNT(*) LIMIT :amount", nativeQuery = true)
    List<User> findTopList(int amount, long start, long end);

}
