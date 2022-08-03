package de.abstractolotl.azplace.service;

import de.abstractolotl.azplace.model.logging.LoginLog;
import de.abstractolotl.azplace.model.logging.PixelLog;
import de.abstractolotl.azplace.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ElasticService {

    @Autowired private ElasticsearchRestTemplate elasticsearchRestTemplate;

    public void logPixel(Integer canvasId, int x, int y, byte color){
        PixelLog pixelLog = PixelLog.builder()
                .timestamp(LocalDateTime.now())
                .canvasId(canvasId)
                .position(new Integer[]{x, y})
                .color(color)
                .build();
        elasticsearchRestTemplate.index(new IndexQueryBuilder()
                .withId(pixelLog.getId())
                .withObject(pixelLog).build(),
                IndexCoordinates.of("backend-pixel"));
    }

    public void logLogin(){
        LoginLog loginLog = LoginLog.builder()
                .timestamp(LocalDateTime.now())
                .build();
        elasticsearchRestTemplate.index(new IndexQueryBuilder()
                        .withId(loginLog.getId())
                        .withObject(loginLog).build(),
                IndexCoordinates.of("backend-login"));
    }

    public void createLogData (PixelLog log) {
        elasticsearchRestTemplate.index(new IndexQueryBuilder()
                .withId(log.getId())
                .withObject(log).build(), IndexCoordinates.of("backend-logs"));
    }

}
