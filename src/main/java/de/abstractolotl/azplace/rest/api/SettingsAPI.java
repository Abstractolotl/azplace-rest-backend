package de.abstractolotl.azplace.rest.api;

import de.abstractolotl.azplace.model.requests.PlaceRequest;
import de.abstractolotl.azplace.model.view.PaletteView;
import de.abstractolotl.azplace.model.view.SizeView;
import de.abstractolotl.azplace.model.view.TimespanView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Validated
@Tag(name = "SettingsAPI", description = "Endpoints for board settings")
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
@RequestMapping("/settings")
@CrossOrigin(origins = {"*"})
public interface SettingsAPI {

    @GetMapping(value = "/colors/{canvasId}", produces = APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Get the color palette of a canvas",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(schema = @Schema(implementation = PaletteView.class))
            )
    )
    PaletteView colors(@PathVariable int canvasId);

    @GetMapping(value = "/timespan/{canvasId}", produces = APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Get the timespan of a canvas",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(schema = @Schema(implementation = TimespanView.class))
            )
    )
    TimespanView timespan(@PathVariable int canvasId);

    @GetMapping(value = "/size/{canvasId}", produces = APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Get the size of a canvas",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(schema = @Schema(implementation = SizeView.class))
            )
    )
    SizeView size(@PathVariable int canvasId);

}
