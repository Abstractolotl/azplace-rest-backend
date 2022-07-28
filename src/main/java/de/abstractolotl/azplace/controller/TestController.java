package de.abstractolotl.azplace.controller;

import de.abstractolotl.azplace.model.ColorPalette;
import de.abstractolotl.azplace.repositorys.CanvasRepo;
import de.abstractolotl.azplace.repositorys.PaletteRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired private CanvasRepo canvasRepo;
    @Autowired private PaletteRepo paletteRepo;

    @CrossOrigin(origins = {"*"})
    @GetMapping("/palette")
    private boolean createPalette() {
        Random r = new Random();
        ColorPalette palette = new ColorPalette();
        String[] colors = new String[16];
        for(int i = 0; i < colors.length; i++) {
            colors[i] = String.format("#%02x%02x%02x", r.nextInt(255), r.nextInt(255), r.nextInt(255));
        }

        palette.setHexColors(colors);
        paletteRepo.save(palette);
        return true;
    }

}
