package frontend;

import base.GameMechanics;
import base.WebSocketService;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public class GameWebSocket {
    protected String name;
    protected Session session;
    protected GameMechanics gameMechanics;
    protected WebSocketService webSocketService;

    public GameWebSocket(String name, GameMechanics gameMechanics, WebSocketService webSocketService) {
        this.name = name;
        this.gameMechanics = gameMechanics;
        this.webSocketService = webSocketService;
    }
}
