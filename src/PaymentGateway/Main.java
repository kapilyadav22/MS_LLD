package PaymentGateway;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

class Player {
    String playerId;
    int score;

    Player(String playerId, int score){
        this.playerId= playerId;
        this.score = score;
    }
}

interface RankingStrategy {
    void updateScore(String playerId, int score);
    List<Player> topKPlayers();
}

class MinHeapStrategy implements RankingStrategy{

    private final PriorityQueue<Player> minHeap;
    private final int k;
    private static final Comparator<Player> MIN_HEAP_COMPARATOR = (p1, p2)->{
        if(p1.score!=p2.score){
            return p1.score-p2.score;
        }
        return p1.playerId.compareTo(p2.playerId);
    };

    MinHeapStrategy(int k){
        minHeap = new PriorityQueue<>(MIN_HEAP_COMPARATOR);
        this.k = k;
    }

    @Override
    public void updateScore(String playerId, int score){

        minHeap.removeIf(p-> p.playerId.equals(playerId));
        Player p = new Player(playerId,score);

        if(minHeap.size()<k){
            minHeap.offer(p);
        } else if(MIN_HEAP_COMPARATOR.compare(minHeap.peek(),p)<0){
            minHeap.poll();
            minHeap.offer(p);
        }
    }

    @Override
    public List<Player> topKPlayers(){
        List<Player> result = new ArrayList<>(minHeap);
        result.sort((p1,p2)->p2.score-p1.score);
        return result;
    }

}

class LeaderBoard {
    private final RankingStrategy rankingStrategy;

    LeaderBoard(RankingStrategy rankingStrategy){
        this.rankingStrategy = rankingStrategy;
    }

    public void updateScore(String playerId, int score){
        rankingStrategy.updateScore(playerId, score);
    }

    public List<Player> topKPlayers(){
        return rankingStrategy.topKPlayers();
    }
}


public class Main{
    public static void main(String[] args){
        LeaderBoard leaderBoard = new LeaderBoard(new MinHeapStrategy(3));
        leaderBoard.updateScore("Kapil",20);
        leaderBoard.updateScore("ravi",30);
        leaderBoard.updateScore("rahul",20);
        leaderBoard.updateScore("kavi",100);

        List<Player> topKPlayers =     leaderBoard.topKPlayers();
        topKPlayers.forEach(p-> System.out.println(p.playerId));
    }
}
