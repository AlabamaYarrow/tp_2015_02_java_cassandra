package base;

import main.NoScoreException;
import main.Score;

import java.util.List;

public interface ScoreService {

    void addScore(Score score);

    Score getScore(int id) throws NoScoreException;

    List<Score> getScores();

    List<Score> getScores(int limit);

    void removeScore(int id) throws NoScoreException;
}
