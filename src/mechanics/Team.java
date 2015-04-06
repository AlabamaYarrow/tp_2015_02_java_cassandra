package mechanics;

import base.GameMechanics;
import com.sun.istack.internal.NotNull;
import frontend.GameWebSocket;

import java.util.*;

public class Team {
    protected List<GameWebSocket> users = new Vector<>();
    protected GameMechanics gameMechanics;
    protected Team viewingAt;
    protected Team viewers;
    protected GameWebSocket artist;
    protected GameWebSocket cassandra;
    protected List<GameWebSocket> judges;
    protected String secret;

    public Team(List<GameWebSocket> users, GameMechanics gameMechanics) {
        this.users = users;
        Collections.shuffle(users);
        this.artist = users.get(0);
        this.cassandra = users.get(1);
        this.judges = users.subList(2, users.size());
        this.gameMechanics = gameMechanics;
        this.notifyPlayerStatus();
    }

    public Team(GameMechanics gameMechanics, Team toViewAt) {
        this.users = new Vector<>();
        this.gameMechanics = gameMechanics;
        this.setViewingAt(toViewAt);
    }

    public Team getViewingAt() {
        return viewingAt;
    }

    public void setViewingAt(Team toViewAt) {
        if (this.viewingAt != null) {
            this.viewingAt.viewers = null;
        }
        this.viewingAt = toViewAt;
        if (toViewAt != null) {
            toViewAt.viewers = this;
        }
    }

    public List<Object> getViewersHydrated() {
        List<Object> viewers = new Vector<>();
        for (Iterator<GameWebSocket> iterator = this.users.iterator(); iterator.hasNext(); ) {
            viewers.add(iterator.next().getUserProfile().getHydrated());
        }
        return viewers;
    }

    public Map<Object, Object> getRoundHydrated(GameWebSocket recipient) {
        Map<Object, Object> round = new HashMap<>();

        if (recipient.getRole() != Role.ARTIST) {
            round.put("artist", this.artist.getUserProfile().getHydrated());
        }
        if (recipient.getRole() != Role.CASSANDRA) {
            round.put("cassandra", this.artist.getUserProfile().getHydrated());
            round.put("secret", this.secret);
        }
        switch (recipient.getRole()) {
            case ARTIST:
                round.put("role", "artist");
                break;
            case CASSANDRA:
                round.put("role", "cassandra");
                break;
            case JUDGE:
                round.put("role", "judge");
                break;
        }
        List<Object> judges = new Vector<>();
        round.put("judges", judges);
        for (Iterator<GameWebSocket> iterator = this.judges.iterator(); iterator.hasNext(); ) {
            GameWebSocket judgeWebSocket = iterator.next();
            if (recipient != judgeWebSocket) {
                judges.add(judgeWebSocket.getUserProfile().getHydrated());
            }
        }
        return round;
    }

    public void add(GameWebSocket viewer) {
        this.users.add(viewer);
        viewer.notifyViewerStatus();
        viewer.setTeam(this);
    }

    public void notifyPlayerStatus() {
        for (Iterator<GameWebSocket> iterator = this.users.iterator(); iterator.hasNext(); ) {
            GameWebSocket user = iterator.next();
            user.notifyPlayerStatus();
        }
    }

    public void onChatTyping(GameWebSocket player) {
        for (Iterator<GameWebSocket> iterator = this.users.iterator(); iterator.hasNext(); ) {
            GameWebSocket user = iterator.next();
            if (player != user) {
                user.notifyChatTyping(player.getUserProfile());
            }
        }
        if (this.viewers != null) {
            this.viewers.onChatTyping(player);
        }
    }

    public void onChatStoppedTyping(GameWebSocket player) {
        for (Iterator<GameWebSocket> iterator = this.users.iterator(); iterator.hasNext(); ) {
            GameWebSocket user = iterator.next();
            if (player != user) {
                user.notifyChatStoppedTyping(player.getUserProfile());
            }
        }
        if (this.viewers != null) {
            this.viewers.onChatStoppedTyping(player);
        }
    }

    public void onChatMessage(@NotNull GameWebSocket player, @NotNull String text) {
        for (Iterator<GameWebSocket> iterator = this.users.iterator(); iterator.hasNext(); ) {
            GameWebSocket user = iterator.next();
            if (player != user) {
                user.notifyChatMessage(player.getUserProfile(), text);
            }
        }
        if (this.viewers != null) {
            this.viewers.onChatMessage(player, text);
        }
    }

    @NotNull
    public List<GameWebSocket> getUsers() {
        return users;
    }

    public void remove(GameWebSocket webSocket) {
        webSocket.setTeam(null);
        this.users.remove(webSocket);
    }
}
