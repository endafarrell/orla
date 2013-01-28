package endafarrell.orla.api.home;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

@WebServlet(urlPatterns = {"/api/home/endomondo"})
public class EndomondoTriggerServlet extends HttpServlet {
//
//    OrlaImpl orla;
//
//    @Override
//    public void init() {
//        this.orla = OrlaImpl.getInstance();
//    }
//
//    @Override
//    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
//        orla.readEndomondoRuns();
//        res.sendRedirect(req.getContextPath());
//    }
}