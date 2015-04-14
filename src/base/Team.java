package base;

import frontend.GameWebSocket;
import mechanics.Event;
import mechanics.UnknownEventError;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public abstract class Team implements Listener {
    protected List<GameWebSocket> users = new Vector<>();
    protected GameMechanics gameMechanics;

    public List<GameWebSocket> getUsers() {
        return new Vector<>(this.users);
    }

    public void flush() {
        for (Iterator<GameWebSocket> iterator = this.users.iterator(); iterator.hasNext(); ) {
            iterator.next().removeListener(this);
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
                this.onChatMessage(webSocket, (String) event.getData());
            }
        } else {
            throw new UnknownEventError();
        }
    }

    private void onClosed(GameWebSocket webSocket) {
        this.notifyListeners("user_gone", null);
    }

    private void onConnected(GameWebSocket webSocket) {
        this.notifyListeners("user_come", null);
    }

    protected abstract void onChatTyping(GameWebSocket webSocket);

    protected abstract void onChatStoppedTyping(GameWebSocket webSocket);

    protected abstract void onChatMessage(GameWebSocket webSocket, String text);

    protected abstract void notifyListeners(String type, Map<Object, Object> data);
}
