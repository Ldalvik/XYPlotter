import com.fazecast.jSerialComm.SerialPort;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

class Main {

    private static int count = 0;

    public static void main(String[] args) throws Exception {

        Port port = new Port("COM21");

        String data = parseSvg(readSvg("heart.svg"));
        System.out.println(data);
        Plotter p = new Plotter(data);
        System.out.println("-----End of SVG coordinates-----");


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
                System.out.println("rec: " + charRead);
                if (charRead == '5') {
                    String xy = p.getX(count) + "," + p.getY(count) + "/";
                    System.out.println("(" + count + ")" + xy);
                    port.send(xy);
                    port.send("B" + count + ":" + p.getLength() + "/");
                    count++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        port.close();
    }

    private static String removeTillWord(String input, String word) {
        return input.substring(input.indexOf(word));
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

    private static String parseSvg(String data) {
        String s1 = removeTillWord(data, "<circle");
        String s2 = replace(s1, "<circle cx=\"", "");
        String s3 = replace(s2, "\" cy=\"", ",");
        String s4 = replace(s3, "\" r=\"0.5178572\" style=\"fill:none;stroke:black;stroke-width:2;\"/>", "");
        String s5 = replace(s4, "</g></g></svg>", "");
        return s5.trim();
    }

}
