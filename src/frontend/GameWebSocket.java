package frontend;

import base.GameMechanics;
import base.Listenable;
import base.Listener;
import base.dataSets.UserDataSet;
import com.sun.istack.internal.Nullable;
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
    private UserDataSet userProfile;
    private Session session;
    private GameMechanics gameMechanics;
    private List<Listener> listeners = new CopyOnWriteArrayList<>();

    public GameWebSocket(UserDataSet userProfile, GameMechanics gameMechanics) {
        this.userProfile = userProfile;
        this.gameMechanics = gameMechanics;
    }

    public UserDataSet getUserProfile() {
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

    private  void onRoundFinished(Event event) {
        this.notifyClient("round_finished", event.getData());
    }

    private void onNewCurve(Event event){
        this.notifyClient("new_curve", event.getData());
    }

    private void onPromptStatus(Event event) {
        this.notifyClient("prompt_status", event.getData());
    }

    private void onCassandraDecided(Event event) {
        this.notifyClient("cassandra_decided", event.getData());
    }

    private void onChatMessage(Event event) {
        this.notifyClient("chat_message", event.getData());
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
        } else if ( ("chat_message".equals(type)) ||
                    ("prompt_status".equals(type)) ||
                    ("cassandra_decided".equals(type)) ){
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
            this.notifyListeners(type, body);
        } else if ("new_curve".equals(type)) {
            this.notifyListeners("new_curve", body);
        } else if ("round_finished".equals(type)) {
            this.notifyListeners("round_finished", body);
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
        } else if ("new_curve".equals(type)) {
            this.onNewCurve(event);
        } else if ("prompt_status".equals(type)) {
            this.onPromptStatus(event);
        } else if ("cassandra_decided".equals(type)) {
            this.onCassandraDecided(event);
        } else if ("round_finished".equals(type)) {
            this.onRoundFinished(event);
        } else {
            LOGGER.debug("Unknown event: {} {}", type, event.getData());
        }
    }
}
