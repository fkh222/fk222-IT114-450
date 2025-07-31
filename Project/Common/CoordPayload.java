package Project.Common;

public class CoordPayload extends Payload {
    private int x;
    private int y;
    private String color;

    public CoordPayload(int x, int y, String color) {
        this.x = x;
        this.y = y;
        this.color = color;
        setPayloadType(PayloadType.DRAW);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getColor(){
        return color;
    }

    @Override
    public String toString() {
        return super.toString() + " {" +
                "x=" + x +
                ", y=" + y +
                ",color=" + color + "}";
    }
}

/**
 * fk222 7/30/25
 * CoordPayload is used to send/sync board updates between client and server side.
 * it includes main method, getter+setters for x and y, a getter for coordinate's color, and toString
 */