package de.abstractolotl.azplace.model.user;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name="user")
public class User {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;
    private String fullname;

    @JsonProperty("inside_identifier")
    private String insideNetIdentifier; //TODO: how is a inside net user identified? what does the CAS return.

    private String role = "default";

    @JsonProperty("timestamp_last_pixel")
    private long timestampLastPixel; //TODO: Discuss how the timestamp should be saved / handled
    @JsonProperty("timestamp_registered")
    private long timestampRegistered;

}
