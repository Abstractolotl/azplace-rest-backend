package de.abstractolotl.azplace.model.canvas;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Min;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="canvas")
public class Canvas {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;
    @JsonProperty("redis_key")
    private String redisKey;

    @ManyToOne
    @JoinColumn(name = "palette_id", nullable = false)
    @JsonProperty("color_palette")
    private ColorPalette colorPalette;

    @Min(1L)
    @JsonProperty("start_date")
    private long startDate;
    @Min(1L)
    private long duration;

    @Min(0L)
    private long cooldown;

    @Min(1L)
    private int width;
    @Min(1L)
    private int height;

}

