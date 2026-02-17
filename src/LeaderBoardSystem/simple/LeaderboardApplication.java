package LeaderBoardSystem.simple;

import java.util.*;

/*
Extensible Leaderboard Design
- Optimized for Top-K queries
- Can be extended for rank / multi-game / different strategies
*/

interface RankingStrategy {
    void updateScore(String playerId, int score);
    List<String> getTopK();
}

class MinHeapRankingStrategy implements RankingStrategy {

    private final int k;
    private final PriorityQueue<PlayerScore> minHeap;

    // Comparator defines ranking + tie-breaking
    private static final Comparator<PlayerScore> MIN_HEAP_COMPARATOR =
            (p1, p2) -> {
                if (p1.score != p2.score) {
                    return p1.score - p2.score;   // lower score = weaker
                }
                return p1.playerId.compareTo(p2.playerId); // tie-breaker
            };

    MinHeapRankingStrategy(int k) {
        this.k = k;
        this.minHeap = new PriorityQueue<>(MIN_HEAP_COMPARATOR);
    }

    @Override
    public void updateScore(String playerId, int score) {

        // Remove existing entry for the player (K is small → acceptable)
        minHeap.removeIf(ps -> ps.playerId.equals(playerId));

        PlayerScore p = new PlayerScore(playerId, score);

        if (minHeap.size() < k) {
            minHeap.offer(p); }
        else if (MIN_HEAP_COMPARATOR.compare(minHeap.peek(), p) < 0) {
            minHeap.poll();
            minHeap.offer(p);
        }
    }

    @Override
    public List<String> getTopK() {
        List<PlayerScore> list = new ArrayList<>(minHeap);

        // Sort Top-K in descending order for output
        list.sort((a, b) -> b.score - a.score);

        List<String> result = new ArrayList<>();
        for (PlayerScore ps : list) {
            result.add(ps.playerId);
        }
        return result;
    }
}

class Leaderboard {
    private final RankingStrategy rankingStrategy;

    Leaderboard(RankingStrategy rankingStrategy) {
        this.rankingStrategy = rankingStrategy;
    }

    public void updateScore(String playerId, int score) {
        rankingStrategy.updateScore(playerId, score);
    }

    public List<String> getTopK() {
        return rankingStrategy.getTopK();
    }
}

class PlayerScore {
    String playerId;
    int score;

    PlayerScore(String playerId, int score) {
        this.playerId = playerId;
        this.score = score;
    }
}

public class LeaderboardApplication {

    public static void main(String[] args) {

        // Top 5 leaderboard
        RankingStrategy strategy = new MinHeapRankingStrategy(5);
        Leaderboard leaderboard = new Leaderboard(strategy);

        leaderboard.updateScore("Alice", 50);
        leaderboard.updateScore("Bob", 80);
        leaderboard.updateScore("Charlie", 70);
        leaderboard.updateScore("David", 90);
        leaderboard.updateScore("Eve", 60);
        leaderboard.updateScore("Frank", 85);

        System.out.println("Top Players: " + leaderboard.getTopK());
    }
}

/*
⏱️ Complexity
Update score: O(log K)
Get Top K: O(K log K)
Space: O(K)


compare(a, b)
    returns:
    negative (< 0) → a is smaller / weaker than b
    zero (0) → a and b are equal in ordering
    positive (> 0) → a is greater / stronger than b
    This is not numeric comparison — it’s ordering comparison.

*/
