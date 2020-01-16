package plotter;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

//Utility class
public class Utils {

    //Remove all chars until specified word/char
    static String removeTillWord(String input, String word) {
        return input.substring(input.indexOf(word) + word.length());
    }

    //Remove all chars after specified word/char
    static String removeAllAfter(String input, String word) {
        return input.substring(0, input.indexOf(word));
    }

    //Remove text in between 2 specified words/chars
    public static String removeFromTo(String input, String from, String to) {
        return input.substring(input.indexOf(from), input.indexOf(to));
    }

    //Replace a word/char with another
    public static String replace(String input, String word, String replace) {
        return input.replace(word, replace);
    }

    //Reads file from webserver
    public static String readFile(String fileName) {
        String webserver = "";
        try {
            webserver = new String(Files.readAllBytes(Paths.get(fileName)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return webserver;
    }

    //Local address of webserver
    public static String getServerUrl(int port) {
        String url = "";
        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            String ip = socket.getLocalAddress().getHostAddress();
            url = "http://" + ip + ":" + port;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }
}
