package de.abstractolotl.azplace.model.user;

import de.abstractolotl.azplace.model.board.Canvas;
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
@Table(name="user_cooldown")
public class UserCooldown {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "canvas_id", nullable = false)
    private Canvas canvas;

    @Builder.Default
    @Column(name = "last_pixel_timestamp")
    private long lastPixelTimestamp = 0L;

}
