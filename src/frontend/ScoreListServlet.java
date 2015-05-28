package frontend;

import base.AccountService;
import base.RequestValidator;
import base.ScoreService;
import base.dataSets.UserDataSet;
import com.sun.istack.internal.Nullable;
import main.NoUserException;
import main.Score;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreListServlet extends HttpServlet {

    private static final String[] REQUIRED_FIELDS = {"int score", "int user_id"};
    private static final RequestValidator VALIDATOR = new RequestValidator(REQUIRED_FIELDS);
    private final AccountService accountService;
    private final ScoreService scoreService;

    public ScoreListServlet(ScoreService scoreService, AccountService accountService) {
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

        Map<Object, Object> requestJson = (Map<Object, Object>) JSONValue.parse(request.getReader());
        if (ScoreListServlet.VALIDATOR.areRequiredFieldsValid(requestJson, jsonBody)) {
            try {
                long userId = (Long) requestJson.get("user_id");
                UserDataSet user = this.accountService.getUserById((int) userId);
                long scoreValue = (Long) requestJson.get("score");
                Score score = new Score(user, (int) scoreValue);
                this.scoreService.addScore(score);
                score.hydrate(jsonBody);
            } catch (NoUserException e) {
                status = HttpServletResponse.SC_NOT_FOUND;
            }
        } else {
            status = HttpServletResponse.SC_BAD_REQUEST;
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
