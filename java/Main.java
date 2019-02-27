package plotter;

import fi.iki.elonen.NanoHTTPD;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Scanner;

class Main extends NanoHTTPD {
    private static int count = 0;
    private static Port port;

    public Main() throws IOException {
        super(8080);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        String ip = "";
        try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            ip = socket.getLocalAddress().getHostAddress();
        }
        System.out.println("\nRunning! Point your browsers to http://" + ip + ":8080/ \n");
    }

    @Override
    public NanoHTTPD.Response serve(NanoHTTPD.IHTTPSession session) {
        String msg = "<html><body><h1>XY plotter.Plotter</h1>\n";
        Map<String, String> parms = session.getParms();
        String X_SPEED = parms.get("xspeed");
        String Y_SPEED = parms.get("yspeed");
        String HOME_SPEED = parms.get("homespeed");
        String TAP_DELAY = parms.get("tapdelay");
        //port.send("EDIT:" + X_SPEED + "," + Y_SPEED + "," + HOME_SPEED + "," + TAP_DELAY + "/");
        System.out.println("EDIT:" + X_SPEED + "," + Y_SPEED + "," + HOME_SPEED + "," + TAP_DELAY + "/");
        String file = "";
        try {
            file = new String(Files.readAllBytes(Paths.get("index.html")), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newFixedLengthResponse(file);
    }

    public static void main(String[] args){
        port = new Port("COM31");
        String fileName = "crybaby o.svg";
        SvgParser svg = new SvgParser(fileName);
        Plotter p = new Plotter(svg, 24000);
        p.calculate();
        p.calculate2();
        for(int i = 0; i < svg.getLength(); i ++){
            System.out.println(i + ": " + p.getX(i) + " | " + p.getY(i));
        }
        System.out.println(p.toString());

        try {
            new Main();
        } catch (IOException ioe) {
            System.err.println("Couldn't start server:\n" + ioe);
        }

        System.out.println("Start drawing? Y/N");
        Scanner scanner = new Scanner(System.in);
        String scan = scanner.nextLine().toLowerCase();

        if(scan.equals("y")) {
            port.send("0,0/");
        } else if(scan.equals("n")){

        }

        port.send( p.getStartX() + "," +  p.getStartY() + "/");

        try {
            while (true) {
                while (port.bytesAvailable() == 0)
                    Thread.sleep(20);
                char charRead = port.read();
                if (charRead == '5' && count < svg.getLength() - 1) {
                    String xy = p.getX(count) + "," + p.getY(count);
                        System.out.println("(" + count + "/" + svg.getLength() + ") " + xy);
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