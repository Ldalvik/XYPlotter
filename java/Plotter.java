package plotter;

import java.util.Arrays;

public class Plotter {
    private long currentX = 0;
    private long currentY = 0;
    public float size;
    public float factor = 1f;
    public long lowestY;
    public long highestY;
    public long lowestX;
    public long highestX;
    public float xSize;
    public float ySize;
    private SvgParser svg;

    public Plotter(SvgParser svg, float size) {
        this.svg = svg;
        this.size = size;
    }


    public long getX(int count) {
        long newX = (long) (svg.getX(count) * factor);
        long x;
        if(count == 0){
            x = 0;
        } else {
            x = newX - currentX;
        }
        currentX = newX;
        return x;
    }

    public long getY(int count) {
        long newY = (long) (svg.getY(count) * factor);
        long y;
        if(count == 0){
            y = 0;
        } else {
            y = newY - currentY;
        }
        currentY = newY;
        return y;
    }

    public long getStartX() {
        return (long) (svg.getX(0) * factor);
    }

    public long getStartY() {
        return (long) (svg.getY(0) * factor);
    }

    public void calculate() {
        lowestX = svg.getLowestX();
        highestX =  svg.getHighestX();
        lowestY = svg.getLowestY();
        highestY = svg.getHighestY();

        xSize = highestX - lowestX;
        ySize = highestY - lowestY;

        if (xSize > ySize) {
            factor = size / xSize;
        } else {
            factor = size / ySize;
        }
    }

    public void calculate2() {
        lowestY = 0;
        highestY = 0;
        lowestX = 0;
        highestX = 0;
        xSize = 0;
        ySize = 0;

        lowestX = (long) (svg.getLowestX() * factor);
        highestX = (long) (svg.getHighestX() * factor);

        lowestY = (long) (svg.getLowestY() * factor);
        highestY = (long) (svg.getHighestY() * factor);

        xSize = highestX - lowestX;
        ySize = highestY - lowestY;
    }
    @Override
    public String toString(){
        return "Length: " + svg.getLength() +
       "\nHighest X: " + highestX +
       "\nLowest X: " + lowestX +
       "\nHighest Y: " + highestY +
       "\nLowest Y: " +lowestY +
       "\nX Size: " + xSize +
       "\nY Size: " + ySize +
       "\nFactor: " + factor +
       "\nStart X: " + getStartY() +
       "\nStart Y: " + getStartX();
    }
}