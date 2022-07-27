package de.abstractolotl.azplace.model;

import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Data
@Table(name = "websockets")
public class WebSocketServer {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;
    private String address;
    private long timestampOnlineSince;
    private String secret;

}
