package main;

import java.util.HashMap;
import java.util.Map;

public class UserProfile {
    protected static int idCounter = 0;
    protected int id;
    protected String email;
    protected String name;
    protected String password;
    protected int score;

    public UserProfile(String email, String name, String password) {
        this.id = UserProfile.getUniqueId();
        this.email = email;
        this.name = name;
        this.password = password;
        this.score = 0;
    }

    protected static int getUniqueId() {
        return ++UserProfile.idCounter;
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    public String getEmail() {
        return this.email;
    }

    public int getID() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int getScore() {
        return this.score;
    }

    public Map<Object, Object> getHydrated() {
        Map<Object, Object> map = new HashMap<>();
        this.hydrate(map);
        return map;
    }

    public void hydrate(Map<Object, Object> map) {
        map.put("id", this.id);
        map.put("email", this.email);
        map.put("name", this.name);
        map.put("score", this.score);
    }
}
