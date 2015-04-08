package frontend;

import base.GameMechanics;
import com.sun.istack.internal.Nullable;
import main.UserProfile;
import mechanics.Role;
import mechanics.Team;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebSocket
public class GameWebSocket {
    private static final Logger LOGGER = LogManager.getLogger(GameWebSocket.class);
    protected String name;
    protected UserProfile userProfile;
    protected Session session;
    protected Team team;
    protected Role role;
    protected GameMechanics gameMechanics;

    //protected WebSocketService webSocketService;
    public GameWebSocket(String name, GameMechanics gameMechanics/*, WebSocketService webSocketService*/) {
        this.name = name;
        this.gameMechanics = gameMechanics;
        //this.webSocketService = webSocketService;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    protected void notifyClient(String type, Map<Object, Object> body) {
        JSONObject json = new JSONObject();
        json.put("type", type);
        json.put("body", body);
        try {
            this.session.getRemote().sendString(json.toJSONString());
        } catch (IOException e) {
            LOGGER.error("Error during notifying client.", e);
        }
    }

    public void notifyChatMessage(UserProfile author, String text) {
        Map<Object, Object> body = new HashMap<>();
        body.put("id", author.getID());
        body.put("text", text);
        this.notifyClient("chat_stopped_typing", body);
    }

    public void notifyChatStoppedTyping(UserProfile userProfile) {
        Map<Object, Object> body = new HashMap<>();
        body.put("id", userProfile.getID());
        this.notifyClient("chat_stopped_typing", body);
    }

    public void notifyChatTyping(UserProfile userProfile) {
        Map<Object, Object> body = new HashMap<>();
        body.put("id", userProfile.getID());
        this.notifyClient("chat_typing", body);
    }

    public void notifyPlayerStatus() {
        this.notifyClient("player_status", this.team.getRoundHydrated(this));
    }

    public void notifyUserCome(UserProfile userProfile) {
        this.notifyClient("user_come", userProfile.getHydrated());
    }

    public void notifyUserGone(UserProfile userProfile) {
        Map<Object, Object> body = new HashMap<>();
        body.put("id", userProfile.getID());
        this.notifyClient("user_gone", body);
    }

    public void notifyViewerStatus() {
        Map<Object, Object> body = new HashMap<>();
        body.put("round", this.team.getViewingAt().getRoundHydrated(this));
        body.put("viewers", this.team.getViewingAt().getViewersHydrated());
        this.notifyClient("viewer_status", body);
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        this.session = session;
        this.team = this.gameMechanics.addToTeam(this);
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        this.gameMechanics.onWebSocketClosed(this);
    }

    @OnWebSocketMessage
    public void onMessage(String text) {
        JSONObject json, body;
        String type;
        try {
            json = (JSONObject) JSONValue.parse(text);
            type = (String) json.get("type");
            body = (JSONObject) json.get("body");
        } catch (NullPointerException | ClassCastException e) {
            LOGGER.error("WebSocket message structure is wrong.", e);
            this.session.close();
            return;
        }
        if ("chat_typing".equals(type)) {
            if (this.role == Role.VIEWER) {
                LOGGER.error("Viewer can't type chat messages.");
                this.session.close();
                return;
            }
            this.team.onChatTyping(this);
        } else if ("chat_stopped_typing".equals(type)) {
            if (this.role == Role.VIEWER) {
                LOGGER.error("Viewer can't type chat messages.");
                this.session.close();
                return;
            }
            this.team.onChatStoppedTyping(this);
        } else if ("chat_message".equals(type)) {
            if (this.role == Role.VIEWER) {
                LOGGER.error("Viewer can't send chat messages.");
                this.session.close();
                return;
            }
            @Nullable String messageText;
            try {
                messageText = (String) body.get("text");
            } catch (ClassCastException e) {
                LOGGER.error("Chat message text should be of string type.", e);
                this.session.close();
                return;
            }
            try {
                this.team.onChatMessage(this, messageText);
            } catch (NullPointerException e) {
                LOGGER.error("Chat message can't be null.", e);
                this.session.close();
                return;
            }
        } else {
            LOGGER.error("Unknown WebSocket message type.");
            this.session.close();
            return;
        }
    }

}
