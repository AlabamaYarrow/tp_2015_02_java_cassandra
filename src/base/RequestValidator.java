package base;

import java.util.HashMap;
import java.util.Map;

public class RequestValidator {

    private String[] requiredFields;

    public RequestValidator(String[] requiredFields) {
        this.requiredFields = requiredFields;
    }

    /**
     * @return isValid
     */
    public boolean areRequiredFieldsValid(Map<Object, Object> request, Map<Object, Object> jsonBody) {
        boolean isValid = true;
        for (String name : this.requiredFields) {
            boolean integerType = name.startsWith("int ");
            if (integerType) {
                name = name.substring(4, name.length());
            }
            Object valueObj = request.get(name);
            try {
                if (valueObj == null || (!integerType && ((String) valueObj).isEmpty())) {
                    isValid = false;
                    Map<Object, Object> error = new HashMap<>();
                    jsonBody.put(name, error);
                    error.put("error", "required");
                    if (!"password".equals(name)) {
                        error.put("value", valueObj);
                    }
                    continue;
                }
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
        }
        return isValid;
    }
}
