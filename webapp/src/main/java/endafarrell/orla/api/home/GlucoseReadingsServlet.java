package endafarrell.orla.api.home;

import endafarrell.orla.api.OrlaHttpServlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/api/home/glucose"})
public class GlucoseReadingsServlet extends OrlaHttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        orla.writeGlucoseReadings(res.getOutputStream(), weeks(req));
    }
}