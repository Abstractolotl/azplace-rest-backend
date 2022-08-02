package de.abstractolotl.azplace.config;

import de.abstractolotl.azplace.scheduler.SessionCleaning;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SchedulerConfig {

    @Autowired
    private SessionCleaning sessionCleaning;

    @Scheduled(initialDelayString = "${cleaning.initialDelay:15000}",
            fixedRateString = "${cleaning.fixedRate:15000}")
    public void cleanSessions(){ sessionCleaning.deleteExpiredSessions(); }

}
