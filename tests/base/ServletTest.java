package base;

import junit.framework.TestCase;
import org.mockito.invocation.InvocationOnMock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import static org.mockito.Mockito.*;

public abstract class ServletTest extends TestCase {

    protected void checkStatusCode(int statusCode, HttpServletResponse response, Map<Object, Object> json) {
        verify(response).setStatus(statusCode);
        assertEquals((long) statusCode, json.get("status"));
    }

    protected HttpServletRequest getMockedRequest() {
        HttpSession session = mock(HttpSession.class);
        when(session.getId()).thenReturn("some_session_id");

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getSession()).thenReturn(session);

        return request;
    }

    protected HttpServletResponse getMockedResponse() throws IOException {
        StringWriter stringWriter = new StringWriter();
        HttpServletResponse response = mock(HttpServletResponse.class);

        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        when(response.toString()).thenAnswer((InvocationOnMock invocationOnMock) -> stringWriter.toString());

        return response;
    }
}
