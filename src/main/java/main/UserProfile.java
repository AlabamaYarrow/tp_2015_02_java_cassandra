package main;

public class UserProfile {
    protected String login;
    protected String password;
    protected String email;

    public UserProfile(String login, String password, String email) {
        this.login = login;
        this.password = password;
        this.email = email;
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    public String getLogin() {
        return login;
    }

    public String getEmail() {
        return email;
    }
}
