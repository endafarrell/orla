package endafarrell.orla.api.home;

import endafarrell.orla.api.OrlaHttpServlet;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/api/home/events"}, name = "events list")
public class EventsServlet extends OrlaHttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType(OrlaHttpServlet.ContentType.JSON);
        Pair<DateTime, DateTime> fromTo = fromTo(req);
        orla.writeEventsAsJson(res.getOutputStream(), fromTo.getLeft(), fromTo.getRight());
    }
}