package base;

import java.util.HashMap;
import java.util.Map;

public abstract class Hydrateable {

    public Map<Object, Object> getHydrated() {
        Map<Object, Object> hydrated = new HashMap<>();
        this.hydrate(hydrated);
        return hydrated;
    }

    public abstract void hydrate(Map<Object, Object> map);
}
