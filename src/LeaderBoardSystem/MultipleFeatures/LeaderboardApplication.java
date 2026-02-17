package LeaderBoardSystem.MultipleFeatures;

import java.util.*;
/*
Features supported:
1. Multiple games
2. Show user rank
3. Top 10 / 50 / 100 players
*/

public class LeaderboardApplication {
    public static void main(String[] args) {
//        LeaderboardStrategyType strategyType = LeaderboardStrategyType.RANK_BASED;
//        LeaderboardService leaderboardService = new LeaderboardServiceImpl(strategyType);

        LeaderboardService leaderboardService = new LeaderboardServiceImpl();
        // GAME 1
        leaderboardService.updateScore("GAME_1", "Alice", 80);
        leaderboardService.updateScore("GAME_1", "Bob", 60);
        leaderboardService.updateScore("GAME_1", "Charlie", 90);
        leaderboardService.updateScore("GAME_1", "David", 70);
        leaderboardService.updateScore("GAME_1", "Eve", 85);

        // GAME 2
        leaderboardService.updateScore("GAME_2", "Alice", 50);
        leaderboardService.updateScore("GAME_2", "Bob", 95);
        leaderboardService.updateScore("GAME_2", "Frank", 75);

        System.out.println("GAME_1 Top 3: " +
                leaderboardService.getTopK("GAME_1", 3));
        System.out.println("GAME_1 Rank of Alice: " +
                leaderboardService.getUserRank("GAME_1", "Alice"));

        System.out.println("GAME_2 Top 2: " +
                leaderboardService.getTopK("GAME_2", 2));
        System.out.println("GAME_2 Rank of Bob: " +
                leaderboardService.getUserRank("GAME_2", "Bob"));
    }
}

//enum LeaderboardStrategyType {
//    RANK_BASED
//    // TOP_K_HEAP can be added later
//}

interface LeaderboardService {
    void updateScore(String gameId, String userId, int score);
    List<String> getTopK(String gameId, int k);
    int getUserRank(String gameId, String userId);
}

class LeaderboardServiceImpl implements LeaderboardService {

    // gameId -> leaderboard
    private final Map<String, GameLeaderboard> gameLeaderboards = new HashMap<>();
//    private final LeaderboardStrategyType strategyType;

//    LeaderboardServiceImpl(LeaderboardStrategyType strategyType) {
//        this.strategyType = strategyType;
//    }

    @Override
    public void updateScore(String gameId, String userId, int score) {
//        GameLeaderboard leaderboard =  gameLeaderboards.computeIfAbsent(gameId,id -> GameLeaderboardFactory.create(strategyType));
        GameLeaderboard leaderboard =  gameLeaderboards.computeIfAbsent(gameId,id -> new RankBasedGameLeaderboard());
        leaderboard.updateScore(userId, score);
    }

    @Override
    public List<String> getTopK(String gameId, int k) {
        GameLeaderboard leaderboard = gameLeaderboards.get(gameId);
        return leaderboard == null ? Collections.emptyList()
                : leaderboard.getTopK(k);
    }

    @Override
    public int getUserRank(String gameId, String userId) {
        GameLeaderboard leaderboard = gameLeaderboards.get(gameId);
        return leaderboard == null ? -1
                : leaderboard.getUserRank(userId);
    }
}

//class GameLeaderboardFactory {
//
//    static GameLeaderboard create(LeaderboardStrategyType type) {
//        switch (type) {
//            case RANK_BASED:
//                return new RankBasedGameLeaderboard();
//            default:
//                throw new IllegalArgumentException("Unsupported strategy");
//        }
//    }
//}

interface GameLeaderboard {
    void updateScore(String userId, int score);
    List<String> getTopK(int k);
    int getUserRank(String userId);
}

/*
TreeSet-based leaderboard
- Supports rank queries
- Supports dynamic Top-K
*/

class RankBasedGameLeaderboard implements GameLeaderboard {

    private final Map<String, PlayerScore> playerMap = new HashMap<>();

    private final TreeSet<PlayerScore> ranking =
            new TreeSet<>((a, b) -> {
                if (a.score != b.score) {
                    return b.score - a.score; // higher score first
                }
                return a.userId.compareTo(b.userId);
            });

    @Override
    public void updateScore(String userId, int score) {
        if (playerMap.containsKey(userId)) {
            ranking.remove(playerMap.get(userId));
        }

        PlayerScore ps = new PlayerScore(userId, score);
        playerMap.put(userId, ps);
        ranking.add(ps);
    }

    @Override
    public List<String> getTopK(int k) {
        List<String> result = new ArrayList<>();
        int count = 0;

        for (PlayerScore ps : ranking) {
            if (count++ == k) break;
            result.add(ps.userId);
        }
        return result;
    }

    @Override
    public int getUserRank(String userId) {
        PlayerScore target = playerMap.get(userId);
        if (target == null) return -1;

        int rank = 1;
        for (PlayerScore ps : ranking) {
            if (ps.equals(target)) return rank;
            rank++;
        }
        return -1;
    }
}

class PlayerScore {
    String userId;
    int score;

    PlayerScore(String userId, int score) {
        this.userId = userId;
        this.score = score;
    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (!(o instanceof PlayerScore)) return false;
//        PlayerScore other = (PlayerScore) o;
//        return Objects.equals(userId, other.userId);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(userId);
//    }
}
