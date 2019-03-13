package plotter;

import fi.iki.elonen.NanoHTTPD;
import java.io.*;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class TestMain extends NanoHTTPD {
    private static int count = 0;
    private static Port port;

    public TestMain() throws IOException {
        super(8080);
        start(1000, false);
        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            String ip = socket.getLocalAddress().getHostAddress();
            System.out.println("URL: http://" + ip + ":8080/");
        }
    }

    public static final String page = "<html>" +
            "<head><title>X Y Plotter server</title></head>" +
            "<body>" +
            "<h2>X Y Plotter</h2>" +
            "<form enctype=\"multipart/form-data\" action=\"/upload\" method=\"post\">\n" +
            "File: <input name=\"uploadFile\" type=\"file\"><br>\n" +
            "Path: <input type=\"text\" name=\"path\" value=\"/directory/\"><br>\n" +
            "<input type=\"submit\" value=\"Upload\" name=\"uploadButton\">\n" +
            "</form>" +
            "</body>" +
            "</html>";

    @Override
    public NanoHTTPD.Response serve(NanoHTTPD.IHTTPSession session) {
        Response response = null;
        String tempFilename = "";
        if (tempFilename != null) {
            try {
                Main.parse(session);
                response = newFixedLengthResponse(NanoHTTPD.Response.Status.OK, NanoHTTPD.MIME_HTML, "OK!");
            } catch (Exception e) {
                e.printStackTrace();
                response = newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_HTML, "Upload Error!");
            }
        } else {
            response = newFixedLengthResponse(NanoHTTPD.Response.Status.OK, NanoHTTPD.MIME_HTML, "No file selected.");
        }
        return response;
    }

    public static void main(String[] args) {
        try {
            new TestMain();
        } catch (IOException ioe) {
            System.err.println("Couldn't start server:\n" + ioe);
        }
    }
}