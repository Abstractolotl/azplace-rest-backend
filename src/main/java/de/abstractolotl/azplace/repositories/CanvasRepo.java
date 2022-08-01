package de.abstractolotl.azplace.repositories;

import de.abstractolotl.azplace.model.canvas.Canvas;
import de.abstractolotl.azplace.model.canvas.ColorPalette;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CanvasRepo extends CrudRepository<Canvas, Integer> {

    List<Canvas> findAllByColorPalette(ColorPalette colorPalette);

}
