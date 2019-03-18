package plotter;

import fi.iki.elonen.NanoHTTPD;
import plotter.controller.Send;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;

class PlotterServer extends NanoHTTPD {
    private static String DIRECTORY; //Directory of the file sent from webserver
    private String WEBSERVER_FILE;   //HTML webpage that is being hosted
    private Control c;               //Class file used for the connection to the arduino and plotting the svg image
    private Send s;                  //Class file for functions to simplify sending data to arduino

    public PlotterServer(int SERVER_PORT, String comPort, int socketTimeout, String WEBSERVER_FILE) throws IOException {
        super(SERVER_PORT);
        this.WEBSERVER_FILE = WEBSERVER_FILE;
        Port port = new Port(comPort);          //Connection to COM port
        print(Utils.getServerUrl(SERVER_PORT)); //Prints URL of webserver
        c = new Control(port);
        s = new Send(port);
        start(socketTimeout, false);   //Starts webserver
    }

    @Override
    public NanoHTTPD.Response serve(NanoHTTPD.IHTTPSession session) {
        Requests r = new Requests(session);                                 //Class file to parse HTTP Request from webserver
        switch (r.getUri()) {                                               //Gets uri of request (e.g. http://localhost:8080/start/)
            case "/start/": c.plot(DIRECTORY, Integer.parseInt(r.getParam("svg_size")));
                break;                                                      //Plots file in 'DIRECTORY' with max size of 'svg_size'
            case "/left/": s.setX(-100).setY(0).sendMessage(true); //Moves plotter left 100 steps
                break;
            case "/right/": s.setX(100).setY(0).sendMessage(true); //Moves plotter left 100 steps
                break;
            case "/up/": s.setX(0).setY(100).sendMessage(true);    //Moves plotter up 100 steps
                break;
            case "/down/": s.setX(0).setY(-100).sendMessage(true); //Moves plotter down 100 steps
                break;
            case "/tap/": s.tap();                                          //Taps pen
                break;
            case "/upload/": readInputStream(session);                      //Reads input stream from file upload request
                break;
        }
        return newFixedLengthResponse(Utils.readFile(WEBSERVER_FILE));      //Serves webserver page after every request
    }

    private static void readInputStream(IHTTPSession session) {
        //Reading input stream and saving the file
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

            FileParser fp = new FileParser(response.toString()); //Class file to parse inputstream
            String FILE_CONTENTS = fp.getContent();              //Contents of file
            DIRECTORY = fp.getDirectory() + fp.getFileName();    //Directory where file is saved
            try {
                fp.saveFile(DIRECTORY, FILE_CONTENTS);           //Saving file
                print("File saved to: " + DIRECTORY);
            } catch (IOException e1) {
                print("File not saved: " + e1.getMessage());
                e1.printStackTrace();
            }
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
            new PlotterServer(
                    8080,
                    //"COM31",
                    "/dev/ttyUSB0",
                    1000,
                    "webserver.html"
            );
        } catch (IOException e) {
            System.err.println("Couldn't start server:\n" + e);
        }
    }
}