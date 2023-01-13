package de.abstractolotl.azplace.service;

import de.abstractolotl.azplace.model.history.HistoryData;
import de.abstractolotl.azplace.model.history.PixelData;
import de.abstractolotl.azplace.model.logging.LoginLog;
import de.abstractolotl.azplace.model.logging.PixelLog;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    public List<PixelData> getPixels(int canvasId, long start, long end){
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withFilter(QueryBuilders.rangeQuery("timestamp")
                .from(start, true)
                .to(end, true))
                .withQuery(QueryBuilders.matchQuery("canvasId", canvasId))
                .build();

        SearchHits<PixelLog> logs = elasticsearchRestTemplate.search(searchQuery, PixelLog.class, IndexCoordinates.of("backend-pixel"));
        List<PixelData> pixels = new ArrayList<>();

        logs.forEach(pixelLogSearchHit -> {
            PixelLog pixelLog = pixelLogSearchHit.getContent();
            pixels.add(new PixelData(pixelLog.getX(), pixelLog.getY(), pixelLog.getColor()));
        });

        return pixels;
    }

    public long getPixelCount(int canvasId){
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withFilter(QueryBuilders.termQuery("canvasId", canvasId))
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

    @Deprecated
    public void createLogData (PixelLog log) {
        elasticsearchRestTemplate.index(new IndexQueryBuilder()
                .withId(log.getId())
                .withObject(log).build(),
                IndexCoordinates.of("backend-logs"));
    }

}
