package de.abstractolotl.azplace.rest.api;

import de.abstractolotl.azplace.model.board.Canvas;
import de.abstractolotl.azplace.model.requests.PlaceRequest;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Validated
@Tag(name = "BoardAPI", description = "Endpoints for board information")
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "401", description = "Unauthorized",
                content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema())),
        @ApiResponse(
                responseCode = "404", description = "Not found",
                content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema())),
        @ApiResponse(
                responseCode = "400", description = "Invalid data provided",
                content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema())),
        @ApiResponse(
                responseCode = "500", description = "Internal server error",
                content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema()))
})
@RequestMapping("/board")
public interface BoardAPI {

    @PostMapping("{canvasId}/place")
    @Operation(
            summary = "Place a Pixel",
            description = "Placed a pixel on the specified Board." +
                          "This endpoint is ready for multi board support." +
                          "for now just use the canvasId as 0",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(schema = @Schema(implementation = PlaceRequest.class))
            )
    )
    @CrossOrigin(origins = {"*"})
    void place(@PathVariable int canvasId, @RequestBody PlaceRequest request);

    @GetMapping("/{canvasId}/data")
    @Operation(
            summary = "Board Data",
            description = "Returns the raw binary data of the board. \n" +
                          "See the outdated presentation for further information: \n" +
                          "https://discord.com/channels/@me/758720519804682248/1001125420055400589"
    )
    @CrossOrigin(origins = {"*"})
    byte[] boardData(@PathVariable int canvasId);

    @GetMapping("/{canvasId}/info")
    @Operation(
            summary = "Board Info",
            description = "Returns the meta information about a Board."
    )
    @CrossOrigin(origins = {"*"})
    Canvas boardInfo(@PathVariable int canvasId);
}