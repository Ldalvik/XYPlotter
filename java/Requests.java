package plotter;

import fi.iki.elonen.NanoHTTPD;
import java.util.Map;

public class Requests {
    private NanoHTTPD.IHTTPSession session;

    int SVG_SIZE;

    public Requests(NanoHTTPD.IHTTPSession session) {
        this.session = session;
        SVG_SIZE = Integer.parseInt(getParam("svg_size"));
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

    public boolean isUriEqual(String uri) {
        return getUri().equals(uri);
    }
}