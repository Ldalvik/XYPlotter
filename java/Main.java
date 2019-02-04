import java.util.Scanner;

class Main {
    private static int count = 0;

    public static void main(String[] args){
        Port port = new Port("COM24");
        String fileName = "marilyn 2.svg";
        SvgParser svg = new SvgParser(fileName);
        Plotter p = new Plotter(svg, 15000);
        p.calculate();
        p.calculate2();
        for(int i = 0; i < svg.getLength(); i ++){
            System.out.println(i + ": " + p.getX(i) + " | " + p.getY(i));
        }
        System.out.println(p.toString());

        System.out.println("Start drawing? Y/N");
        Scanner scanner = new Scanner(System.in);
        String scan = scanner.nextLine().toLowerCase();

        if(scan.equals("y")) {
            port.send("0,0/");
        } else if(scan.equals("n")){
            System.exit(0);
        }

        try {
            while (true) {
                while (port.bytesAvailable() == 0)
                    Thread.sleep(20);
                char charRead = port.read();
                if (charRead == '5' && count < svg.getLength() - 1) {
                    String xy = p.getX(count) + "," + p.getY(count);
                        System.out.println("(" + count + "/" + svg.getLength() + ")" + xy);
                        port.send(xy + "/");
                    count++;
                } else {
                    System.exit(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        port.close();
    }
}
