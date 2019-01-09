public class Plotter {
    private long currentX = 0;
    private long currentY = 0;
    private int multiply;
    public long lowestY;
    public long highestY;
    public long lowestX;
    public long highestX;
    private SvgParser svg;

    public Plotter(SvgParser svg, int multiply){
        this.svg = svg;
        this.multiply = multiply;
    }

    public long getX(int count){
        String coord = svg.getX(count);
        String a = coord.substring(coord.indexOf(".") + 1, coord.indexOf(".") + 2);
        String decimal = coord.substring(0, coord.indexOf(".")).concat(a);

        long newX = Long.parseLong(decimal);
        long x;
        if(count==0){
            x = 0;
        } else {
            x = (newX - currentX) * multiply;
        }
        /*String xValue = String.valueOf(x);
        if(xValue.length() > 3){
            x = Long.parseLong(String.valueOf(0));
        }*/
        currentX = newX;
        return x;
    }

    public long getY(int count){
        String coord = svg.getY(count);
        String a = coord.substring(coord.indexOf(".") + 1, coord.indexOf(".") + 2);
        String decimal = coord.substring(0, coord.indexOf(".")).concat(a);
        long newY = Long.parseLong(decimal);
        long y;
        if(count==0){
            y = 0;
        } else {
            y = (newY - currentY) * multiply;
            System.out.print(newY + " - " + currentY + " = " + y + "\t");
        }

        /*String yValue = String.valueOf(y);
        if(yValue.length() > 3){
            y = Long.parseLong(String.valueOf(0));
        }*/
        currentY = newY;
        return y;
    }

    public void calculate(){
        String xx1 = svg.getX(0);
        String xx2 = xx1.substring(0, 4);
        long xx = Long.parseLong(xx2);

        String yy1 = svg.getX(0);
        String yy2 = yy1.substring(0, 3);
        long yy = Long.parseLong(yy2);
        lowestX = xx;
        lowestY = yy;

        for(int i = 0; i < svg.getLength(); i++) {
            String x1 = svg.getX(i);
            String x2 = x1.substring(0, 4);
            long x = Long.parseLong(x2);

            String y1 = svg.getX(i);
            String y2 = y1.substring(0, 3);
            long y = Long.parseLong(y2);

            if(x > highestX){
                highestX = x;
            }
            if(x < lowestX){
                lowestX = x;
            }
            if(y > highestY){
                highestY = y;
            }
            if(y < lowestY){
                lowestY = y;
            }
        }
    }
}
