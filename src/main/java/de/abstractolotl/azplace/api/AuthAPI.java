package de.abstractolotl.azplace.api;

import de.abstractolotl.azplace.model.user.Session;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;

@Validated
@Tag(name = "AuthenticationAPI", description = "Authentication endpoints")
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "401", description = "Unauthorized",
                content = @Content(mediaType = TEXT_HTML_VALUE, schema = @Schema())),
        @ApiResponse(
                responseCode = "400", description = "Invalid data provided",
                content = @Content(mediaType = TEXT_HTML_VALUE, schema = @Schema())),
        @ApiResponse(
                responseCode = "500", description = "Internal server error",
                content = @Content(mediaType = TEXT_HTML_VALUE, schema = @Schema())),
        @ApiResponse(
                responseCode = "200", description = "OK",
                content = @Content(mediaType = TEXT_HTML_VALUE, schema = @Schema())),
})
@RequestMapping("/auth")
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
    @GetMapping(path = "/verify", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    @CrossOrigin(origins = {"*"})
    String verify(@RequestParam("ticket") String ticket);

    @GetMapping(path = "/debug", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    @CrossOrigin(origins = {"*"})
    String casDebug(@RequestParam("ticket") String ticket);

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
    @GetMapping(path = "/isSessionValid")
    @ResponseBody
    @CrossOrigin(origins = {"*"})
    boolean isSessionValid(@RequestParam("sessionKey") String sessionKey);
}