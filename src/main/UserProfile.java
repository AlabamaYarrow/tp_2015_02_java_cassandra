package main;

import java.util.HashMap;
import java.util.Map;

public class UserProfile {
    private static int idCounter = 0;
    private int id;
    private String email;
    private String name;
    private String password;
    private int scoreTotal;

    public UserProfile(String email, String name, String password) {
        this.id = UserProfile.getUniqueId();
        this.email = email;
        this.name = name;
        this.password = password;
        this.scoreTotal = 0;
    }

    private static int getUniqueId() {
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

    public int getScoreTotal() {
        return this.scoreTotal;
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
        map.put("scoreTotal", this.scoreTotal);
    }
}
