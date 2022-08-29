package de.abstractolotl.azplace.rest.api;

import de.abstractolotl.azplace.model.requests.PlaceRequest;
import de.abstractolotl.azplace.model.view.BotView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Validated
@Tag(name = "BotAPI", description = "Endpoints for bot users")
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
@RequestMapping("bot")
public interface BotAPI {

    @PostMapping(value = "/create")
    @Operation(summary = "Create a new bot token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = BotView.class))
            )
    })
    BotView createBotToken();

    @GetMapping(value = "/info")
    @Operation(summary = "Get your current bot token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = BotView.class))
            )
    })
    BotView getBotToken();

    @PostMapping(value = "/refresh")
    @Operation(summary = "Regenerate your current bot token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = BotView.class))
            )
    })
    BotView refreshToken();

    @PostMapping(value = "/{canvasId}/place")
    @Operation(
            summary = "Place a pixel via bot token",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(schema = @Schema(implementation = PlaceRequest.class))
            )
    )
    void placePixel(@RequestHeader("Bot-Token") String token, @PathVariable int canvasId, @RequestBody PlaceRequest placeRequest);

}
