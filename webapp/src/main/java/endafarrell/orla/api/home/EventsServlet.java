package endafarrell.orla.api.home;

import endafarrell.orla.service.Orla;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/api/home/events"})
public class EventsServlet extends HttpServlet {

    Orla orla;

    @Override
    public void init() {
        this.orla = Orla.getInstance();
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        orla.writeEventsAsJson(res.getOutputStream());
    }
}