package de.abstractolotl.azplace.rest.api;

import de.abstractolotl.azplace.model.view.CountView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("statistics")
public interface StatisticsAPI {

    @GetMapping(value = "/pixels", produces = { APPLICATION_JSON_VALUE, "application/v1+json" })
    @Operation(summary = "Get placed pixels in the last 24h")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pixel statistics calculated and returned",
                    content = @Content(schema = @Schema(implementation = CountView.class)))
    })
    CountView pixels();

    @GetMapping(value = "/online", produces = { APPLICATION_JSON_VALUE, "application/v1+json" })
    @Operation(summary = "Get amount of currently online users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Amount of currently online users returned",
                    content = @Content(schema = @Schema(implementation = CountView.class)))
    })
    CountView online();



}
