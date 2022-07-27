package de.abstractolotl.azplace.repositorys;

import de.abstractolotl.azplace.model.ColorPalette;
import org.springframework.data.repository.CrudRepository;

public interface PaletteRepo extends CrudRepository<ColorPalette, Integer> {
}
