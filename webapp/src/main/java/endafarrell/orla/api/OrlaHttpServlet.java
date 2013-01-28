package endafarrell.orla.api;


import endafarrell.orla.service.Orla;
import endafarrell.orla.service.OrlaImpl;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

public abstract class OrlaHttpServlet extends HttpServlet {
    public static final int DEFAULT_NUM_WEEKS = 4;
    protected Orla orla;


    @Override
    public void init() {
        this.orla = OrlaImpl.getInstance();
    }


    protected int weeks(HttpServletRequest req) {
        int weeks = DEFAULT_NUM_WEEKS;
        String w = req.getParameter("w");
        if (w != null) {
            try {
                weeks = Integer.valueOf(w);
            } catch (NumberFormatException ignored) {}
        }
        return weeks;
    }

    public static final class ContentType {
        public final static String JSON = "application/json";
    }
}
