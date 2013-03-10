package endafarrell.orla.api.home;

import endafarrell.orla.api.OrlaHttpServlet;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/api/home/hourlyPercentiles"})
public class HourlyPercentilesServlet extends OrlaHttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        Pair<DateTime, DateTime> fromTo = fromTo(req);
        orla.writeHourlyPercentiles(res.getOutputStream(), fromTo.getLeft(), fromTo.getRight());
    }
}