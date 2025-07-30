package Project.Common;

public class Pixel{
    private int x;
    private int y;
    private String color="-"; 

    public Pixel(int x, int y){
        this.x=x;
        this.y=y;
    }

    public void initialize(){
        this.color="-";  //placeholder for white, "+"" would be colors
    }

    // basically color setter
    public void tryDraw(TextFX.Color color){
        this.color=TextFX.colorize("+", color);
    }

    public String getColor(){
        return this.color;
    }

    public int getX(){
        return this.x;
    }

    public int getY(){
        return this.y;
    }

     @Override
    public String toString(){
        return String.format("Pixel[%s][%s]: Color= $s", x, y, color);
    }

    public boolean isAlreadyDrawn(){
        if (color.equals("-")){
            return false;
        }
        else {return true;}
    }
}


