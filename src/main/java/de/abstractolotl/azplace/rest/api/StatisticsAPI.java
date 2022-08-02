package de.abstractolotl.azplace.rest.api;

import de.abstractolotl.azplace.model.view.PaletteView;
import de.abstractolotl.azplace.model.view.PixelStatisticView;
import de.abstractolotl.azplace.model.view.TopStatisticView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Validated
@Tag(name = "StatisticsAPI", description = "Endpoints for statistics")
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
@RequestMapping("/statistics")
@CrossOrigin(origins = {"*"})
public interface StatisticsAPI {

    @GetMapping(value = "/pixels", produces = APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Get placed pixels in the last 24h",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(schema = @Schema(implementation = PixelStatisticView.class))
            )
    )
    PixelStatisticView pixels();

    @GetMapping(value = "/toplist/{max:10}", produces = APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Get toplist by placed pixels",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(schema = @Schema(implementation = TopStatisticView.class))
            )
    )
    TopStatisticView topList(@PathVariable(required = false) int max);



}
