package de.abstractolotl.azplace.service;

import de.abstractolotl.azplace.model.logging.LoginLog;
import de.abstractolotl.azplace.model.logging.PixelLog;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class ElasticService {

    @Autowired private ElasticsearchRestTemplate elasticsearchRestTemplate;

    public long getLogs(long start, long end){
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withFilter(QueryBuilders.rangeQuery("timestamp")
                .from(start, true)
                .to(end, true))
                .build();

        return elasticsearchRestTemplate.count(searchQuery,
                IndexCoordinates.of("backend-pixel"));
    }

    public void logPixel(Integer canvasId, Integer userId, int x, int y, int color){
        logPixel(canvasId, userId, x, y, color, false);
    }

    public void logPixel(Integer canvasId, Integer userId, int x, int y, int color, boolean bot){
        PixelLog pixelLog = PixelLog.builder()
                .timestamp(LocalDateTime.now())
                .canvasId(canvasId).userId(userId)
                .x(x).y(y).bot(bot).color(color)
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
                .withObject(log).build(),
                IndexCoordinates.of("backend-logs"));
    }

}
