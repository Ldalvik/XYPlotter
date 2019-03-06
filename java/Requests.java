package plotter;

import fi.iki.elonen.NanoHTTPD;
import java.util.Map;

public class Requests {
    private NanoHTTPD.IHTTPSession session;
    String X_SPEED;
    String Y_SPEED;
    String HOME_SPEED;
    String TAP_DELAY;

    public Requests(NanoHTTPD.IHTTPSession session) {
        this.session = session;
        X_SPEED = getParam("x_speed");
        Y_SPEED = getParam("y_speed");
        HOME_SPEED = getParam("home_speed");
        TAP_DELAY = getParam("tap_delay");
    }

    String getParam(String param) {
        Map<String, String> params = session.getParms();
        return params.get(param);
    }

    public String getMethod() {
        return String.valueOf(session.getMethod());
    }

    public String getUri() {
        return session.getUri();
    }

    public boolean isStart() {
        return getUri().equals("/start/");
    }

    public boolean moveUp() {
        return getUri().equals("/up/");
    }

    public boolean moveDown() {
        return getUri().equals("/down/");
    }

    public boolean moveLeft() {
        return getUri().equals("/left/");
    }

    public boolean moveRight() {
        return getUri().equals("/right/");
    }

    public boolean tap() {
        return getUri().equals("/tap/");
    }
}