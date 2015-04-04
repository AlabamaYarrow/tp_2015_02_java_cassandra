package base;

import main.UserProfile;

public interface AccountService {

    public UserProfile getUser(String sid);

    boolean signIn(String sid, String name, String password);

    long getUsersCount();

    long getOnlineCount();

    void logout(String sid);

    boolean addUser(UserProfile newUser);

    UserProfile getUserByName(String name);
}
