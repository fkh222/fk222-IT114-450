package Project.Common;

import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;

public class PointsPayload extends Payload {
    private ConcurrentHashMap<String, Integer> playerPoints;

    public ConcurrentHashMap<String, Integer> getPlayerPoints() {
        return playerPoints;
    }

    public void setPlayerPoints(ConcurrentHashMap<String, Integer> playerPoints) {
        this.playerPoints = playerPoints;
    }

    /**
     * @param scoreboard scoreboard to be reset
     */
    public void resetAllPoints(ConcurrentHashMap<String, Integer> scoreboard) {
        for (ConcurrentHashMap.Entry<String, Integer> entry : scoreboard.entrySet()) {
            entry.setValue(0);
        }
    }

    @Override
    public String toString(){
        StringJoiner joiner = new StringJoiner(", ");
        if (playerPoints != null) {
        for (ConcurrentHashMap.Entry<String, Integer> entry : playerPoints.entrySet()) {
            joiner.add(entry.getKey() + "=" + entry.getValue());
            }
        }
        return String.format("PointsPayload Scoreboard: {%s}", joiner.toString());
    }
}
/**
 * fk222 7/30/25
 * PointsPayload is used for syncing the scoreboard of players' points between the client and server side. It includes getter+setter, reset points, and toString() method
 */