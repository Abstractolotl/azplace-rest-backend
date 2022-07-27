package de.abstractolotl.azplace.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "websockets")
public class WebSocketServer {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;
    private String adress;
    private long timestampOnlineSince;

}
