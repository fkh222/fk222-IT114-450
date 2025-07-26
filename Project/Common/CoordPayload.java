package Project.Common;

public class CoordPayload extends Payload {
    private int x;
    private int y;

    public CoordPayload(int x, int y) {
        this.x = x;
        this.y = y;
        setPayloadType(PayloadType.DRAW);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return super.toString() + " {" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}