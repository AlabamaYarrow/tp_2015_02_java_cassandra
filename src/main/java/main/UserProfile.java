package main;

import base.Hydrateable;

import java.util.Map;

public class UserProfile extends Hydrateable {
    protected static int idCounter = 0;
    protected int id;
    protected String name;
    protected String password;
    protected String email;

    public UserProfile(String name, String password, String email) {
        this.id = UserProfile.getUniqueId();
        this.name = name;
        this.password = password;
        this.email = email;
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    protected static int getUniqueId() {
        return ++UserProfile.idCounter;
    }

    @Override
    public void hydrate(Map<Object, Object> map) {
        map.put("id", this.id);
        map.put("name", this.name);
        map.put("email", this.email);
    }
}
