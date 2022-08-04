package de.abstractolotl.azplace.rest.controller;

import de.abstractolotl.azplace.model.user.User;
import de.abstractolotl.azplace.model.view.CountView;
import de.abstractolotl.azplace.model.view.TopStatisticView;
import de.abstractolotl.azplace.repositories.PixelOwnerRepo;
import de.abstractolotl.azplace.rest.api.StatisticsAPI;
import de.abstractolotl.azplace.service.AuthenticationService;
import de.abstractolotl.azplace.service.ElasticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

@RestController
public class StatisticsController implements StatisticsAPI {

    @Autowired private PixelOwnerRepo pixelOwnerRepo;
    @Autowired private ElasticService elasticService;

    @Autowired private AuthenticationService authenticationService;
    @Autowired private Jedis jedis;

    private static final long TIME_FRAME = 86_400_000L;

    @Override
    public CountView pixels() {
        authenticationService.authUserWithRole("statistics");

        long end = System.currentTimeMillis();
        long start = end - TIME_FRAME;

        return new CountView(elasticService.getLogs(start, end));
    }

    @Override
    public CountView online() {
        authenticationService.authUserWithRole("statistics");

        Set<String> keys = jedis.keys("spring:session:sessions:expires:*");
        return new CountView(keys.size());
    }
}
