package frontend;

import base.GameMechanics;
import base.Listenable;
import base.Listener;
import com.sun.istack.internal.Nullable;
import main.UserProfile;
import mechanics.Event;
import mechanics.PlayersTeam;
import mechanics.UnknownEventError;
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
    protected UserProfile userProfile;
    protected Session session;
    protected GameMechanics gameMechanics;
    protected List<Listener> listeners = new CopyOnWriteArrayList<>();

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

    protected void notifyClientChatMessage(UserProfile author, String text) {
        Map<Object, Object> body = new HashMap<>();
        body.put("id", author.getID());
        body.put("text", text);
        this.notifyClient("chat_stopped_typing", body);
    }

    protected void notifyClientChatStoppedTyping(UserProfile userProfile) {
        Map<Object, Object> body = new HashMap<>();
        body.put("id", userProfile.getID());
        this.notifyClient("chat_stopped_typing", body);
    }

    protected void notifyClientChatTyping(UserProfile userProfile) {
        Map<Object, Object> body = new HashMap<>();
        body.put("id", userProfile.getID());
        this.notifyClient("chat_typing", body);
    }

    protected void notifyClientPlayerStatus(PlayersTeam team) {
        this.notifyClient("player_status", team.getRoundHydrated(this));
    }

    protected void notifyClientUserCome(UserProfile userProfile) {
        this.notifyClient("user_come", userProfile.getHydrated());
    }

    protected void notifyClientUserGone(UserProfile userProfile) {
        Map<Object, Object> body = new HashMap<>();
        body.put("id", userProfile.getID());
        this.notifyClient("user_gone", body);
    }

    protected void notifyClientViewerStatus(Map<Object, Object> players, List<Object> viewers) {
        Map<Object, Object> body = new HashMap<>();
        body.put("round", players);
        body.put("viewers", viewers);
        this.notifyClient("viewer_status", body);
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        this.session = session;
        this.notifyListeners("connected", null);
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        this.notifyListeners("closed", null);
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
            }
            if (messageText == null) {
                LOGGER.error("Chat message can't be null.");
                this.closeSession();
                return;
            }
            this.notifyListeners("chat_message", null);
        } else {
            LOGGER.error("Unknown WebSocket message type.");
            this.closeSession();
        }
    }

    protected void notifyListeners(String type, Map<Object, Object> data) {
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
        Map<Object, Object> data = event.getData();
        if ("connected".equals(type)) {
            this.notifyClientUserCome(((GameWebSocket) event.getTarget()).getUserProfile());
        } else if ("closed".equals(type)) {
            this.notifyClientUserGone(((GameWebSocket) event.getTarget()).getUserProfile());
        } else if ("player_status".equals(type)) {
            this.notifyClientPlayerStatus((PlayersTeam) event.getTarget());
        } else if ("viewer_status".equals(type)) {
            this.notifyClientViewerStatus((Map<Object, Object>) data.get("players"), (List<Object>) data.get("viewers"));
        } else if ("chat_message".equals(type)) {
            this.notifyClientChatMessage(((GameWebSocket) data.get("user")).getUserProfile(), (String) data.get("text"));
        } else if ("chat_typing".equals(type)) {
            this.notifyClientChatTyping(((GameWebSocket) data.get("user")).getUserProfile());
        } else if ("chat_stopped_typing".equals(type)) {
            this.notifyClientChatStoppedTyping(((GameWebSocket) data.get("user")).getUserProfile());
        } else {
            throw new UnknownEventError();
        }
    }
}
