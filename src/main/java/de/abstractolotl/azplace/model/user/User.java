package de.abstractolotl.azplace.model.user;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="user")
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = -7770861223841514824L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull @JsonProperty("first_name")
    private String firstName;
    @NotNull @JsonProperty("last_name")
    private String lastName;

    @Builder.Default @JsonIgnore
    private String roles = String.join(",", UserRoles.ANONYMOUS.format());

    @Column(unique=true) @NotNull
    @JsonProperty("inside_id")
    private String insideNetIdentifier;

    @Builder.Default @JsonProperty("timestamp_registered")
    private long timestampRegistered = System.currentTimeMillis();

    @JsonProperty("role_array")
    public String[] getRoleArray(){
        return roles.split(",");
    }

    @JsonIgnore
    public String getFullName(){
        return firstName + " " + lastName;
    }

}