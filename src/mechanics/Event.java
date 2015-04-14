package mechanics;

import base.Listenable;

public class Event {
    protected Listenable target;
    protected String type;
    protected Object data;

    public Event(Listenable target, String type, Object data) {
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

    public Object getData() {
        return this.data;
    }
}
