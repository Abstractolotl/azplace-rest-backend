package de.abstractolotl.azplace.rest.api;


import de.abstractolotl.azplace.model.requests.BanRequest;
import de.abstractolotl.azplace.model.view.ProfileView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@CrossOrigin("*")
public interface UserAPI {

    @Operation(
            method = "GET",
            summary = "Get Profile of current user"
    )
    @GetMapping("/")
    ProfileView profile();
}
