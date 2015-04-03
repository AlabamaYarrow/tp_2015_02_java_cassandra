package frontend;

import base.ServletTest;
import main.AccountService;
import org.junit.Test;
import templater.PageGenerator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static org.mockito.Mockito.*;

public class AdminServletTest extends ServletTest {

    @Test
    public void testDoGet() throws Exception {
        AccountService accountService = mock(AccountService.class);
        final long ONLINE_COUNT = 314159;
        final long USERS_COUNT = 3141593;
        when(accountService.getOnlineCount()).thenReturn(ONLINE_COUNT);
        when(accountService.getUsersCount()).thenReturn(USERS_COUNT);
        PageGenerator pageGenerator = mock(PageGenerator.class);
        AdminServlet admin = new AdminServlet(accountService, pageGenerator, new Timer());

        HttpServletResponse response = this.getMockedResponse();
        HttpServletRequest request = this.getMockedRequest();

        admin.doGet(request, response);
        Map<String, Object> map = new HashMap<>();
        map.put("online_count", ONLINE_COUNT);
        map.put("registered_count", USERS_COUNT);
        verify(pageGenerator).getPage("admin.ftl", map);
    }

    @Test
    public void testDoPostBadRequest() throws Exception {
        AccountService accountService = mock(AccountService.class);
        PageGenerator pageGenerator = mock(PageGenerator.class);
        Timer timer = mock(Timer.class);
        AdminServlet admin = new AdminServlet(accountService, pageGenerator, timer);

        HttpServletResponse response = this.getMockedResponse();
        HttpServletRequest request = this.getMockedRequest();

        admin.doPost(request, response);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoPostOK() throws Exception {
        AccountService accountService = mock(AccountService.class);
        PageGenerator pageGenerator = mock(PageGenerator.class);
        Timer timer = mock(Timer.class);
        AdminServlet admin = new AdminServlet(accountService, pageGenerator, timer);

        HttpServletResponse response = this.getMockedResponse();
        HttpServletRequest request = this.getMockedRequest();
        final Long DELAY = 31416L;
        when(request.getParameter("delay")).thenReturn(DELAY.toString());

        admin.doPost(request, response);
        Map<String, Object> map = new HashMap<>();
        map.put("stopping", true);
        verify(pageGenerator).getPage("admin.ftl", map);
        verify(timer).schedule(any(TimerTask.class), eq(DELAY));
    }
}