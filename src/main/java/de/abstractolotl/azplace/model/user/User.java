package de.abstractolotl.azplace.model.user;


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

    @NotNull
    private String firstName;
    @NotNull
    private String lastName;

    @Builder.Default
    private String roles = String.join(",", "anonymous");

    @Column(unique=true) @NotNull
    private String insideNetIdentifier;
    @Builder.Default
    private long timestampRegistered = System.currentTimeMillis();

    public String[] getRoleArray(){
        return roles.split(",");
    }

    public String getFullName(){
        return firstName + " " + lastName;
    }

}