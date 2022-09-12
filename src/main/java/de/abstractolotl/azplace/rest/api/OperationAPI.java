package de.abstractolotl.azplace.rest.api;

import de.abstractolotl.azplace.model.board.Canvas;
import de.abstractolotl.azplace.model.board.ColorPalette;
import de.abstractolotl.azplace.model.requests.CanvasRequest;
import de.abstractolotl.azplace.model.requests.PaletteRequest;
import de.abstractolotl.azplace.model.requests.ResetRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Validated
@Tag(name = "OperationAPI", description = "Endpoints for operations")
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
@RequestMapping(value = "operation")
public interface OperationAPI {

    @GetMapping(value = "/canvas/{id}", produces = APPLICATION_JSON_VALUE)
    @Operation(method = "GET", summary = "Get current canvas data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Canvas found and returned",
                    content = @Content(schema = @Schema(implementation = Canvas.class)))
    })
    Canvas getCanvas(@PathVariable Integer id);

    @PostMapping(value = "/canvas",
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    @Operation(method = "POST", summary = "Create new canvas",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(schema = @Schema(implementation = CanvasRequest.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Canvas created successfully",
                    content = @Content(schema = @Schema(implementation = Canvas.class)))
    })
    @ResponseStatus(HttpStatus.CREATED)
    Canvas createCanvas(@RequestBody CanvasRequest canvasRequest);

    @PatchMapping(value = "/canvas/{id}",
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    @Operation(method = "PATCH", summary = "Update an existing canvas",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(schema = @Schema(implementation = CanvasRequest.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Canvas updated successfully",
                    content = @Content(schema = @Schema(implementation = Canvas.class)))
    })
    Canvas updateCanvas(@PathVariable Integer id, @RequestBody CanvasRequest canvasRequest);

    @DeleteMapping(value = "/canvas/{id}",
            produces = APPLICATION_JSON_VALUE)
    @Operation(method = "DELETE", summary = "Delete an existing canvas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Canvas deleted successfully",
                    content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
    })
    ResponseEntity<?> deleteCanvas(@PathVariable Integer id);

    @GetMapping(value = "/palette/{id}", produces = APPLICATION_JSON_VALUE)
    @Operation(method = "GET", summary = "Get current palette data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Palette found and returned",
                    content = @Content(schema = @Schema(implementation = ColorPalette.class)))
    })
    ColorPalette getPalette(@PathVariable Integer id);

    @PostMapping(value = "/palette",
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    @Operation(method = "POST", summary = "Create new palette",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(schema = @Schema(implementation = PaletteRequest.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Palette created successfully",
                    content = @Content(schema = @Schema(implementation = ColorPalette.class)))
    })
    @ResponseStatus(HttpStatus.CREATED)
    ColorPalette createPalette(@RequestBody PaletteRequest paletteRequest);

    @PatchMapping(value = "/palette/{id}",
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    @Operation(method = "PATCH", summary = "Update an existing palette",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(schema = @Schema(implementation = PaletteRequest.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Palette updated successfully",
                    content = @Content(schema = @Schema(implementation = ColorPalette.class))),
    })
    ColorPalette updatePalette(@PathVariable Integer id, @RequestBody PaletteRequest paletteRequest);

    @DeleteMapping(value = "/palette/{id}",
            produces = APPLICATION_JSON_VALUE)
    @Operation(method = "DELETE", summary = "Delete an existing palette")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Palette deleted successfully",
                    content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
    })
    ResponseEntity<?> deletePalette(@PathVariable Integer id);

    @PostMapping(value = "/reset/{id}",
            consumes = APPLICATION_JSON_VALUE)
    @Operation(method = "POST", summary = "Reset all pixels included in this request",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                content = @Content(schema = @Schema(implementation = ResetRequest.class))
            )
    )
    ResponseEntity<?> resetPixel(@PathVariable Integer id, ResetRequest resetRequest);

}
