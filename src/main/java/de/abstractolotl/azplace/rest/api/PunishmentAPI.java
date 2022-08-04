package de.abstractolotl.azplace.rest.api;

import de.abstractolotl.azplace.model.requests.BanRequest;
import de.abstractolotl.azplace.model.user.UserBan;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Validated
@Tag(name = "PunishmentAPI", description = "Endpoints for user punishment (bans)")
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "401", description = "Unauthorized",
                content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema())
        ),
        @ApiResponse(
                responseCode = "404", description = "Not found",
                content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema())
        ),
        @ApiResponse(
                responseCode = "400", description = "Invalid data provided",
                content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema())
        ),
        @ApiResponse(
                responseCode = "500", description = "Internal server error",
                content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema())
        )
})
@RequestMapping(value = "punishment")
public interface PunishmentAPI {

    @PostMapping(
            value = "/ban",
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE
    )
    @Operation(
            method = "POST",
            summary = "Ban a user",
            requestBody = @RequestBody(
                    content = @Content(schema = @Schema(implementation = BanRequest.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User banned successfully",
                    content = @Content(schema = @Schema(implementation = UserBan.class)
                    )
            )
    })
    @ResponseStatus(HttpStatus.CREATED)
    UserBan banUser(@RequestBody BanRequest banRequest);

    @PostMapping(
            value = "/pardon/{banId}",
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE
    )
    @Operation(method = "POST", summary = "Pardon a user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User unbanned successfully",
                    content = @Content(schema = @Schema(implementation = UserBan.class)
                    )
            )
    })
    UserBan pardon(@PathVariable Long banId);

}
