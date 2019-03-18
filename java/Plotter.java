package plotter;

public class Plotter {
    private long currentX = 0;
    private long currentY = 0;
    private float size;
    private float factor = 1f;
    private long lowestY;
    private long highestY;
    private long lowestX;
    private long highestX;
    private float xSize;
    private float ySize;
    private SvgParser svg;

    Plotter(SvgParser svg, float size) {
        this.svg = svg;
        this.size = size;
    }

    long getX(int count) {
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

    long getY(int count) {
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

    long getStartX() {
        return (long) (svg.getX(0) * factor);
    }

    long getStartY() {
        return (long) (svg.getY(0) * factor);
    }

    void calculate() {
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
        calculate2();
    }

    private void calculate2() {
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