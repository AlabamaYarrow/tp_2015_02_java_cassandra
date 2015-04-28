package frontend;

import base.AccountService;
import base.ScoreService;
import base.ValidatedServlet;
import com.sun.istack.internal.Nullable;
import main.NoUserException;
import main.Score;
import main.UserProfile;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreListServlet extends ValidatedServlet {

    private static final String[] REQUIRED_FIELDS = {"score", "user_id"};
    private final AccountService accountService;
    private final ScoreService scoreService;

    public ScoreListServlet(ScoreService scoreService, AccountService accountService) {
        super(ScoreListServlet.REQUIRED_FIELDS);
        this.accountService = accountService;
        this.scoreService = scoreService;
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JSONObject json = new JSONObject();
        Map<Object, Object> jsonBody = new HashMap<>();
        json.put("body", jsonBody);

        int status = HttpServletResponse.SC_OK;

        @Nullable
        String limitString = request.getParameter("limit");
        List<Score> scores;
        if (limitString == null) {
            scores = this.scoreService.getScores();
        } else {
            int limit;
            try {
                limit = Integer.decode(limitString);
                scores = this.scoreService.getScores(limit);
            } catch (NumberFormatException e) {
                status = HttpServletResponse.SC_BAD_REQUEST;
                this.respond(json, status, response);
                return;
            }
        }
        List<Map<Object, Object>> scoresList = new ArrayList<>();
        scores.stream().forEach((score) -> scoresList.add(score.getHydrated()));
        jsonBody.put("objects", scoresList);
        this.respond(json, status, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JSONObject json = new JSONObject();
        Map<Object, Object> jsonBody = new HashMap<>();
        json.put("body", jsonBody);

        int status = HttpServletResponse.SC_OK;

        String[] pathParts = request.getPathInfo().split("/");
        Map<Object, Object> requestJson = (Map<Object, Object>) JSONValue.parse(request.getReader());
        if (pathParts.length != 1) {
            status = HttpServletResponse.SC_NOT_FOUND;
        } else if (!this.areRequiredFieldsValid(requestJson, jsonBody)) {
            status = HttpServletResponse.SC_BAD_REQUEST;
        } else {
            try {
                int id = Integer.decode(pathParts[0]);
                UserProfile user = this.accountService.getUserById((int) requestJson.get("user_id"));
                Score score = new Score(user, (int) requestJson.get("score"));
                this.scoreService.addScore(score);
            } catch (NumberFormatException | NoUserException e) {
                status = HttpServletResponse.SC_NOT_FOUND;
            } catch (ClassCastException e) {
                status = HttpServletResponse.SC_BAD_REQUEST;
            }
        }
        json.put("status", status);
        response.setStatus(status);
        response.getWriter().print(json.toJSONString());
    }

    private void respond(JSONObject json, int status, HttpServletResponse response) throws IOException {
        json.put("status", status);
        response.setStatus(status);
        response.getWriter().print(json.toJSONString());
    }
}
