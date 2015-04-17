package base;

import com.sun.istack.internal.Nullable;
import frontend.GameWebSocket;
import mechanics.Event;
import mechanics.PlayersTeam;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public abstract class Team implements Listener {
    private static final Logger LOGGER = LogManager.getLogger(Team.class);
    protected List<GameWebSocket> users = new Vector<>();

    public List<GameWebSocket> getUsers() {
        return new Vector<>(this.users);
    }

    public void flush(@Nullable PlayersTeam playersTeam) {
        Map<Object, Object> data = new HashMap<>();
        data.put("players", playersTeam);
        this.notifyListeners("flush", data);
        for (GameWebSocket user : this.users) {
            user.removeListener(this);
        }
        this.users.clear();
    }

    public void onEvent(Event event) {
        String type = event.getType();
        if ("connected".equals(type)) {
            this.onConnected(event);
        } else if ("closed".equals(type)) {
            this.onClosed(event);
        } else if ("chat_typing".equals(type)) {
            this.onChatTyping(event);
        } else if ("chat_stopped_typing".equals(type)) {
            this.onChatStoppedTyping(event);
        } else if ("chat_message".equals(type)) {
            this.onChatMessage(event);
        } else {
            LOGGER.error("Unknown event: {} {}", type, event.getData());
        }
    }

    protected void onClosed(Event event) {
        this.notifyListeners(event);
    }

    protected void onChatTyping(Event event) {
        this.notifyListeners(event);
    }

    protected void onChatStoppedTyping(Event event) {
        this.notifyListeners(event);
    }

    protected void onChatMessage(Event event) {
        this.notifyListeners(event);
    }

    protected void onConnected(Event event) {
        this.users.stream()
                .filter(player -> player != event.getTarget())
                .forEach(player -> player.onEvent(event))
        ;
    }

    protected void notifyListeners(String type, Map<Object, Object> data) {
        Event event = new Event(null, type, data);
        this.notifyListeners(event);
    }

    protected void notifyListeners(Event event) {
        this.users.stream()
                .filter(user -> user != event.getTarget())
                .forEach(user -> user.onEvent(event))
        ;
    }
}
