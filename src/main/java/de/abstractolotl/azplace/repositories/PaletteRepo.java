package de.abstractolotl.azplace.repositories;

import de.abstractolotl.azplace.model.board.ColorPalette;
import org.springframework.data.repository.CrudRepository;

public interface PaletteRepo extends CrudRepository<ColorPalette, Integer> {
}
