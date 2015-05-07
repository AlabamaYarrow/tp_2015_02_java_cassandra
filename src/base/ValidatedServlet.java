package base;

import javax.servlet.http.HttpServlet;
import java.util.HashMap;
import java.util.Map;

public class ValidatedServlet extends HttpServlet {

    private final String[] REQUIRED_FIELDS;

    protected ValidatedServlet(String[] requiredFields) {
        this.REQUIRED_FIELDS = requiredFields;
    }

    /**
     * @return isValid
     */
    protected boolean areRequiredFieldsValid(Map<Object, Object> request, Map<Object, Object> jsonBody) {
        boolean isValid = true;
        for (String name : this.REQUIRED_FIELDS) {
            boolean integerType = name.startsWith("int ");
            if (integerType) {
                name = name.substring(4, name.length());
            }
            Object valueObj = request.get(name);
            try {
                if (integerType) {
                    long value = (Long) valueObj;
                } else {
                    String value = (String) valueObj;
                }
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
            if (valueObj == null || (!integerType && ((String) valueObj).isEmpty())) {
                isValid = false;
                Map<Object, Object> error = new HashMap<>();
                jsonBody.put(name, error);
                error.put("error", "required");
                if (!"password".equals(name)) {
                    error.put("value", valueObj);
                }
            }
        }
        return isValid;
    }
}
