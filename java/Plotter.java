public class Plotter {

    private String[] coordinates;
    private int currentX = 0;
    private int currentY = 0;

    public Plotter(String data){
        this.coordinates = data.split("\n");
    }

    public int getX(int count){
        String coordDeci = coordinates[count].replace(".", "");
        String coord = coordDeci.split(",")[0];
        String decimal = coord.substring(0, 5);
        long newX = Long.parseLong(decimal);
        long x;
        if(count==0){
            x = 0;
        } else {
            x = (newX - currentX);
        }
        /*String xValue = String.valueOf(x);
        if(xValue.length() > 3){
            x = Long.parseLong(String.valueOf(0));
        }*/
        currentX = (int) newX;
        return (int) x;
    }

    public long getY(int count){
        String coordDeci = coordinates[count].replace(".", "");
        String coord = coordDeci.split(",")[1];
        String decimal = coord.substring(0, 4);

        long newY = Long.parseLong(decimal);
        long y;
        if(count==0){
            y = 0;
        } else {
            y = (newY - currentY);
        }

        /*String yValue = String.valueOf(y);
        if(yValue.length() > 3){
            y = Long.parseLong(String.valueOf(0));
        }*/
        currentY = (int) newY;
        return y;
    }

    public int getLength(){
        return coordinates.length;
    }
}
