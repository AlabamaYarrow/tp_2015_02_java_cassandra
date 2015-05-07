package mechanics;

import base.Listenable;
import base.Listener;
import base.Team;
import com.sun.istack.internal.Nullable;
import frontend.GameWebSocket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class PlayersTeam extends Team implements Listenable {
    private static final Logger LOGGER = LogManager.getLogger(PlayersTeam.class);
    private GameWebSocket artist;
    private GameWebSocket cassandra;
    private String secret;
    private List<Listener> listeners = new CopyOnWriteArrayList<>();

    public PlayersTeam(List<GameWebSocket> users, String secret) {
        this.users = users;
        Collections.shuffle(users);
        this.artist = users.get(0);
        this.cassandra = users.get(1);
        this.secret = secret;
        this.notifyPlayerStatus();
    }

    public Map<Object, Object> getRoundHydrated(@Nullable GameWebSocket recipient) {
        Map<Object, Object> round = new HashMap<>();
        if (recipient != this.artist) {
            round.put("artist", this.artist.getUserProfile().getHydrated());
        }
        if (recipient != this.cassandra) {
            round.put("cassandra", this.artist.getUserProfile().getHydrated());
            round.put("secret", this.secret);
        }
        if (recipient == this.artist) {
            round.put("role", "artist");
        } else if (recipient == this.cassandra) {
            round.put("role", "cassandra");
        }
        return round;
    }

    public void notifyPlayerStatus() {
        this.notifyListeners("player_status", null);
    }

    @Override
    protected void notifyListeners(String type, Map<Object, Object> data) {
        Event event = new Event(this, type, data);
        super.notifyListeners(event);
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
}
