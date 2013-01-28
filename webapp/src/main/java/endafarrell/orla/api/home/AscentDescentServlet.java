package endafarrell.orla.api.home;

import endafarrell.orla.api.OrlaHttpServlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/api/home/ascentDescent"})
public class AscentDescentServlet extends OrlaHttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        int weeks = -1;
        try {
            weeks = Integer.valueOf(req.getParameter("w"));
        } catch (Exception ignored) {
        }
        res.setContentType("application/json");
        orla.writeAscentDecentByDayAsJson(res.getOutputStream(), weeks);
    }
}