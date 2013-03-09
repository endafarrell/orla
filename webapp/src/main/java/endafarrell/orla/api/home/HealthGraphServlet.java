package endafarrell.orla.api.home;

import endafarrell.orla.OrlaException;
import endafarrell.orla.api.OrlaHttpServlet;
import endafarrell.orla.service.processor.ProcessResults;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet(urlPatterns = {"/api/home/healthgraph"})
public class HealthGraphServlet extends OrlaHttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // I want this to - (1) connect with the HG, (2) download the fitness_activities, (3) report back
        // As step (1) might need an OAuth2 connect with RunKeeper, this might cause a redirect to our
        // callback servlet
        Map<String, String[]> parameterMap = req.getParameterMap();
        if (parameterMap.containsKey("code")) {
            // This is RunKeeper's callback URL in action
            String code = parameterMap.get("code")[0];
            try {
                orla.authenticate(code);
                ProcessResults results = orla.readHealthgraphFitnessActivities();
                res.sendRedirect(req.getContextPath() + "?provider=HealthGraph&results=" + results);
            } catch (OrlaException e) {
                throw new ServletException(e);
            }

        } else {
            String authorisationURL;
            try {
                authorisationURL = orla.getHealthGraphAuthorisation();
                res.sendRedirect(authorisationURL);
            } catch (OrlaException e) {
                throw new ServletException(e);
            }

        }

    }
}