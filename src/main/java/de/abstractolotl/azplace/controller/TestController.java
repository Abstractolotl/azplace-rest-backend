package de.abstractolotl.azplace.controller;

import de.abstractolotl.azplace.model.ColorPalette;
import de.abstractolotl.azplace.model.logging.LogData;
import de.abstractolotl.azplace.repositorys.CanvasRepo;
import de.abstractolotl.azplace.repositorys.PaletteRepo;
import de.abstractolotl.azplace.service.ElasticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private ElasticService service;

    @Autowired
    private CanvasRepo canvasRepo;
    @Autowired
    private PaletteRepo paletteRepo;

    @GetMapping("/palette")
    private boolean createPalette() {
        service.createLogData(LogData.builder()
                .message("Palette requested").controller("TestController")
                .id(UUID.randomUUID().toString()).timestamp(LocalDateTime.now())
                .build());

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
