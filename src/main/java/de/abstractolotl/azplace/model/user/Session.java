package de.abstractolotl.azplace.model.user;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Entity
@Table(schema = "session")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Session implements Serializable {

    public static Session createKey(int sessionExpiresIn, User user) {
        final LocalDateTime now = LocalDateTime.now();
        return Session.builder()
                .sessionKey(UUID.randomUUID().toString())
                .creationDate(now)
                .expireDate(now.plusMinutes(sessionExpiresIn))
                .user(user)
                .build();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @JsonProperty("session_key")
    private String sessionKey;

    @JsonProperty("creation_date")
    private LocalDateTime creationDate;

    @JsonProperty("expire_date")
    private LocalDateTime expireDate;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}