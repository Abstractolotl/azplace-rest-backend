package de.abstractolotl.azplace.endpoints;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController("/auth")
public class AuthEndpoint {

    private final String URL = "https://login.united-internet.org/ims-sso";

    /**
     * Get ticket
     * <p>
     * Request an CAS mit ticket -> Token
     * Endpoint
     */

    @PostMapping("verify")
    public String verifyTicket(@RequestParam("ticket") String ticket) {
        System.out.println("verify");

        String app = "https://united-internet.org";
        final String requestString = URL + "/serviceValidate?service=" + app + "&ticket=" + ticket;
        HttpRequest request = HttpRequest.newBuilder().GET()
                                         .uri(URI.create(requestString)).build();
        HttpClient client = HttpClient.newBuilder().build();
        try {
            final HttpResponse<Void> httpResponse = client.send(request, HttpResponse.BodyHandlers.discarding());
            final int                      statusCode   = httpResponse.statusCode();
            final Void                     body         = httpResponse.body();
            System.out.println("response  \t:" + httpResponse);
            System.out.println("statusCode\t:" + statusCode);
            System.out.println("body      \t:" + body);
            System.out.println("x         \t:" + httpResponse.headers());
            System.out.println("x         \t:" + httpResponse.previousResponse());
            System.out.println("x         \t:" + httpResponse.request());
        } catch (IOException | InterruptedException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        //TODO: verify ticket by CAS Server
        // generate sessionKey
        // store sessionKey in DB
        // return session Key

        return "Works!";
    }

    private String createSessionKey() {
        final UUID sessionKey = UUID.randomUUID();
        //TODO: register key in DB with dateUntilAlive
        return sessionKey.toString();
    }
}