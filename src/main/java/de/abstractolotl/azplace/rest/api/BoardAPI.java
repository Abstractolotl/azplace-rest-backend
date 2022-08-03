package de.abstractolotl.azplace.rest.api;

import de.abstractolotl.azplace.model.board.Canvas;
import de.abstractolotl.azplace.model.requests.PlaceRequest;
import de.abstractolotl.azplace.model.view.ConfigView;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;

import java.util.HashMap;

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
    void place(@PathVariable int canvasId, @RequestBody PlaceRequest request);

    @GetMapping("/{canvasId}/data")
    @Operation(
            summary = "Board Data",
            description = "Returns the raw binary data of the board. \n" +
                          "See the outdated presentation for further information: \n" +
                          "https://discord.com/channels/@me/758720519804682248/1001125420055400589"
    )
    byte[] boardData(@PathVariable int canvasId);

    @GetMapping("/{canvasId}/info")
    @Operation(
            deprecated = true,
            summary = "Board Info",
            description = "This endpoint should not be used anymore use /{canvasId}/config instead"
    )
    Canvas boardInfo(@PathVariable int canvasId);

    @GetMapping("/{canvasId}/cooldown")
    @Operation(summary = "Get your current cooldown information" )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Current user cooldown returned",
                    content = @Content(schema = @Schema(implementation = HashMap.class)))
    })
    HashMap<String, Long> cooldown(@PathVariable int canvasId);

    @GetMapping("/{canvasId}/config")
    @Operation(summary = "Get board configuration" )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Current user cooldown returned",
                    content = @Content(schema = @Schema(implementation = ConfigView.class)))
    })
    ConfigView boardConfig(@PathVariable int canvasId);

}