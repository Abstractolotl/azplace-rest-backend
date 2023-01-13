package de.abstractolotl.azplace.rest.controller;

import de.abstractolotl.azplace.exceptions.board.CanvasNotFoundException;
import de.abstractolotl.azplace.model.board.Canvas;
import de.abstractolotl.azplace.model.history.HistoryData;
import de.abstractolotl.azplace.model.history.HistoryInfo;
import de.abstractolotl.azplace.model.requests.HistoryRequest;
import de.abstractolotl.azplace.repositories.CanvasRepo;
import de.abstractolotl.azplace.rest.api.HistoryAPI;
import de.abstractolotl.azplace.service.ElasticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class HistoryController implements HistoryAPI {

    @Autowired private CanvasRepo canvasRepo;
    @Autowired private ElasticService elasticService;

    @Override
    public HistoryInfo getHistoryInfo(int boardId) {
        Canvas canvas = getCanvasById(boardId);

        long endTime = System.currentTimeMillis();
        if(canvas.getStartDate() + canvas.getDuration() < endTime) {
            endTime = canvas.getStartDate() + canvas.getDuration();
        }

        return HistoryInfo.builder()
                .startTime(canvas.getStartDate())
                .endTime(endTime)
                .pixelCount(elasticService.getPixelCount(boardId))
                .build();
    }

    @Override
    public HistoryData getHistory(int boardId, HistoryRequest request) {
        Canvas canvas = getCanvasById(boardId);

        long endTime = System.currentTimeMillis();
        if(canvas.getStartDate() + canvas.getDuration() < endTime) {
            endTime = canvas.getStartDate() + canvas.getDuration();
        }

        if(request.getStartTime() < canvas.getStartDate() || request.getEndTime() > endTime){
            throw new IllegalArgumentException("Invalid time range");
        }

        return HistoryData.builder()
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .pixels(elasticService.getPixels(boardId, request.getStartTime(), request.getEndTime()))
                .build();
    }

    private Canvas getCanvasById(int canvasId){
        Optional<Canvas> optionalCanvas = canvasRepo.findById(canvasId);
        if (optionalCanvas.isEmpty()) {
            throw new CanvasNotFoundException(canvasId);
        }

        return optionalCanvas.get();
    }
}
