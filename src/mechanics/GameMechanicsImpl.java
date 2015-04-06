package mechanics;

import base.GameMechanics;
import frontend.GameWebSocket;
import resources.GameResource;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class GameMechanicsImpl implements GameMechanics {

    Deque<Team> teams = new LinkedList<>();
    Team viewersTeam;
    GameResource resource;

    public GameMechanicsImpl(GameResource resource) {
        this.resource = resource;
    }

    @Override
    public void onWebSocketClosed(GameWebSocket webSocket) {
        Team team = webSocket.getTeam();
        if (team == this.viewersTeam) {
            this.viewersTeam.remove(webSocket);
        } else {
            this.teams.remove(team);
            List<GameWebSocket> users = team.getUsers();
            users.remove(webSocket);
            if (this.viewersTeam == null) {
                this.viewersTeam = new Team(this, this.teams.peekFirst());
                /* TODO */
            } else {

            }
        }
    }

    @Override
    public Team addToTeam(GameWebSocket webSocket) {
        if (this.viewersTeam == null) {
            this.viewersTeam = new Team(this, this.teams.peekFirst());
        } else if (this.viewersTeam.getUsers().size() + 1 >= this.resource.judgesCount + 2) {
            this.viewersTeam.setViewingAt(null);
            List<GameWebSocket> users = this.viewersTeam.getUsers();
            this.viewersTeam = null;
            users.add(webSocket);
            Team team = new Team(users, this);
            this.teams.add(team);
            return team;
        }
        this.viewersTeam.add(webSocket);
        return this.viewersTeam;
    }
}
