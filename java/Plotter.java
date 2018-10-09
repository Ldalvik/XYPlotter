public class Plotter {

    private String[] coordinates;
    private int currentX = 0;
    private int currentY = 0;

    public Plotter(String data){
        this.coordinates = data.split("\n");
    }

    public int getX(int count){
        long newX = Math.round(Double.parseDouble(coordinates[count].split(",")[0]));
        System.out.println(newX);
        long x;
        if(count==0){
            x = (newX - currentX);
        } else {
            x = (newX - currentX)*20;
        }
        currentX = (int) newX;
        return (int) x;
    }

    public long getY(int count){
        long newY = Math.round(Double.parseDouble(coordinates[count].split(",")[1]));
        System.out.println(newY);
        long y;
        if(count==0){
            y = (newY - currentY);
        } else {
            y = (newY - currentY)*20;
        }
        currentY = (int) newY;
        return y;
    }

    public int getLength(){
        return coordinates.length;
    }
}
