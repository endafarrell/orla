package endafarrell.orla.api;


import endafarrell.orla.service.Orla;
import endafarrell.orla.service.OrlaDateTimeFormat;
import endafarrell.orla.service.OrlaImpl;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

public abstract class OrlaHttpServlet extends HttpServlet {
    public static final int DEFAULT_NUM_WEEKS = 4;
    protected Orla orla;


    @Override
    public void init() {
        this.orla = OrlaImpl.getInstance();
    }

    protected Pair<DateTime, DateTime> fromTo(HttpServletRequest req) {
        DateTime f = null;
        DateTime t = null;
        String from = req.getParameter("from");
        if (from != null) {
            try {
                f = OrlaDateTimeFormat.PRETTY_yyyyMMdd.parseDateTime(from);
            } catch (IllegalArgumentException ignored) { }
        }

        String to = req.getParameter("to");
        if (to != null) {
            try {
                t = OrlaDateTimeFormat.PRETTY_yyyyMMdd.parseDateTime(to);
            } catch (IllegalArgumentException ignored) { }
        }

        if (t == null) {
            t = DateTime.now();
        }
        if (f == null) {
            f = t.minusWeeks(DEFAULT_NUM_WEEKS);
        }
        StringBuilder params = new StringBuilder();
        Enumeration<String> paramNames = req.getParameterNames();
        while(paramNames.hasMoreElements()){
            String paramName = paramNames.nextElement();
            params.append(paramName).append("=").append(StringUtils.join(req.getParameterValues(paramName),",")).append(";");
        }
        System.out.println("»OrlaHttpServlet.fromTo("+params.toString()+") => ["+from+","+to+"] => <"+OrlaDateTimeFormat.PRETTY_yyyyMMdd.print(f)+","+OrlaDateTimeFormat.PRETTY_yyyyMMdd.print(t)+">«");
        return new ImmutablePair<DateTime, DateTime>(f, t);
    }

    public static final class ContentType {
        public final static String JSON = "application/json";
    }
}
