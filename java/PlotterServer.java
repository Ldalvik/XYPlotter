package plotter;

import fi.iki.elonen.NanoHTTPD;
import plotter.controller.Send;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;

class PlotterServer extends NanoHTTPD {
    private static String FILE_CONTENTS;
    private String WEBSERVER_FILE;
    private Control c;
    private Send s;

    public PlotterServer(int SERVER_PORT, String comPort, int socketTimeout, String WEBSERVER_FILE) throws IOException {
        super(SERVER_PORT);
        this.WEBSERVER_FILE = WEBSERVER_FILE;
        Port port = new Port(comPort);
        print(Utils.getServerUrl(SERVER_PORT));
        c = new Control(port);
        s = new Send(port);
        start(socketTimeout, false);
    }

    @Override
    public NanoHTTPD.Response serve(NanoHTTPD.IHTTPSession session) {
        Requests r = new Requests(session);
        switch (r.getUri()) {
            case "/start/":
                c.plot(FILE_CONTENTS, Integer.parseInt(r.getParam("svg_size")));
                print("start");
                break;
            case "/left/":
                s.setX(100).setY(0).sendMessage(true);
                print("move left");
                break;
            case "/right/":
                s.setX(-100).setY(0).sendMessage(true);
                print("move right");
                break;
            case "/up/":
                s.setX(0).setY(100).sendMessage(true);
                print("move up");
                break;
            case "/down/":
                s.setX(0).setY(-100).sendMessage(true);
                print("move down");
                break;
            case "/tap/":
                s.tap();
                print("tap");
                break;
            case "/upload/":
                readInputStream(session);
                print("file uploaded");
                break;
        }
        return newFixedLengthResponse(Utils.readFile(WEBSERVER_FILE));
    }

    private static void readInputStream(IHTTPSession session) {
        StringBuilder response = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(session.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine).append("\n");
            }
        } catch (SocketTimeoutException e) {
            FileParser fp = new FileParser(response.toString());
            FILE_CONTENTS = fp.getContent();
            String DIRECTORY = fp.getDirectory() + fp.getFileName();
            print("File received, saved to: " + DIRECTORY);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void print(String text) {
        System.out.println(text);
    }





    ////////////////////////////////////////////////////////////////





    public static void main(String[] args) {
        try {
            new PlotterServer(8080, "/dev/ttyUSB0", 1000, "webserver.html");
        } catch (IOException e) {
            System.err.println("Couldn't start server:\n" + e);
        }    }
}
