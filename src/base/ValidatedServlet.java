package base;

import javax.servlet.http.HttpServlet;
import java.util.HashMap;
import java.util.Map;

public class ValidatedServlet extends HttpServlet {

    protected final String[] REQUIRED_FIELDS;

    protected ValidatedServlet(String[] requiredFields) {
        this.REQUIRED_FIELDS = requiredFields;
    }

    /**
     * @return isValid
     */
    protected boolean areRequiredFieldsValid(Map<Object, Object> request, Map<Object, Object> jsonBody) {
        boolean isValid = true;
        for (String name : this.REQUIRED_FIELDS) {
            Object valueObj = request.get(name);
            String value;
            try {
                value = (String) valueObj;
            } catch (ClassCastException e) {
                isValid = false;
                Map<Object, Object> error = new HashMap<>();
                jsonBody.put(name, error);
                error.put("error", "wrong_type");
                if (!"password".equals(name)) {
                    error.put("value", valueObj);
                }
                continue;
            }
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
