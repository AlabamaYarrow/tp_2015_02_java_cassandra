package mechanics;

import base.Team;
import frontend.GameWebSocket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Collectors;

public class ViewersTeam extends Team {

    private static final Logger LOGGER = LogManager.getLogger(ViewersTeam.class);
    private PlayersTeam players;

    private List<Object> getViewersHydrated() {
        return this.users.stream()
                .map(user -> user.getUserProfile().getHydrated())
                .collect(Collectors.toCollection(Vector::new))
                ;
    }

    @Override
    public void onEvent(Event event) {
        String type = event.getType();
        if ("player_status".equals(type)) {
            this.onPlayerStatus(event);
        } else if ("flush".equals(type)) {
            this.onFlush(event);
        } else {
            super.onEvent(event);
        }
    }

    private void onPlayerStatus(Event event) {
        Map<Object, Object> data = new HashMap<>();
        data.put("round", ((PlayersTeam) event.getTarget()).getRoundHydrated(null));
        data.put("viewers", this.getViewersHydrated());
        this.notifyListeners("viewer_status", data);
    }

    public void add(GameWebSocket viewer) {
        this.users.add(viewer);
        viewer.addListener(this);
        Event event = new Event(viewer, "connected", null);
        this.onEvent(event);
    }

    public void remove(GameWebSocket viewer) {
        viewer.removeListener(this);
        this.users.remove(viewer);
    }

    @Override
    protected void onConnected(Event event) {
        super.onConnected(event);
        Map<Object, Object> data = new HashMap<>();
        data.put("round", this.players == null ? null : this.players.getRoundHydrated(null));
        data.put("viewers", this.getViewersHydrated());
        Event viewerEvent = new Event(event.getTarget(), "viewer_status", data);
        ((GameWebSocket) event.getTarget()).onEvent(viewerEvent);
    }

    private void onFlush(Event event) {
        this.players = (PlayersTeam) event.getData().get("players");
    }
}
