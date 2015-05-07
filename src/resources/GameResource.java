package resources;

import base.Resource;

import java.util.ArrayList;
import java.util.List;

public class GameResource extends Resource {
    public List<String> words = new ArrayList<>();
    private String path;

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public void setPath(String path) {
        this.path = path;
    }
}
