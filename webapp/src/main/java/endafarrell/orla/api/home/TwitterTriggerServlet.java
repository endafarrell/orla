package endafarrell.orla.api.home;

import endafarrell.orla.api.OrlaHttpServlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/api/home/twitter"})
public class TwitterTriggerServlet extends OrlaHttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        orla.readTwitterMessages();
        res.sendRedirect(req.getContextPath());
    }
}