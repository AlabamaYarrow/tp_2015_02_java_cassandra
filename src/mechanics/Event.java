package mechanics;

import base.Listenable;

import java.util.Map;

public class Event {
    private Listenable target;
    private String type;
    private Map<Object, Object> data;

    public Event(Listenable target, String type, Map<Object, Object> data) {
        this.target = target;
        this.type = type;
        this.data = data;
    }

    public Listenable getTarget() {
        return this.target;
    }

    public String getType() {
        return this.type;
    }

    public Map<Object, Object> getData() {
        return this.data;
    }
}
