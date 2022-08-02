package de.abstractolotl.azplace.scheduler;

import de.abstractolotl.azplace.repositories.SessionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class SessionCleaning {

    @Autowired
    private SessionRepo sessionRepo;

    public void deleteExpiredSessions(){
        sessionRepo.deleteAll(sessionRepo.findAllByExpireDateIsBefore(LocalDateTime.now()));
    }

}
