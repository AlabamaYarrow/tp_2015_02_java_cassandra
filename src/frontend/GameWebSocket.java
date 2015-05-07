package frontend;

import base.GameMechanics;
import base.Listenable;
import base.Listener;
import com.sun.istack.internal.Nullable;
import main.UserProfile;
import mechanics.Event;
import mechanics.PlayersTeam;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@WebSocket
public class GameWebSocket implements Listenable, Listener {
    private static final Logger LOGGER = LogManager.getLogger(GameWebSocket.class);
    private UserProfile userProfile;
    private Session session;
    private GameMechanics gameMechanics;
    private List<Listener> listeners = new CopyOnWriteArrayList<>();

    public GameWebSocket(UserProfile userProfile, GameMechanics gameMechanics) {
        this.userProfile = userProfile;
        this.gameMechanics = gameMechanics;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void closeSession() {
        this.session.close();
    }

    private void notifyClient(String type, Map<Object, Object> body) {
        JSONObject json = new JSONObject();
        json.put("type", type);
        json.put("body", body);
        try {
            this.session.getRemote().sendString(json.toJSONString());
        } catch (IOException e) {
            LOGGER.error("Error during notifying client.", e);
        }
    }

    private void onChatMessage(Event event) {
        Map<Object, Object> body = new HashMap<>();
        body.put("id", ((GameWebSocket) event.getTarget()).getUserProfile().getID());
        body.put("text", event.getData().get("text"));
        this.notifyClient("chat_message", body);
    }

    private void onChatStoppedTyping(Event event) {
        Map<Object, Object> body = new HashMap<>();
        body.put("id", ((GameWebSocket) event.getTarget()).getUserProfile().getID());
        this.notifyClient("chat_stopped_typing", body);
    }

    private void onChatTyping(Event event) {
        Map<Object, Object> body = new HashMap<>();
        body.put("id", ((GameWebSocket) event.getTarget()).getUserProfile().getID());
        this.notifyClient("chat_typing", body);
    }

    private void onPlayerStatus(Event event) {
        this.notifyClient(event.getType(), ((PlayersTeam) event.getTarget()).getRoundHydrated(this));
    }

    private void onConnected(Event event) {
        this.notifyClient("user_come", ((GameWebSocket) event.getTarget()).getUserProfile().getHydrated());
    }

    private void onClosed(Event event) {
        Map<Object, Object> body = new HashMap<>();
        body.put("id", ((GameWebSocket) event.getTarget()).getUserProfile().getID());
        this.notifyClient("user_gone", body);
    }

    private void onViewerStatus(Event event) {
        this.notifyClient(event.getType(), event.getData());
    }

    @OnWebSocketConnect
    public void onWebSocketConnect(Session session) {
        this.session = session;
        this.notifyListeners("connected", null);
    }

    @OnWebSocketClose
    public void onWebSocketClose(int statusCode, String reason) {
        this.notifyListeners("closed", null);
    }

    @OnWebSocketMessage
    public void onWebSocketMessage(String text) {
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
            this.notifyListeners("chat_typing", null);
        } else if ("chat_stopped_typing".equals(type)) {
            this.notifyListeners("chat_stopped_typing", null);
        } else if ("chat_message".equals(type)) {
            @Nullable String messageText;
            try {
                messageText = (String) body.get("text");
            } catch (ClassCastException e) {
                LOGGER.error("Chat message text should be of string type.", e);
                this.closeSession();
                return;
            } catch (NullPointerException e) {
                LOGGER.error("Chat message can't be null.");
                this.closeSession();
                return;
            }
            this.notifyListeners("chat_message", body);
        } else {
            LOGGER.error("Unknown WebSocket message type.");
            this.closeSession();
        }
    }

    private void notifyListeners(String type, Map<Object, Object> data) {
        Event event = new Event(this, type, data);
        for (Listener listener : this.listeners) {
            listener.onEvent(event);
        }
    }

    @Override
    public void addListener(Listener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeListener(Listener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public void onEvent(Event event) {
        String type = event.getType();
        if ("connected".equals(type)) {
            this.onConnected(event);
        } else if ("closed".equals(type)) {
            this.onClosed(event);
        } else if ("player_status".equals(type)) {
            this.onPlayerStatus(event);
        } else if ("viewer_status".equals(type)) {
            this.onViewerStatus(event);
        } else if ("chat_message".equals(type)) {
            this.onChatMessage(event);
        } else if ("chat_typing".equals(type)) {
            this.onChatTyping(event);
        } else if ("chat_stopped_typing".equals(type)) {
            this.onChatStoppedTyping(event);
        } else {
            LOGGER.debug("Unknown event: {} {}", type, event.getData());
        }
    }
}
