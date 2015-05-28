package resources;

import base.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameResource extends Resource {
    public List<String> words = new ArrayList<>();
    private Random random = new Random();

    public String getWord() {
        return this.words.get(this.random.nextInt(this.words.size()));
    }
}
