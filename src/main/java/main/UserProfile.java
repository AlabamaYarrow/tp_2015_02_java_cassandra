package main;

public class UserProfile {
    protected String name;
    protected String password;
    protected String email;

    public UserProfile(String name, String password, String email) {
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
}
