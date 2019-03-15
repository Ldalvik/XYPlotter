package plotter;

import fi.iki.elonen.NanoHTTPD;
import java.util.Map;

public class Requests {
    private NanoHTTPD.IHTTPSession session;

    Requests(NanoHTTPD.IHTTPSession session) {
        this.session = session;
    }

    String getParam(String param) {
        Map<String, String> params = session.getParms();
        return params.get(param);
    }

    public String getMethod() {
        return String.valueOf(session.getMethod());
    }

    String getUri() {
        return session.getUri();
    }

    public boolean isUriEqual(String uri) {
        return getUri().equals(uri);
    }
}