package de.abstractolotl.azplace.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "websockets")
public class WebSocketServer {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;
    private String address;
    private long timestampOnlineSince;
    private String secret;

}
