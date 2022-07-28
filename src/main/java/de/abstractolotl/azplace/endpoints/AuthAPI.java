package de.abstractolotl.azplace.endpoints;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController("/auth")
public interface AuthAPI {


    /**
     * Get ticket
     * <p>
     * Request an CAS mit ticket -> Token
     * Endpoint
     */

    @GetMapping(path="/verify", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    String verify(@RequestParam("ticket") String ticket);

    @GetMapping(path="/logout", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    String logout(@RequestParam("session") String sessionKey);

    @GetMapping(path="/xmlShit", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    String xmlShit(@RequestParam("xml") String xml);
}