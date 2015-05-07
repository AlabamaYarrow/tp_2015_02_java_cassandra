package frontend;

import base.AccountService;
import main.NoUserException;
import org.json.simple.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SignOutServlet extends HttpServlet {

    private final AccountService accountService;

    public SignOutServlet(AccountService accountService) {
        this.accountService = accountService;
    }

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
        JSONObject json = new JSONObject();
        Map<Object, Object> jsonBody = new HashMap<>();
        json.put("body", jsonBody);

        int status = HttpServletResponse.SC_OK;

        String sid = request.getSession().getId();
        try {
            this.accountService.signOut(sid);
        } catch (NoUserException e) {
            status = HttpServletResponse.SC_UNAUTHORIZED;
        }
        json.put("status", status);
        response.setStatus(status);
        response.getWriter().print(json.toJSONString());
    }
}
