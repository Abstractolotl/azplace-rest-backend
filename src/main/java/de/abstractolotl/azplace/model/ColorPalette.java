package de.abstractolotl.azplace.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="palette")
@Data
public class ColorPalette {

    @Id private int id;
    private int[] hexColors;

}
