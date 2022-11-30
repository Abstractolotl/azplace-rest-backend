package de.abstractolotl.azplace.rest.api;

import de.abstractolotl.azplace.model.user.UserSettings;
import de.abstractolotl.azplace.model.view.ProfileView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @GetMapping(value = "/", produces = { APPLICATION_JSON_VALUE, "application/v1+json" })
    @Operation(summary = "Get Profile of current user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Profile returned",
                    content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProfileView.class))
            )
    })
    ProfileView profile();

    @Operation(summary = "Update user settings",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(schema = @Schema(implementation = UserSettings.class))
            ))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Settings updated successfully")
    })
    @PutMapping(value = "/settings", consumes = { APPLICATION_JSON_VALUE, "application/v1+json" })
    void setSettings(@RequestBody UserSettings userSettings);

}
