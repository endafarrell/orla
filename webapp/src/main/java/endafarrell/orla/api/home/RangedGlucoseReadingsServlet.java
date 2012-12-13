package endafarrell.orla.api.home;

import endafarrell.orla.service.Orla;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/api/home/glucose"})
public class RangedGlucoseReadingsServlet extends HttpServlet {

    Orla orla;

    @Override
    public void init() {
        this.orla = Orla.getInstance();
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        int weeks = 4;
        int lower = 25;
        int higher = 75;
        try {
            weeks = Integer.valueOf(req.getParameter("w"));
        } catch (Exception ignored) {
        }

        try {
            lower = Integer.valueOf(req.getParameter("l"));
        } catch (Exception ignored) {
        }

        try {
            higher = Integer.valueOf(req.getParameter("h"));
        } catch (Exception ignored) {
        }

        res.setContentType("application/json");
        orla.writeGlucoseReadings(res.getOutputStream(), weeks, lower, higher);
    }
}