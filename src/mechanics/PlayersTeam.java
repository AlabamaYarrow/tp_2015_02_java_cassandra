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

        this.artist.addListener(this);
        this.cassandra.addListener(this);

        this.secret = secret;
        this.notifyPlayerStatus();
    }

    public Map<Object, Object> getRoundHydrated(@Nullable GameWebSocket recipient) {
        Map<Object, Object> round = new HashMap<>();
        if (recipient != this.artist) {
            round.put("artist", this.artist.getUserProfile().getHydrated());
        }
        if (recipient != this.cassandra) {
            round.put("cassandra", this.cassandra.getUserProfile().getHydrated());
            round.put("secret", this.secret);
        }
        if (recipient == this.artist) {
            round.put("role", "artist");
        } else if (recipient == this.cassandra) {
            round.put("role", "cassandra");
        }
        return round;
    }

    @Override
    public void onEvent(Event event) {
        String type = event.getType();
        if ("round_finished".equals(type)) {
            this.onRoundFinished();
        } else if ("new_curve".equals(type)) {
            this.onNewCurve(event);
        } else if ("prompt_status".equals(type)) {
            this.onPromptStatus(event);
        } else if ("cassandra_decided".equals(type)) {
            this.onCassandraDecided(event);
        } else {
            super.onEvent(event);
        }
    }

    private void onRoundFinished() {
        Map<Object, Object> body = new HashMap<>();
        body.put("artist", this.artist.getUserProfile().getHydrated());
        body.put("cassandra", this.cassandra.getUserProfile().getHydrated());
        body.put("secret", this.secret);

        super.notifyListeners("round_finished", body);
    }


    private void onNewCurve(Event event){
        this.notifyListeners(event);
    }

    private void onPromptStatus(Event event) {
        this.notifyListeners(event);
    }

    private void onCassandraDecided(Event event) {
        this.notifyListeners(event);
    }

    public void notifyPlayerStatus() {
        this.notifyListeners("player_status", null);
    }

    @Override
    protected void notifyListeners(String type, Map<Object, Object> data) {
        Event event = new Event(this, type, data);
        this.notifyListeners(event);
    }

    @Override
    protected void notifyListeners(Event event) {
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
