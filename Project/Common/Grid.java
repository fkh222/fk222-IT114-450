package Project.Common;


public class Grid{
    private Pixel[][] board;

    // generate new board of pixels
    public void generate(int rows, int cols, boolean isServer) {
        board = new Pixel[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                board[i][j] = new Pixel(i, j);
                if(isServer){
                    board[i][j].initialize();
                }
            }
        }
    }

    // attempt to draw in pixel
    public void tryDraw(int x, int y, TextFX.Color color){
        if(!isValidCoordinate(x, y)){
            throw new IllegalArgumentException("Invalid coordinates");
        }
        Pixel pixel = board[x-1][y-1];
        if(pixel==null){
            throw new IllegalStateException("Pixel not initialized");
        }
        pixel.tryDraw(color);
    }

    // check coordinate validation
    public boolean isValidCoordinate(int row, int col) {
        return board != null && row >= 0 && col >= 0 && row <= board.length && col <= board[0].length;
    }

   /**
     * Resets all pixels in the grid/board by calling their reset method.
     */
    public void reset() {
        if (board == null)
            return;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] != null) {
                    board[i][j] = null;
                }
            }
        }
        board = null; // clear the reference to the grid
    }

     public int getHeight() {
        return board != null ? board.length : 0;
    }

    public int getWidth() {
        return (board != null && board.length > 0) ? board[0].length : 0;
    }

    public Pixel getPixel(int row, int col) {
        return board[row-1][col-1];
    }

    @Override
    public String toString() {

        if (board == null) {
            return "Board is not initialized.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                String color = board[i][j].getColor();
                String cellValue = String.format("[%s]", color.equals("-") ? "-" : color);
                sb.append(cellValue);// .append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }


}

