package plotter;

import fi.iki.elonen.NanoHTTPD;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

class Main extends NanoHTTPD {
    private static int count = 0;
    private static Port port;

    public Main() throws IOException {
        super(8080);
        port = new Port("COM31");
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            String ip = socket.getLocalAddress().getHostAddress();
            System.out.println("URL: http://" + ip + ":8080/");
        }
    }

    @Override
    public NanoHTTPD.Response serve(NanoHTTPD.IHTTPSession session) {
        Requests r = new Requests(session);
        String X_SPEED = r.X_SPEED;
        String Y_SPEED = r.Y_SPEED;
        String HOME_SPEED = r.HOME_SPEED;
        String TAP_DELAY = r.TAP_DELAY;
        String file = "";
        try {
            file = new String(Files.readAllBytes(Paths.get("index.html")), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        switch (r.getUri()) {
            case "/start/": {
                System.out.println("/start/");
                //port = new Port("/dev/ttyUSB0");
                String fileName = r.getParam("file_name");
                SvgParser svg = new SvgParser(fileName);
                Plotter p = new Plotter(svg, 24000);
                p.calculate();
                for (int i = 0; i < svg.getLength(); i++) {
                    System.out.println(i + ": " + p.getX(i) + " | " + p.getY(i));
                }

                port.send(p.getStartX() + "," + p.getStartY() + "/");

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
            break;
                case "/left/": port.send("M:" + -100 + "," + 0 + "/");
                    System.out.println("/left/");
                    break;
                case "/right/": port.send("M:" + 100 + "," + 0 + "/");
                    System.out.println("/right/");
                    break;
                case "/up/": port.send("M:" + 0 + "," + -100 + "/");
                    System.out.println("/up/");
                    break;
                case "/down/": port.send("M:" + 0 + "," + 100 + "/");
                    System.out.println("/down/");
                    break;
                case "/tap/": port.send("tap/");
                    System.out.println("/tap/");
                    break;
                case "/save/": port.send("EDIT:" + X_SPEED + "," + Y_SPEED + "," + HOME_SPEED + "," + TAP_DELAY + "/");
                    System.out.println("/save/");
                    break;

           /* case "/left/":
                System.out.println(-1 + "," + 0 + "/");
                break;
            case "/right/":
                System.out.println(1 + "," + 0 + "/");
                break;
            case "/up/":
                System.out.println(0 + "," + -1 + "/");
                break;
            case "/down/":
                System.out.println(0 + "," + 1 + "/");
                break;
            case "/tap/":
                System.out.println("tap/");
                break;
            case "/save/":
                System.out.println("EDIT:" + X_SPEED + "," + Y_SPEED + "," + HOME_SPEED + "," + TAP_DELAY + "/");
                break;*/
        }
        return newFixedLengthResponse(file);
    }

    public static void main(String[] args) {

        try {
            new Main();
        } catch (IOException ioe) {
            System.err.println("Couldn't start server:\n" + ioe);
        }
    }
}