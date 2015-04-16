package base;

import main.AccountServiceImpl;
import main.UserProfile;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public abstract class UserProfileTest extends ServletTest {

    protected static int counter = 0;
    protected AccountService accountService = new AccountServiceImpl();

    protected static int getUniqueCounter() {
        return ++UserProfileTest.counter;
    }

    protected HttpServletRequest getSignedInRequest(UserProfile user, String json) throws Exception {
        HttpServletRequest request = this.getMockedRequest(json);
        this.accountService.signIn(request.getSession().getId(), user.getName(), "topsecret");
        return request;
    }

    protected UserProfile createUserProfile() {
        UserProfile user = new UserProfile("john" + UserProfileTest.getUniqueCounter() + "@mail.com", "john" + UserProfileTest.getUniqueCounter(), "topsecret");
        this.accountService.addUser(user);
        return user;
    }

    protected void checkUserProfileHydrated(UserProfile user, Map<Object, Object> hydrated) {
        assertNotNull(hydrated);
        assertNotNull(hydrated.get("id"));
        assertEquals(user.getEmail(), hydrated.get("email"));
        assertEquals(user.getName(), hydrated.get("name"));
        assertNull(hydrated.get("password"));
        assertEquals((long) user.getScore(), hydrated.get("score"));
    }

}
