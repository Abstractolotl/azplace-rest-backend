package de.abstractolotl.azplace.endpoints;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Component
public class AuthController implements AuthAPI {

    private final String casUrl;
    private final String redirectUrl;

    public AuthController(@Value("${app.cas.url}") String casUrl,
                          @Value("${app.cas.redirectUrl}") String redirectUrl) {
        this.casUrl = casUrl;
        this.redirectUrl = redirectUrl;
    }

    /**
     * Get ticket
     * <p>
     * Request an CAS mit ticket -> Token
     * Endpoint
     */

    @Override
    public String verify(@RequestParam("ticket") String ticket) {
        final String       url      = casUrl + "/serviceValidate?service=" + redirectUrl + "&ticket=" + ticket;
        final RestTemplate template = new RestTemplate();
        String             response;
        try {
            response = template.getForObject(url, String.class);
        } catch (HttpClientErrorException e) {
            throw new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT, e.getResponseBodyAsString());
        }
        System.out.println("Response: " + response);


        boolean validTicket = false; //TODO: check if ticket is valid and get response (there is the user name in it)
        if (validTicket) {

            return "<meta http-equiv=\"refresh\" content=\"0; url=" + redirectUrl + "\" />";
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Ticket invalid!");
    }

    @Override
    public String logout(String sessionKey) {
        return null;
    }

    private String createSessionKey() {
        final UUID sessionKey = UUID.randomUUID();
        //TODO: register key in DB with dateUntilAlive
        return sessionKey.toString();
    }
}
