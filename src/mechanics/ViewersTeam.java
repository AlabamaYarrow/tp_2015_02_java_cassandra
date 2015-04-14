package mechanics;

import base.Listenable;
import base.Team;
import frontend.GameWebSocket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class ViewersTeam extends Team {

    protected static final Logger LOGGER = LogManager.getLogger(ViewersTeam.class);

    public List<Object> getViewersHydrated() {
        List<Object> viewers = new Vector<>();
        for (Iterator<GameWebSocket> iterator = this.users.iterator(); iterator.hasNext(); ) {
            viewers.add(iterator.next().getUserProfile().getHydrated());
        }
        return viewers;
    }

    @Override
    public void onEvent(Event event) {
        Listenable target = event.getTarget();
        if (target instanceof PlayersTeam) {
            String type = event.getType();
            Map<Object, Object> map = new HashMap<>();
            Object data = event.getData();
            if ("player_status".equals(type)) {
                type = "viewer_status";
                map.put("players", target);
                map.put("viewers", this);
            }
            this.notifyListeners(type, map);
        } else {
            super.onEvent(event);
        }
    }

    @Override
    protected void notifyListeners(String type, Map<Object, Object> map) {
        Event event = new Event(null, type, map);
        for (GameWebSocket webSocket : this.users) {
            webSocket.onEvent(event);
        }
    }

    public void add(GameWebSocket viewer) {
        this.users.add(viewer);
        viewer.addListener(this);
        viewer.notifyClientViewerStatus();
    }

    public void remove(GameWebSocket webSocket) {
        webSocket.removeListener(this);
        this.users.remove(webSocket);
    }

    protected void onChatMessage(GameWebSocket webSocket, String data) {
        LOGGER.error("Viewer can't send chat messages.");
        webSocket.closeSession();
    }

    protected void onChatStoppedTyping(GameWebSocket webSocket) {
        LOGGER.error("Viewer can't type chat messages.");
        webSocket.closeSession();
    }

    protected void onChatTyping(GameWebSocket webSocket) {
        LOGGER.error("Viewer can't type chat messages.");
        webSocket.closeSession();
    }
}
