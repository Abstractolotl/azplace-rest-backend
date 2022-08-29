package de.abstractolotl.azplace.model.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="user_token")
public class UserBotToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne()
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(unique = true)
    private String token;

    @Builder.Default
    private Long registered = System.currentTimeMillis();

    private Integer rateLimit;

}
