package de.abstractolotl.azplace.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name="pixel_owner")
public class PixelOwner {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;
    @ManyToOne()
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne
    @JoinColumn(name = "canvas_id", nullable = false)
    private Canvas canvas;
    private int x;
    private int y;
    private long timestamp;

}

