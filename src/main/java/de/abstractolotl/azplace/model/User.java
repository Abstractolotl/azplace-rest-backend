package de.abstractolotl.azplace.model;


import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name="user")
public class User {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;
    private String fullname;
    private String insideNetIdentifier; //TODO: how is a inside net user identified? what does the CAS return.
    private long timestampLastPixel; //TODO: Discuss how the timestamp should be saved / handeld
    private long timestampRegistered;

}
