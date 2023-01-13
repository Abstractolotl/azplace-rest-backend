package de.abstractolotl.azplace.rest.api;

import de.abstractolotl.azplace.model.history.HistoryData;
import de.abstractolotl.azplace.model.history.HistoryInfo;
import de.abstractolotl.azplace.model.requests.HistoryRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;

@Validated
@Tag(name = "HistoryAPI", description = "API for getting the board history")
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
@RequestMapping(value = "history")
public interface HistoryAPI {

    @GetMapping(value = "/{boardId}/info", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "Get the history info of a board")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = HistoryInfo.class))
            )
    })
    HistoryInfo getHistoryInfo(@PathVariable int boardId);

    @PostMapping(value = "/{boardId}/data", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "Get the history data of a board", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                    schema = @Schema(implementation = HistoryRequest.class)
            )
    ))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = HistoryData.class))
            )
    })
    HistoryData getHistory(@PathVariable int boardId, @RequestBody HistoryRequest request);

}
