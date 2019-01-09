import com.fazecast.jSerialComm.SerialPort;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

class Main {

    private static int count = 0;

    public static void main(String[] args) throws Exception {

        Port port = new Port("COM24");
        SvgParser svg = new SvgParser("ok.svg");
        Plotter p = new Plotter(svg, 1);
        p.calculate();
        int length = svg.getLength();
        System.out.println("Length=" + length);
        System.out.println(p.highestX);
        System.out.println(p.lowestX);
        System.out.println(p.highestY);
        System.out.println(p.lowestY);
        //String xy1 = p.getX(0) + "," + p.getY(
        // 0) + "/";
        //String xy2 = p.getX(1) + "," + p.getY(1) + "/";
        //System.out.println(xy1);
        //System.out.println(xy2);

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
                //System.out.println("rec: " + charRead);
                if (charRead == '5' && count < svg.getLength()) {
                    String xy = p.getX(count) + "," + p.getY(count);
                    System.out.println("(" + count + "/" + svg.getLength() + ")" + xy);
                    port.send(xy + "/");
                    //port.send("B" + count + ":" + p.getLength() + "/");
                    count++;
                } else {
                    //port.send("BDone!/");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        port.close();
    }

    private static String removeTillWord(String input, String word) {
        return input.substring(input.indexOf(word) + word.length());
    }

    public static String removeFromTo(String input, String from, String to){
        return input.substring(input.indexOf(from), input.indexOf(to));
    }

    private static String replace(String input, String word, String replace) {
        return input.replace(word, replace);
    }

    private static String readSvg(String file) throws Exception {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
        }
        return sb.toString();
    }

    private static String parseSvg(String data, String radius) {
        String s1 = removeTillWord(data, "<circle");
        return s1.trim();
    }

    private static String parseTspSvg(String data, String radius) {
        /*String s1 = removeTillWord(data, "\"M");
        String s2 = replace(s1, "</g></g></svg>", "");
        String s3 = replace(s2, "\" />", "");*/
        String s4 = replace(data, ",,", ",");
        return s4.trim();
    }
}
