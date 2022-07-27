package de.abstractolotl.azplace.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name="palette")
@Data
public class ColorPalette {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;
    private String[] hexColors;

}
