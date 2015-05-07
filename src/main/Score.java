package main;

import base.dataSets.UserDataSet;

import java.util.HashMap;
import java.util.Map;

public class Score implements Comparable {
    private static int id_counter = 0;
    private int id;
    private UserDataSet user;

    private int score;

    public Score(UserDataSet user, int score) {
        this.id = ++Score.id_counter;
        this.score = score;
        this.user = user;
    }

    @Override
    public int compareTo(Object o) {
        Score score = (Score) o;
        return -Integer.compare(this.score, score.getScore());
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Map<Object, Object> getHydrated() {
        Map<Object, Object> hydrated = new HashMap<>();
        this.hydrate(hydrated);
        return hydrated;
    }

    public int getId() {
        return this.id;
    }

    public void hydrate(Map<Object, Object> map) {
        map.put("id", this.id);
        map.put("score", this.score);
        map.put("user", this.user.getHydrated());
    }
}
