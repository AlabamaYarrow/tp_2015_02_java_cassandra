package main;

import base.Hydrateable;

import java.util.Map;

public class UserProfile extends Hydrateable {
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

    protected static int getUniqueId() {
        return ++UserProfile.idCounter;
    }

    @Override
    public void hydrate(Map<Object, Object> map) {
        map.put("id", this.id);
        map.put("email", this.email);
        map.put("name", this.name);
        map.put("score", this.score);
    }
}
