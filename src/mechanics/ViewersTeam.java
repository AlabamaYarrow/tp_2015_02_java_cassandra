package mechanics;

import base.Listenable;
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

    protected static final Logger LOGGER = LogManager.getLogger(ViewersTeam.class);
    protected PlayersTeam players;

    public List<Object> getViewersHydrated() {
        return this.users.stream()
                .map(user -> user.getUserProfile().getHydrated())
                .collect(Collectors.toCollection(Vector::new))
                ;
    }

    @Override
    public void onEvent(Event event) {
        Listenable target = event.getTarget();
        if (target instanceof PlayersTeam) {
            String type = event.getType();
            Map<Object, Object> map = new HashMap<>();
            if ("player_status".equals(type)) {
                type = "viewer_status";
                map.put("players", ((PlayersTeam) target).getRoundHydrated(null));
                map.put("viewers", this);
                this.notifyListeners(type, map);
            } else if ("flush".equals(type)) {
                this.onPlayersFlush((PlayersTeam) event.getData());
            }
        } else {
            super.onEvent(event);
        }
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
        data.put("players", this.players == null ? null : this.players.getRoundHydrated(null));
        data.put("viewers", this.getViewersHydrated());
        Event viewerEvent = new Event(event.getTarget(), "viewer_status", data);
        ((GameWebSocket) event.getTarget()).onEvent(viewerEvent);
    }

    protected void onPlayersFlush(PlayersTeam players) {
        this.players = players;
    }
}
