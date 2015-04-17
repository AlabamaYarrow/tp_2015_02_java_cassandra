package mechanics;

import base.Listenable;
import base.Listener;
import base.Team;
import com.sun.istack.internal.Nullable;
import frontend.GameWebSocket;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class PlayersTeam extends Team implements Listenable {
    protected GameWebSocket artist;
    protected GameWebSocket cassandra;
    protected List<GameWebSocket> judges;
    protected String secret;
    protected List<Listener> listeners = new CopyOnWriteArrayList<>();

    public PlayersTeam(List<GameWebSocket> users) {
        this.users = users;
        Collections.shuffle(users);
        this.artist = users.get(0);
        this.cassandra = users.get(1);
        this.judges = users.subList(2, users.size());
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
        } else if (this.judges.contains(recipient)) {
            round.put("role", "judge");
        }
        List<Object> judges = new Vector<>();
        round.put("judges", judges);
        judges.addAll(this.judges.stream()
                        .filter(judgeWebSocket -> recipient != judgeWebSocket)
                        .map(judgeWebSocket -> judgeWebSocket.getUserProfile().getHydrated())
                        .collect(Collectors.toList())
        );
        return round;
    }

    public void notifyPlayerStatus() {
        this.notifyListeners("player_status", null);
    }

    @Override
    protected void notifyListeners(String type, Map<Object, Object> data) {
        super.notifyListeners(type, data);
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
}