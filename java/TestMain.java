package plotter;

import fi.iki.elonen.NanoHTTPD;
import sun.misc.IOUtils;

import java.io.*;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

class TestMain extends NanoHTTPD {
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
            "<head><title>Web</title></head>" +
            "<body>" +
            "<h2>Web</h2>" +
            "<a href=\"/\">HOME</a>" +
            "<form enctype=\"multipart/form-data\" action=\"/upload\" method=\"post\">\n" +
            "File: <input name=\"uploadFile\" type=\"file\"><br>\n" +
            "Path: <input type=\"text\" name=\"path\" value=\"/sdcard/uploads/\"><br>\n" +
            "<input type=\"submit\" value=\"Upload\" name=\"uploadButton\">\n" +
            "</form>" +
            "</body>" +
            "</html>";

    @Override
    public NanoHTTPD.Response serve(NanoHTTPD.IHTTPSession session) {
        Response response = null;
        if (session.getMethod().equals(NanoHTTPD.Method.POST)) {
            String tempFilename = "te";
            if (tempFilename != null) {
                try {
                    getIS(session);
                    response = newFixedLengthResponse(NanoHTTPD.Response.Status.OK, NanoHTTPD.MIME_HTML, "OK !");
                } catch (Exception e) {
                    e.printStackTrace();
                    response = newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_HTML, "Upload Error !");
                }
            } else {
                response = newFixedLengthResponse(NanoHTTPD.Response.Status.OK, NanoHTTPD.MIME_HTML, "Upload Error !");
            }
            return response;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(page);
        response = newFixedLengthResponse(NanoHTTPD.Response.Status.OK, NanoHTTPD.MIME_HTML, sb.toString());
        return response;
    }

    public void getIS(IHTTPSession session) {
        StringBuffer response = new StringBuffer();
        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(session.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine+"\n");
                //System.out.println(inputLine);
            }
        } catch(Exception e) {
            FileParser fp = new FileParser(response.toString());
            System.out.println(fp.getFileName());
            System.out.println(fp.getContent());
            System.out.println(fp.getDirectory());
        }
    }

    public static void main(String[] args) {

        try {
            new TestMain();
        } catch (IOException ioe) {
            System.err.println("Couldn't start server:\n" + ioe);
        }
    }
}