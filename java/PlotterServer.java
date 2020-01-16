package plotter;

import fi.iki.elonen.NanoHTTPD;
import plotter.controller.Send;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;

//Webserver host

public class PlotterServer extends NanoHTTPD {
    private static String DIRECTORY = "";
    private String WEBSERVER;
    private Control c;
    private Send s;

    //Starts local webserver
    public PlotterServer(int SERVER_PORT, String comPort, int socketTimeout, String WEBSERVER_FILE) throws IOException {
        super(SERVER_PORT);
        this.WEBSERVER = Utils.readFile(WEBSERVER_FILE);
        Port port = new Port(comPort);
        print(Utils.getServerUrl(SERVER_PORT));
        c = new Control(port);
        s = new Send(port);
        start(socketTimeout, false);
    }

    //Web server function
    @Override
    public NanoHTTPD.Response serve(NanoHTTPD.IHTTPSession session) {
        Requests r = new Requests(session);
        switch (r.getUri()) {
            case "/start/": c.plot(DIRECTORY, Integer.parseInt(r.getParam("svg_size"))); //Starts plotting image at the given size
                print("STARTING...");
                break;
            case "/stop/": System.exit(0); //Stop program
                print("STOP");
                break;
            case "/left/": s.setX(-100).setY(0).sendMessage(true); //Move pen to the left
                //print("LEFT");
                break;
            case "/right/": s.setX(100).setY(0).sendMessage(true); //Move pen to the right
                //print("RIGHT");
                break;
            case "/up/": s.setX(0).setY(100).sendMessage(true); //Moves pen up
                //print("UP");
                break;
            case "/down/": s.setX(0).setY(-100).sendMessage(true); //Moves pen down
                //print("DOWN");
                break;
            case "/tap/": s.tap(); //Taps pen
                //print("TAP");
                break;
            case "/home/": s.home(); //Set pen to default position
                //print("TAP");
                break;
            case "/box/": s.box(); //Draws box, homes pen, and scrolls until box is sensed
                print("BOX");
                break;
            case "/nextimage/": s.nextImage(); //Scrolls until box is sensed
                print("NEXT IMAGE");
                break;
            case "/scr/": s.scroll(); //Scrolls infinitely
                print("SCROLL");
                break;
            case "/stopscroll/": s.stopScroll(); //Stops scroll
                print("STOP SCROLL");
                break;
            case "/upload/": readInputStream(session); //Uploads SVG file
                print("UPLOAD");
                break;
        }
        return newFixedLengthResponse(WEBSERVER); //Web server file
    }


    //Read and set SVG file data
    private static void readInputStream(IHTTPSession session) {
        StringBuilder response = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(session.getInputStream())
            );
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine).append("\n");
            }
        } catch (SocketTimeoutException e) {

            FileParser fp = new FileParser(response.toString());
            String FILE_CONTENTS = fp.getContent();
            DIRECTORY = fp.getDirectory() + fp.getFileName();
            try {
                fp.saveFile(DIRECTORY, FILE_CONTENTS);
                print("File saved to: " + DIRECTORY);
            } catch (IOException e1) {
                print("File not saved: " + e1.getMessage());
                e1.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void print(String text) {
        System.out.println(text);
    }

    public static void main(String[] args) {
        try {
            //Constructor for starting up webserver/connection to Serial Port
            new PlotterServer(
                    8080,
                    "COM45",
                    //"/dev/ttyUSB0",
                    1000,
                    "webserver.html"
            );
        } catch (IOException e) {
            System.err.println("Couldn't start server:\n" + e);
        }
    }
}