package frontend;

import base.ScoreService;
import base.ValidatedServlet;
import main.NoScoreException;
import main.Score;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ScoreDetailServlet extends ValidatedServlet {

    private final static String[] REQUIRED_FIELDS = {"score"};
    private final ScoreService scoreService;

    public ScoreDetailServlet(ScoreService scoreService) {
        super(ScoreDetailServlet.REQUIRED_FIELDS);
        this.scoreService = scoreService;
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JSONObject json = new JSONObject();
        Map<Object, Object> jsonBody = new HashMap<>();
        json.put("body", jsonBody);

        int status = HttpServletResponse.SC_OK;

        String[] pathParts = request.getPathInfo().split("/");
        if (pathParts.length == 1) {
            try {
                int id = Integer.decode(pathParts[0]);
                Score score = this.scoreService.getScore(id);
                score.hydrate(jsonBody);
            } catch (NumberFormatException | NoScoreException e) {
                status = HttpServletResponse.SC_NOT_FOUND;
            }
        } else {
            status = HttpServletResponse.SC_NOT_FOUND;
        }
        json.put("status", status);
        response.setStatus(status);
        response.getWriter().print(json.toJSONString());
    }

    public void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
                Score score = this.scoreService.getScore(id);
                score.setScore((int) requestJson.get("score"));
            } catch (NumberFormatException | NoScoreException e) {
                status = HttpServletResponse.SC_NOT_FOUND;
            } catch (ClassCastException e) {
                status = HttpServletResponse.SC_BAD_REQUEST;
            }
        }
        json.put("status", status);
        response.setStatus(status);
        response.getWriter().print(json.toJSONString());
    }

    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JSONObject json = new JSONObject();
        Map<Object, Object> jsonBody = new HashMap<>();
        json.put("body", jsonBody);

        int status = HttpServletResponse.SC_OK;

        String[] pathParts = request.getPathInfo().split("/");
        if (pathParts.length == 1) {
            try {
                int id = Integer.decode(pathParts[0]);
                this.scoreService.removeScore(id);
            } catch (NumberFormatException | NoScoreException e) {
                status = HttpServletResponse.SC_NOT_FOUND;
            }
        } else {
            status = HttpServletResponse.SC_NOT_FOUND;
        }
        json.put("status", status);
        response.setStatus(status);
        response.getWriter().print(json.toJSONString());
    }
}
