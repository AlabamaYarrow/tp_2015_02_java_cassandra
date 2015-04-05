package base;

import main.NoUserException;
import main.SignInException;
import main.UserProfile;

public interface AccountService {

    public UserProfile getUser(String sid) throws NoUserException;

    UserProfile signIn(String sid, String name, String password) throws SignInException;

    long getUsersCount();

    long getOnlineCount();

    void signOut(String sid) throws NoUserException;

    boolean addUser(UserProfile newUser);

    UserProfile getUserByName(String name) throws NoUserException;
}
