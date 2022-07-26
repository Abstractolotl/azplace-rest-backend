package de.abstractolotl.azplace.model;

import lombok.Data;

import javax.persistence.*;
import java.sql.Blob;

@Entity
@Data
@Table(name="canvas")
public class Canvas {

    @Id
    private int id;
    @ManyToOne
    @JoinColumn(name = "palette_id", nullable = false)
    private ColorPalette colorPalette;
    private long startData;
    private long duration;
    //@Lob private Blob boardData;
    private String redisKey;
    private int width;
    private int height;

}

