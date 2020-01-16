package plotter;

import fi.iki.elonen.NanoHTTPD;
import java.util.Map;

//Web request parsing
public class Requests {
    private NanoHTTPD.IHTTPSession session;

    public Requests(NanoHTTPD.IHTTPSession session) {
        this.session = session;
    }

    //Get value from key (e.g. localhost:8080/?key=value = value)
    String getParam(String param) {
        Map<String, String> params = session.getParms();
        return params.get(param);
    }

    public String getMethod() {
        return String.valueOf(session.getMethod());
    }

    //Get parameters from url (e.g. localhost:8080/param/ = /param/)
    public String getUri() {
        return session.getUri();
    }

    public boolean isUriEqual(String uri) {
        return getUri().equals(uri);
    }
}