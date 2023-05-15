package de.abstractolotl.azplace.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

@Configuration
@ComponentScan
public class ElasticConfig extends AbstractElasticsearchConfiguration {

    @Value("${spring.elasticsearch.uris}")
    public String elasticsearchUris;
    @Value("${spring.elasticsearch.username}")
    public String elasticsearchUsername;
    @Value("${spring.elasticsearch.password}")
    public String elasticsearchPassword;

    @Override
    public RestHighLevelClient elasticsearchClient() {
        try {
            final ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                    .connectedTo(elasticsearchUris)
                    .usingSsl(sslContext())
                    .withBasicAuth(elasticsearchUsername, elasticsearchPassword)
                    .build();
            return RestClients.create(clientConfiguration).rest();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private SSLContext sslContext() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain, String authType) {}
            public void checkServerTrusted(X509Certificate[] chain, String authType) {}
            public X509Certificate[] getAcceptedIssuers() { return null; }
        }};

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        return sslContext;
    }

}
