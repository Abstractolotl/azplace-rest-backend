package de.abstractolotl.azplace.rest.api;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import javax.servlet.http.HttpSession;

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
@RequestMapping("auth")
public interface AuthAPI {

    @Operation(summary = "Start login process")
    @GetMapping(path = "/login", produces = MediaType.TEXT_HTML_VALUE)
    String login(@RequestHeader(HttpHeaders.HOST) String hostName);

    @Operation(
            summary = "Verify authentication token")
    @GetMapping(path = "/callback", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    ResponseEntity<String> verify(@RequestParam("code") String code);

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
    ResponseEntity<String> logout(HttpSession httpSession);

}