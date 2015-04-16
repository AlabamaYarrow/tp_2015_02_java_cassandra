package base;

import com.sun.istack.internal.Nullable;
import frontend.GameWebSocket;
import mechanics.Event;
import mechanics.PlayersTeam;
import mechanics.UnknownEventError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public abstract class Team implements Listener {
    protected List<GameWebSocket> users = new Vector<>();
    protected GameMechanics gameMechanics;

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
        Listenable target = event.getTarget();
        if (target instanceof GameWebSocket) {
            GameWebSocket webSocket = (GameWebSocket) target;
            if ("connected".equals(type)) {
                this.onConnected(webSocket);
            } else if ("closed".equals(type)) {
                this.onClosed(webSocket);
            } else if ("chat_typing".equals(type)) {
                this.onChatTyping(webSocket);
            } else if ("chat_stopped_typing".equals(type)) {
                this.onChatStoppedTyping(webSocket);
            } else if ("chat_message".equals(type)) {
                this.onChatMessage(webSocket, (String) event.getData().get("text"));
            }
        } else {
            throw new UnknownEventError();
        }
    }

    protected void onClosed(GameWebSocket webSocket) {
        this.notifyListeners("user_gone", null);
    }

    protected abstract void onChatTyping(GameWebSocket webSocket);

    protected abstract void onChatStoppedTyping(GameWebSocket webSocket);

    protected abstract void onChatMessage(GameWebSocket webSocket, String text);

    protected void onConnected(GameWebSocket webSocket) {
        Event connectedEvent = new Event(webSocket, "connected", null);
        this.users.stream()
                .filter(player -> player != webSocket)
                .forEach(player -> player.onEvent(connectedEvent))
        ;
    }

    protected abstract void notifyListeners(String type, Map<Object, Object> data);
}
