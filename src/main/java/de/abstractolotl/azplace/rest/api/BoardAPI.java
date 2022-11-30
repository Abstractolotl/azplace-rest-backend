package de.abstractolotl.azplace.rest.api;

import de.abstractolotl.azplace.model.requests.PlaceRequest;
import de.abstractolotl.azplace.model.view.ConfigView;
import de.abstractolotl.azplace.model.view.CooldownView;
import de.abstractolotl.azplace.model.view.PixelInfoView;
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
@RequestMapping("board")
public interface BoardAPI {

    @PostMapping(value = "{canvasId}/place", consumes = { APPLICATION_JSON_VALUE, "application/v1+json" })
    @Operation(
            summary = "Place a Pixel",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(schema = @Schema(implementation = PlaceRequest.class))
            )
    )
    void place(@PathVariable int canvasId, @RequestBody PlaceRequest request);

    @GetMapping(value = "{canvasId}/pixel/{x}/{y}", produces = { APPLICATION_JSON_VALUE, "application/v1+json" })
    @Operation(summary = "Get Information about a pixel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PixelInfo returned",
                content = @Content(schema = @Schema(implementation = PixelInfoView.class)))
    })
    PixelInfoView pixel(@PathVariable int canvasId, @PathVariable int x, @PathVariable int y);

    @GetMapping("/{canvasId}/data")
    @Operation(summary = "Get the current board data")
    byte[] boardData(@PathVariable int canvasId);

    @GetMapping(value = "/{canvasId}/cooldown", produces = { APPLICATION_JSON_VALUE, "application/v1+json" })
    @Operation(summary = "Get your current cooldown information" )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Current user cooldown returned",
                    content = @Content(schema = @Schema(implementation = CooldownView.class)))
    })
    CooldownView cooldown(@PathVariable int canvasId);

    @GetMapping(value = "/{canvasId}/config", produces = { APPLICATION_JSON_VALUE, "application/v1+json" })
    @Operation(summary = "Get board configuration" )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Current user cooldown returned",
                    content = @Content(schema = @Schema(implementation = ConfigView.class)))
    })
    ConfigView boardConfig(@PathVariable int canvasId);

}