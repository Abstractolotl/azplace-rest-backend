package de.abstractolotl.azplace.rest.controller;

import de.abstractolotl.azplace.model.user.User;
import de.abstractolotl.azplace.model.view.CountView;
import de.abstractolotl.azplace.model.view.TopStatisticView;
import de.abstractolotl.azplace.repositories.PixelOwnerRepo;
import de.abstractolotl.azplace.rest.api.StatisticsAPI;
import de.abstractolotl.azplace.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Set;

@RestController
public class StatisticsController implements StatisticsAPI {

    @Autowired private PixelOwnerRepo pixelOwnerRepo;

    @Autowired private AuthenticationService authenticationService;
    @Autowired private Jedis jedis;

    private static final long TIME_FRAME = 86400000L;

    @Override
    public CountView pixels() {
        authenticationService.authUserWithRole("statistics");

        long end = System.currentTimeMillis();
        long start = end - TIME_FRAME;

        return new CountView(pixelOwnerRepo.countAllByTimestampBetween(start, end));
    }

    @Override
    public TopStatisticView topList(int max) {
        authenticationService.authUserWithRole("statistics");

        long end = System.currentTimeMillis();
        long start = end - TIME_FRAME;

        List<User> topList = pixelOwnerRepo.findTopList(max, start, end);
        return new TopStatisticView(topList.size(), topList);
    }

    @Override
    public CountView online() {
        authenticationService.authUserWithRole("statistics");

        Set<String> keys = jedis.keys("spring:session:sessions:expires");
        return new CountView(keys.size());
    }
}
