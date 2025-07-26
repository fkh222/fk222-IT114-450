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

    public String tryDraw(TextFX.Color color){
        return this.color=TextFX.colorize("+", color);
    }

    public String getColor(){
        return this.color;
    }

     @Override
    public String toString(){
        return String.format("Pixel[%s][%s]: Color= $s", x, y, color);
    }
}


