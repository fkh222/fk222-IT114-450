package Project.Common;

public class PointsPayload extends Payload {
    private int points = 0;

    public PointsPayload(long clientId, int points){
        setPayloadType(PayloadType.POINTS);
    }
    @Override
    public String toString() {
        return super.toString() + toString().format("Client Id [%s] Points: %d",
            getClientId(), points);
    }
}
