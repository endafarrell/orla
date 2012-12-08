package endafarrell.orla.api.sys;

import endafarrell.orla.service.Orla;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@WebServlet(urlPatterns = {"/api/sys/config"}, name = "API sys")
public class ConfigServlet extends HttpServlet {

    Orla orla;

    @Override
    public void init() {
        this.orla = Orla.getInstance();
    }


    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        ArrayNode registrations = JsonNodeFactory.instance.arrayNode();

        Map<String, ? extends ServletRegistration> servletRegistrations =
                this.getServletContext().getServletRegistrations();
        for (String registration : servletRegistrations.keySet()) {
            ObjectNode registrationNode = JsonNodeFactory.instance.objectNode();
            ArrayNode mappings = JsonNodeFactory.instance.arrayNode();

            ServletRegistration servletRegistration = servletRegistrations.get(registration);
            for (String mapping : servletRegistration.getMappings()) {
                mappings.add(mapping);
                System.err.println(mapping);
            }
            registrationNode.put("mappings", mappings);
            registrationNode.put("name", servletRegistration.getName());
            registrationNode.put("className", servletRegistration.getClassName());
            registrationNode.put("runAsRole", servletRegistration.getRunAsRole());

            registrations.add(registrationNode);
        }

        ObjectNode orlaConfig = orla.config();
        ObjectNode servletContext = JsonNodeFactory.instance.objectNode();

        servletContext.put("servletRegistrations", registrations);
        orlaConfig.put("servletContext", servletContext);

        res.setContentType("text/json");
        OutputStream outputStream = res.getOutputStream();
        outputStream.write(orlaConfig.toString().getBytes());
        outputStream.flush();
    }
}