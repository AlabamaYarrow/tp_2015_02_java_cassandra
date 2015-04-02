package base;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class ValidatedServlet extends HttpServlet {

    protected final String[] REQUIRED_FIELDS;

    protected ValidatedServlet (String[] requiredFields) {
        this.REQUIRED_FIELDS = requiredFields;
    }

    /**
     * @return isValid
     */
    protected boolean areRequiredFieldsValid(HttpServletRequest request, Map<Object, Object> jsonBody) {
        boolean isValid = true;
        for (String name: this.REQUIRED_FIELDS) {
            String value = request.getParameter(name);
            if (null == value || value.isEmpty()) {
                isValid = false;
                Map<Object, Object> error = new HashMap<>();
                jsonBody.put(name, error);
                error.put("error", "required");
                if (!"password".equals(name)) {
                    error.put("value", value);
                }
            }
        }
        return isValid;
    }
}
