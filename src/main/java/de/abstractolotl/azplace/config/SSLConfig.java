package de.abstractolotl.azplace.config;

import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.springframework.context.annotation.Bean;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

public class SSLConfig {

    private final char[] keyStorePass = "changeit".toCharArray();
    private final String certPathName = new File("/etc/es-certificates/tls.crt").isFile() ? "/etc/es-certificates/tls.crt" : "tls.crt";
    private final File keyStoreFile;

    public SSLConfig() throws Exception {
        keyStoreFile = this.generateKeyStoreFile();
    }

    @Bean
    SSLContext sslContext() throws Exception {
        return this.getSSLContext();
    }

    Certificate generateCert() throws Exception {
        InputStream inStream = new FileInputStream(certPathName);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        return cf.generateCertificate(inStream);
    }

    File generateKeyStoreFile() throws Exception {
        String keyStoreName = "keystore.jks";
        File f = new File(keyStoreName);
        if (f.isFile()) return f;

        Certificate cert = this.generateCert();
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null);
        ks.setCertificateEntry("es-http-public", cert);
        ks.store(new FileOutputStream(keyStoreName), keyStorePass);
        return new File(keyStoreName);
    }

    public SSLContext getSSLContext() throws Exception {
        SSLContextBuilder builder = SSLContexts.custom();
        builder.loadTrustMaterial(keyStoreFile, keyStorePass, new TrustSelfSignedStrategy());
        return builder.build();
    }
}
