package resources;

import base.Resource;

import java.util.*;

public class GameResource extends Resource {
    public List<String> words = new ArrayList<>();
    private Iterator<String> wordsIterator;

    public String getWord() {
        if (this.wordsIterator == null) {
            Collections.shuffle(this.words);
            this.wordsIterator = this.words.iterator();
        }
        try {
            return this.wordsIterator.next();
        } catch (NoSuchElementException e) {
            Collections.shuffle(this.words);
            this.wordsIterator = this.words.iterator();
            return this.wordsIterator.next();
        }
    }
}
