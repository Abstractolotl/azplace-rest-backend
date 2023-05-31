package de.abstractolotl.azplace;

import de.abstractolotl.azplace.config.SSLConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.security.KeyManagementException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

@EnableScheduling
@SpringBootApplication
public class AzplaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AzplaceApplication.class, args);
    }

    @Bean
    public CloseableHttpClient getCloseableHttpClient()
    {
        CloseableHttpClient httpClient = null;

        try {
            httpClient = HttpClients.custom().
                    setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).
                    setSSLContext(new SSLConfig().getSSLContext()).build();
        } catch (Exception ignored) { }

        return httpClient;
    }

}