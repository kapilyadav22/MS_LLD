//package Games.WordPuzzle;
//
//import java.util.*;
//
//enum GameStatus { IN_PROGRESS, WON, LOST }
//enum CellColor { GREEN, YELLOW, GRAY }
//
//
//class GuessResult {
//    CellColor[] feedback;
//    boolean isWin;
//
//    GuessResult(CellColor[] feedback) {
//        this.feedback = feedback;
//        this.isWin = Arrays.stream(feedback).allMatch(c -> c == CellColor.GREEN);
//    }
//}
//
//class UserProgress {
//    int attempts = 0;
//    GameStatus status = GameStatus.IN_PROGRESS;
//    List<String> guesses = new ArrayList<>();
//}
//
//class GameService {
//    private static final int MAX_ATTEMPTS = 6;
//    private String dailyWord = "CRANE";
//    private Map<String, UserProgress> games = new HashMap<>();
//
//    GuessResult submitGuess(String userId, String guess) {
//        guess = guess.toUpperCase();
//
//        UserProgress progress = games.computeIfAbsent(userId, k -> new UserProgress());
//
//        if (progress.status != GameStatus.IN_PROGRESS || progress.attempts >= MAX_ATTEMPTS) {
//            throw new IllegalStateException("Game over");
//        }
//
//        GuessResult result = evaluate(dailyWord, guess);
//        progress.attempts++;
//        progress.guesses.add(guess);
//
//        if (result.isWin) {
//            progress.status = GameStatus.WON;
//        } else if (progress.attempts >= MAX_ATTEMPTS) {
//            progress.status = GameStatus.LOST;
//        }
//
//        return result;
//    }
//
//    private GuessResult evaluate(String word, String guess) {
//        int n = word.length();
//        CellColor[] feedback = new CellColor[n];
//
//        Map<Character, Integer> freq = new HashMap<>();
//        for (char c : word.toCharArray()) {
//            freq.put(c, freq.getOrDefault(c, 0) + 1);
//        }
//
//        // GREEN pass
//        for (int i = 0; i < n; i++) {
//            if (word.charAt(i) == guess.charAt(i)) {
//                feedback[i] = CellColor.GREEN;
//                freq.put(word.charAt(i), freq.get(word.charAt(i)) - 1);
//            }
//        }
//
//        // YELLOW/GRAY pass
//        for (int i = 0; i < n; i++) {
//            if (feedback[i] == CellColor.GREEN) continue;
//            char c = guess.charAt(i);
//            if (freq.getOrDefault(c, 0) > 0) {
//                feedback[i] = CellColor.YELLOW;
//                freq.put(c, freq.get(c) - 1);
//            } else {
//                feedback[i] = CellColor.GRAY;
//            }
//        }
//
//        return new GuessResult(feedback);
//    }
//}
//
//public class DailyWordPuzzleGame {
//    public static void main(String[] args) {
//        GameService game = new GameService();
//
//        GuessResult r1 = game.submitGuess("alice", "TRAIN");
//        System.out.println("Guess 1: " + Arrays.toString(r1.feedback));
//
//        GuessResult r2 = game.submitGuess("alice", "CRANE");
//        System.out.println("Guess 2: " + Arrays.toString(r2.feedback));
//        System.out.println("Win: " + r2.isWin);
//    }
//}
//
//
////        "For thread-safety, I'd use ConcurrentHashMap + synchronized"
////        "For daily words, I'd use LocalDate-based selection"
////        "For production, I'd add Redis, DB, validation"