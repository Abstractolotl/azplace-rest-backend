package de.abstractolotl.azplace.service;

import de.abstractolotl.azplace.model.logging.LogData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Service;

@Service
public class ElasticService {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    public void createLogData (LogData log) {
        elasticsearchRestTemplate.index(new IndexQueryBuilder()
                .withId(log.getId())
                .withObject(log).build(), IndexCoordinates.of("backend-logs"));
    }

}
