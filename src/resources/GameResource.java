package resources;

import base.Resource;

public class GameResource extends Resource {
    public int judgesCount;
    public int judgeBonus;
    protected String name;

    @Override
    public String getName() {
        return name;
    }
}
