package de.abstractolotl.azplace.model.user;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    private String firstName;
    @NotNull
    private String lastName;

    @Builder.Default
    private String roles = String.join(",", "default");

    @NotNull
    private String insideNetIdentifier;
    @Builder.Default
    private long timestampRegistered = System.currentTimeMillis();

    public String[] getRoleArray(){
        return roles.split(",");
    }

}