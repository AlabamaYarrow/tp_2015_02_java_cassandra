package main;

import base.ScoreService;
import com.sun.istack.internal.NotNull;

import java.util.*;

public class ScoreServiceImpl implements ScoreService {
    private List<Score> scores = new ArrayList<>();
    private Map<Integer, Score> scoresById = new HashMap<>();

    @Override
    @NotNull
    public Score getScore(int id) throws NoScoreException {
        Score score = this.scoresById.get(id);
        if (score == null) {
            throw new NoScoreException();
        }
        return score;
    }

    @Override
    public void removeScore(int id) throws NoScoreException {
        Score score = this.scoresById.remove(id);
        if (score == null) {
            throw new NoScoreException();
        }
        this.scores.remove(score);
    }

    @Override
    public void addScore(Score score) {
        this.scoresById.put(score.getId(), score);
        this.scores.add(score);
    }

    @Override
    public List<Score> getScores() {
        Collections.sort(this.scores);
        return this.scores;
    }

    @Override
    public List<Score> getScores(int limit) {
        List<Score> scores = this.getScores();
        return scores.subList(0, Math.min(limit, scores.size()));
    }
}
