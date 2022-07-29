package de.abstractolotl.azplace.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name="canvas")
public class Canvas {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name = "palette_id", nullable = false)
    private ColorPalette colorPalette;
    private long startData;
    private long duration;
    private String redisKey;
    private int width;
    private int height;

}

