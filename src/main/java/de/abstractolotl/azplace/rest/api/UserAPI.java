package de.abstractolotl.azplace.rest.api;

import de.abstractolotl.azplace.model.view.ProfileView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Validated
@Tag(name = "UserAPI", description = "User profile endpoints")
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "401", description = "Unauthorized",
                content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema())),
        @ApiResponse(
                responseCode = "400", description = "Invalid data provided",
                content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema())),
        @ApiResponse(
                responseCode = "500", description = "Internal server error",
                content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema())),
        @ApiResponse(
                responseCode = "200", description = "OK",
                content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema())),
})
@RequestMapping("user")
public interface UserAPI {

    @Operation(
            method = "GET",
            summary = "Get Profile of current user"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Profile returned",
                    content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProfileView.class))
            )
    })
    @GetMapping("/")
    ProfileView profile();

}
