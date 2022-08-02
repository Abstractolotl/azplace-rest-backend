package de.abstractolotl.azplace.rest.controller;

import de.abstractolotl.azplace.exceptions.MissingRoleException;
import de.abstractolotl.azplace.model.user.User;
import de.abstractolotl.azplace.model.view.PixelStatisticView;
import de.abstractolotl.azplace.model.view.TopStatisticView;
import de.abstractolotl.azplace.repositories.PixelOwnerRepo;
import de.abstractolotl.azplace.rest.api.StatisticsAPI;
import de.abstractolotl.azplace.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StatisticsController implements StatisticsAPI {

    @Autowired
    private PixelOwnerRepo pixelOwnerRepo;

    @Autowired
    private AuthenticationService authenticationService;

    @Override
    public PixelStatisticView pixels() {
        if(!authenticationService.hasRole("statistics"))
            throw new MissingRoleException();

        long end = System.currentTimeMillis();
        long start = end - 86400000;

        return new PixelStatisticView(pixelOwnerRepo.countAllByTimestampBetween(start, end));
    }

    @Override
    public TopStatisticView topList(int max) {
        /*
        if(!authenticationService.hasRole("statistics"))
            throw new MissingRoleException();
         */

        List<User> topList = pixelOwnerRepo.findTopList(max);

        return new TopStatisticView(topList.size(), topList);
    }

}
