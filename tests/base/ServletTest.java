package base;

import junit.framework.TestCase;
import org.json.simple.JSONValue;
import org.mockito.invocation.InvocationOnMock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.Map;

import static org.mockito.Mockito.*;

public abstract class ServletTest extends TestCase {

    protected void checkStatusCode(int statusCode, HttpServletResponse response) {
        Map<Object, Object> json = (Map<Object, Object>) JSONValue.parse(response.toString());
        verify(response).setStatus(statusCode);
        assertEquals((long) statusCode, json.get("status"));
    }

    protected HttpServletRequest getMockedRequest(String json) throws IOException {
        HttpSession session = mock(HttpSession.class);
        when(session.getId()).thenReturn("some_session_id");

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getSession()).thenReturn(session);

        BufferedReader reader = new BufferedReader(new StringReader(json));
        when(request.getReader()).thenReturn(reader);

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
