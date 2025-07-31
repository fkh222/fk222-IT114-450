package Project.Common;

public class DimensionPayload extends Payload {
    private int width;
    private int height;

    public DimensionPayload() {}

    public DimensionPayload(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return String.format("DimensionPayload[%s] Client Id [%s] Board Size: %dx%d",
            getPayloadType(), getClientId(), width, height);
    }
}
