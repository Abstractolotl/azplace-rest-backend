package de.abstractolotl.azplace.model.utility;

import lombok.Data;

import javax.persistence.*;
import java.net.URI;
import java.net.URISyntaxException;

@Entity
@Data
@Table(name = "websockets")
public class WebSocketServerInfo {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;
    @Column(name="address")
    private String ipAddress;
    private long timestampOnlineSince;
    private String secret;

    public URI getURI() throws URISyntaxException {
        return new URI("ws://" + ipAddress + ":8080/ws"); //TODO: port not hardcoded
    }

}
