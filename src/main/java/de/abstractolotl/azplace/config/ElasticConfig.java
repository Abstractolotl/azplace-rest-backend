package de.abstractolotl.azplace.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

import javax.net.ssl.SSLContext;

@Configuration
@ComponentScan
public class ElasticConfig extends AbstractElasticsearchConfiguration {

    @Value("${spring.elasticsearch.host}")
    public String elasticsearchUrl;
    @Value("${spring.elasticsearch.port}")
    public String elasticsearchPort;
    @Value("${spring.elasticsearch.username}")
    public String elasticsearchUsername;
    @Value("${spring.elasticsearch.password}")
    public String elasticsearchPassword;

    private SSLConfig sslConfig;

    public ElasticConfig() throws Exception {
        sslConfig = new SSLConfig();
    }

    @Override
    public RestHighLevelClient elasticsearchClient() {
        SSLContext sslContext = null;
        try {
            sslContext = sslConfig.getSSLContext();
        } catch (Exception e) {
            e.printStackTrace();
        }

        final ClientConfiguration config = ClientConfiguration.builder()
                .connectedTo(elasticsearchUrl + ":" + elasticsearchPort)
                .usingSsl(sslContext)
                .withBasicAuth(elasticsearchUsername, elasticsearchPassword)
                .build();
        return RestClients.create(config).rest();
    }

    @Bean
    ElasticsearchRestTemplate elasticsearchRestTemplate() {
        return new ElasticsearchRestTemplate(elasticsearchClient());
    }
}
