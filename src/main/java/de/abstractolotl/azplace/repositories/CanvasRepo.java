package de.abstractolotl.azplace.repositories;

import de.abstractolotl.azplace.model.board.Canvas;
import de.abstractolotl.azplace.model.board.ColorPalette;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CanvasRepo extends CrudRepository<Canvas, Integer> {

}
