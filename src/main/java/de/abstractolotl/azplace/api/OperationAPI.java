package de.abstractolotl.azplace.api;

import de.abstractolotl.azplace.model.board.Canvas;
import de.abstractolotl.azplace.model.board.ColorPalette;
import de.abstractolotl.azplace.model.requests.CanvasRequest;
import de.abstractolotl.azplace.model.requests.PaletteRequest;
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
    @CrossOrigin(origins = {"*"})
    Canvas getCanvas(@PathVariable Integer id, @RequestParam("sessionKey") String sessionKey);

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
    @CrossOrigin(origins = {"*"})
    @ResponseStatus(HttpStatus.CREATED)
    Canvas createCanvas(@RequestBody CanvasRequest canvas, @RequestParam("sessionKey") String sessionKey);

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
    @CrossOrigin(origins = {"*"})
    Canvas updateCanvas(@PathVariable Integer id, @RequestBody CanvasRequest canvasRequest, @RequestParam("sessionKey") String sessionKey);

    @DeleteMapping(value = "/canvas/{id}",
            produces = APPLICATION_JSON_VALUE)
    @Operation(method = "DELETE", summary = "Delete an existing canvas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Canvas deleted successfully",
                    content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
    })
    @CrossOrigin(origins = {"*"})
    ResponseEntity<?> deleteCanvas(@PathVariable Integer id, @RequestParam("sessionKey") String sessionKey);

    @GetMapping(value = "/palette/{id}", produces = APPLICATION_JSON_VALUE)
    @Operation(method = "GET", summary = "Get current palette data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Palette found and returned",
                    content = @Content(schema = @Schema(implementation = ColorPalette.class)))
    })
    @CrossOrigin(origins = {"*"})
    ColorPalette getPalette(@PathVariable Integer id, @RequestParam("sessionKey") String sessionKey);

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
    @CrossOrigin(origins = {"*"})
    @ResponseStatus(HttpStatus.CREATED)
    ColorPalette createPalette(@RequestBody PaletteRequest palette, @RequestParam("sessionKey") String sessionKey);

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
    @CrossOrigin(origins = {"*"})
    ColorPalette updatePalette(@PathVariable Integer id, @RequestBody PaletteRequest palette, @RequestParam("sessionKey") String sessionKey);

    @DeleteMapping(value = "/palette/{id}",
            produces = APPLICATION_JSON_VALUE)
    @Operation(method = "DELETE", summary = "Delete an existing palette")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Palette deleted successfully",
                    content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
    })
    @CrossOrigin(origins = {"*"})
    ResponseEntity<?> deletePalette(@PathVariable Integer id, @RequestParam("sessionKey") String sessionKey);

}
