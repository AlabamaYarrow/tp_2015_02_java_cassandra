package base;

import junit.framework.TestCase;
import main.AccountService;
import main.UserProfile;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public abstract class UserProfileTest extends ServletTest {

    protected AccountService accountService = new AccountService();

    protected static int counter = 0;

    protected HttpServletRequest getSignedInRequest(UserProfile user) {
        HttpServletRequest request = this.getMockedRequest();
        this.accountService.signIn(request.getSession().getId(), user.getName(), "topsecret");
        return request;
    }

    protected static int getUniqueCounter() {
        return ++UserProfileTest.counter;
    }

    protected UserProfile createUserProfile() {
        UserProfile user = new UserProfile("john" + UserProfileTest.getUniqueCounter() + "@mail.com", "john" + UserProfileTest.getUniqueCounter(), "topsecret");
        this.accountService.addUser(user);
        return user;
    }

    protected void checkUserProfileHydrated(UserProfile user, Map<Object, Object> hydrated) {
        TestCase.assertNotNull(hydrated);
        TestCase.assertNotNull(hydrated.get("id"));
        TestCase.assertEquals(user.getEmail(), hydrated.get("email"));
        TestCase.assertEquals(user.getName(), hydrated.get("name"));
        TestCase.assertNull(hydrated.get("password"));
        TestCase.assertEquals((long)user.getScore(), hydrated.get("score"));
    }

}
