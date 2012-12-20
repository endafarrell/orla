package endafarrell.orla.api.home.events;

import endafarrell.orla.service.Orla;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/api/home/events/byDay"})
public class EventsByDayServlet extends HttpServlet {

    Orla orla;

    @Override
    public void init() {
        this.orla = Orla.getInstance();
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        boolean skipEventsList = false;
        int weeks = -1;
        try {
            skipEventsList = Boolean.valueOf(req.getParameter("skipEventsList"));
        } catch (Exception ignored) {
        }
        try {
            weeks = Integer.valueOf(req.getParameter("w"));
        } catch (Exception ignored) {
        }
        res.setContentType("application/json");
        orla.writeEventsByDayAsJson(res.getOutputStream(), skipEventsList, weeks);
    }
}