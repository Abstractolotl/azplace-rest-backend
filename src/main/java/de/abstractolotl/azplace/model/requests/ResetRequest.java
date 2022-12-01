package de.abstractolotl.azplace.model.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import de.abstractolotl.azplace.model.utility.PixelRegion;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResetRequest {

    private List<Integer[]> pixelList;
    private PixelRegion pixelRegion;

}
