package de.abstractolotl.azplace.endpoints;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@RestController("/auth")
public class AuthEndpoint {

    private final String CAS_URL = "https://login.united-internet.org/ims-sso";
    private final String APP_URL = "https://place.azubi.server.lan";

    /**
     * Get ticket
     * <p>
     * Request an CAS mit ticket -> Token
     * Endpoint
     */

    @GetMapping(path="/verify", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String loginInside(@RequestParam String ticket){
        final String url = CAS_URL + "/serviceValidate?service=" + APP_URL + "&ticket=" + ticket;
        final RestTemplate template = new RestTemplate();
        String response;
        try {
            response = template.getForObject(url, String.class);
        } catch (HttpClientErrorException e) {
            throw new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT, e.getResponseBodyAsString());
        }
        System.out.println("Response: " + response);

        return "<meta http-equiv=\"refresh\" content=\"0; url=" + APP_URL + "\" />";
    }

    private String createSessionKey() {
        final UUID sessionKey = UUID.randomUUID();
        //TODO: register key in DB with dateUntilAlive
        return sessionKey.toString();
    }
}