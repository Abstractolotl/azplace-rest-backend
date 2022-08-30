package de.abstractolotl.azplace.rest.controller;

import de.abstractolotl.azplace.model.user.UserRoles;
import de.abstractolotl.azplace.model.view.CountView;
import de.abstractolotl.azplace.rest.api.StatisticsAPI;
import de.abstractolotl.azplace.service.AuthenticationService;
import de.abstractolotl.azplace.service.ElasticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
public class StatisticsController implements StatisticsAPI {

    @Autowired private ElasticService elasticService;

    @Autowired private AuthenticationService authenticationService;
    @Autowired private RedisTemplate<byte[], byte[]> redis;

    private static final long TIME_FRAME = 86_400_000L;

    @Override
    public CountView pixels() {
        authenticationService.authUserWithRole(UserRoles.STATISTICS);

        long end = System.currentTimeMillis();
        long start = end - TIME_FRAME;

        return new CountView(elasticService.getLogs(start, end));
    }

    @Override
    public CountView online() {
        authenticationService.authUserWithRole(UserRoles.STATISTICS);

        Set<?> keys = redis.keys("spring:session:sessions:expires:*".getBytes());
        return new CountView(keys.size());
    }
}
