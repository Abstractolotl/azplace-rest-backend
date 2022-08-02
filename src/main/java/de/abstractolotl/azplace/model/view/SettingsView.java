package de.abstractolotl.azplace.model.view;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SettingsView {

    @JsonProperty("hex_colors")
    private String[] hexColors;

    private SizeView size;

    private TimespanView timespan;

    public static SettingsView

}
