package de.abstractolotl.azplace.model;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Table(schema = "session")
@Data
@RequiredArgsConstructor
public class Session {

    public Session(String sessionKey,
                   LocalDateTime creationDate,
                   LocalDateTime expireDate,
                   User user) {
        this.sessionKey = sessionKey;
        this.creationDate = creationDate;
        this.expireDate = expireDate;
        this.user = user;
    }

    public static Session createKey(int sessionExpiresIn, User user) {
        final LocalDateTime now = LocalDateTime.now();
        return new Session(UUID.randomUUID().toString(),
                           now,
                           now.plusMinutes(sessionExpiresIn),
                           user);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int           id;
    private String        sessionKey;
    private LocalDateTime creationDate;
    private LocalDateTime expireDate;
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User          user;
}