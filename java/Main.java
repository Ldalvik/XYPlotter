package plotter;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        try {
            new PlotterServer(8080, "/dev/ttyUSB0", 1000, "webserver.html");
        } catch (IOException e) {
            System.err.println("Couldn't start server:\n" + e);
        }
    }
}
