package Games.WordPuzzle;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/*
1. Clarify Requirements (Always do this first)
Functional Requirements
    One daily word puzzle (same word for all users per day)
    Fixed word length (e.g., 5 letters)
    User can make limited attempts (e.g., 6)
    After each guessWord:
        Correct letter & position → Green
        Correct letter, wrong position → Yellow
        Wrong letter → Gray
Puzzle resets once per day (UTC)
Track:
    Win / Lose
    Attempts used
Show result after completion

Non-Functional Requirements
    High read traffic (millions of users at same time)
    Low latency (<100 ms)
    Highly available
    Cheating prevention (same puzzle per day)
    Scalable globally
 */

enum GameStatus {
    IN_PROGRESS, WON, LOST
}

enum CellColor {
    GREEN, YELLOW, GRAY
}

class TurnResult {
    CellColor[] feedback;
    boolean isWin;

    TurnResult(CellColor[] feedback, boolean isWin) {
        this.feedback = feedback;
        this.isWin = isWin;
    }
}

class UserProgress {
    int attempts = 0;
    GameStatus status = GameStatus.IN_PROGRESS;
    List<String> guesses = new ArrayList<>();
}

interface GuessEvaluator {
    TurnResult evaluate(String word, String guessWord);
}

interface GameRules {
    void validate(UserProgress progress);
    GameStatus nextStatus(UserProgress progress, TurnResult result);
}

class DefaultGuessEvaluator implements GuessEvaluator {

    public TurnResult evaluate(String word, String guessWord) {
        int n = word.length();
        CellColor[] feedback = new CellColor[n];
        boolean isWin = true;

        Map<Character, Integer> freq = new HashMap<>();
        for (int i = 0; i < n; i++) {
            char c = word.charAt(i);
            freq.put(c, freq.getOrDefault(c, 0) + 1);
        }

        // GREEN pass
        for (int i = 0; i < n; i++) {
            if (word.charAt(i) == guessWord.charAt(i)) {
                feedback[i] = CellColor.GREEN;
                freq.put(word.charAt(i), freq.get(word.charAt(i)) - 1);
            }
        }

        // YELLOW / GRAY pass + win detection
        for (int i = 0; i < n; i++) {
            if (feedback[i] == CellColor.GREEN) {
                continue;
            }

            char c = guessWord.charAt(i);
            if (freq.getOrDefault(c, 0) > 0) {
                feedback[i] = CellColor.YELLOW;
                freq.put(c, freq.get(c) - 1);
            } else {
                feedback[i] = CellColor.GRAY;
            }
            isWin = false;
        }

        return new TurnResult(feedback, isWin);
    }
}

class DefaultGameRules implements GameRules {
    private final int maxAttempts;

    DefaultGameRules(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public void validate(UserProgress progress) {
        if (progress.status != GameStatus.IN_PROGRESS ||
                progress.attempts >= maxAttempts) {
            throw new IllegalStateException("Game over");
        }
    }

    public GameStatus nextStatus(UserProgress progress, TurnResult result) {
        if (result.isWin) {
            return GameStatus.WON;
        }
        if (progress.attempts >= maxAttempts) {
            return GameStatus.LOST;
        }
        return GameStatus.IN_PROGRESS;
    }
}

class GameService {

    private final String dailyWord;
    private final GuessEvaluator evaluator;
    private final GameRules rules;

    private final Map<String, UserProgress> userProgressMap = new ConcurrentHashMap<>();
    private final Map<String, ReentrantLock> locks = new ConcurrentHashMap<>();

    GameService(String dailyWord,GuessEvaluator evaluator,GameRules rules) {

        this.dailyWord = dailyWord.toUpperCase();
        this.evaluator = evaluator;
        this.rules = rules;
    }

    TurnResult submitGuess(String userId, String guessWord) {
        guessWord = guessWord.toUpperCase();

        ReentrantLock lock = locks.computeIfAbsent(userId, k -> new ReentrantLock());
        lock.lock();
        try {
            UserProgress progress = userProgressMap.get(userId);
            if (progress == null) {
                progress = new UserProgress();
                userProgressMap.put(userId, progress);
            }

            rules.validate(progress);
            TurnResult result = evaluator.evaluate(dailyWord, guessWord);

            progress.attempts++;
            progress.guesses.add(guessWord);
            progress.status = rules.nextStatus(progress, result);

            return result;
        } finally {
            lock.unlock();
        }
    }
}

public class DailyWordPuzzleGame1 {
    public static void main(String[] args) {
        GameService game = new GameService("CRANE", new DefaultGuessEvaluator(),new DefaultGameRules(3));

        TurnResult r1 = game.submitGuess("alice", "TRAIN");
        System.out.println("Guess 1: " + Arrays.toString(r1.feedback));
        System.out.println("Is r1 Win: " + r1.isWin);
        TurnResult r2 = game.submitGuess("alice", "asdsd");
        System.out.println("Guess 2: " + Arrays.toString(r2.feedback));
        System.out.println("is r2 Win: " + r2.isWin);

        TurnResult r3 = game.submitGuess("alice", "CRANE");
        System.out.println("Guess 3: " + Arrays.toString(r3.feedback));
        System.out.println("is r3 Win: " + r3.isWin);

    }
}
