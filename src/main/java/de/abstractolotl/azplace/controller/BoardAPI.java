package de.abstractolotl.azplace.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import de.abstractolotl.azplace.model.Canvas;
import de.abstractolotl.azplace.model.requests.PlaceRequest;
import io.swagger.v3.oas.annotations.Operation;

public interface BoardAPI {

    @Operation(
            summary = "Place a Pixel",
            description = "Placed a pixel on the specified Board." +
                          "This endpoint is ready for multi board support." +
                          "for now just use the canasId as 0"
    )
    @CrossOrigin(origins = {"*"})
    @PostMapping("{canvasId}/place")
    void place(@PathVariable int canvasId, @RequestBody PlaceRequest request, @RequestParam("sessionKey") String sessionKey);

    @Operation(
            summary = "Board Data",
            description = "Returns the raw binary data of the board. \n" +
                          "See the outdated presentation for further information: \n" +
                          "https://discord.com/channels/@me/758720519804682248/1001125420055400589"
    )
    @CrossOrigin(origins = {"*"})
    @GetMapping("/{canvasId}/data")
    byte[] boardData(@PathVariable int canvasId);

    @Operation(
            summary = "Board Info",
            description = "Returns the meta information about a Board."
    )
    @CrossOrigin(origins = {"*"})
    @GetMapping("/{canvasId}/info")
    Canvas boardInfo(@PathVariable int canvasId);
}