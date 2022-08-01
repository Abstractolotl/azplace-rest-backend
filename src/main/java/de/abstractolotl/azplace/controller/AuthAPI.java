package de.abstractolotl.azplace.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import de.abstractolotl.azplace.model.Session;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

public interface AuthAPI {

    @Operation(
            summary = "Verify",
            description = """
                    This endpoint get called from the CAS.
                    A Request to the CAS look like this: https://login.united-internet.org/ims-sso/login?service=https://api.azplace.azubi.server.lan/verify
                    This Backend validate the given ticket.
                    If it is valid:
                        - It create a session to the backend
                            - SessionKey which is written in to the DB
                            - Backend caches SessionKey and User via Spring session
                        - It redirect to the frontend
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "500", description = "In this Case something went wrong with the ticket validation request to the CAS."),
            @ApiResponse(responseCode = "500", description = "In this Case the XML mapping failed"),
            @ApiResponse(responseCode = "401", description = "CAS response is no \"authenticationsuccess\""),
            @ApiResponse(responseCode = "400", description = "A required key is not included in the CAS response")
    })
    @GetMapping(path = "/verify", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    @CrossOrigin(origins = {"*"})
    String verify(@RequestParam("ticket") String ticket);

    @Operation(
            summary = "XML Fake Verify",
            description = """
                    This Endpoint is for testing.
                    It's a basically the same as /verify but you have to put in the CAS response you want to process.
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "500", description = "In this Case the XML mapping failed"),
            @ApiResponse(responseCode = "401", description = "CAS response is no \"authenticationsuccess\""),
            @ApiResponse(responseCode = "400", description = "A required key is not included in the CAS response")
    })
    @GetMapping(path = "/xmlShit", produces = MediaType.TEXT_HTML_VALUE) //TODO: only test usage
    @ResponseBody
    @CrossOrigin(origins = {"*"})
    void xmlShit(@RequestParam("xml") String xml);

    @Operation(
            summary = "Log out",
            description = """
                    This endpoint delete the sessionKey from DB and clear the Spring session information's.
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK")
    })
    @GetMapping(path = "/logout", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    @CrossOrigin(origins = {"*"})
    void logout(@RequestParam("sessionKey") String sessionKey);

    @Operation(
            summary = "Get session",
            description = """
                    This endpoint returns information about your session.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Your sessionKey is not registered")
    })
    @GetMapping(path = "/session")
    @ResponseBody
    @CrossOrigin(origins = {"*"})
    Session getSession(@RequestParam("sessionKey") String sessionKey);

    @Operation(
            summary = "Is session valid",
            description = """
                    This endpoint show the User if his session is Valid.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Your sessionKey is not registered"),
            @ApiResponse(responseCode = "401", description = "Your sessionKey is expired")
    })
    @GetMapping(path = "/isSessionValid")
    @ResponseBody
    @CrossOrigin(origins = {"*"})
    boolean isSessionValid(@RequestParam("sessionKey") String sessionKey);
}