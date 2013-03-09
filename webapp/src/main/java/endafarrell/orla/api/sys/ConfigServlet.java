package endafarrell.orla.api.sys;

import endafarrell.orla.api.OrlaHttpServlet;
import endafarrell.orla.service.OrlaConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@WebServlet(urlPatterns = {"/api/sys/config"}, name = "The configuration of the service")
public class ConfigServlet extends OrlaHttpServlet {

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

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode configJson = JsonNodeFactory.instance.objectNode();
        configJson.put("orlaConfig", mapper.convertValue(OrlaConfig.getInstance(), ObjectNode.class));
        ObjectNode servletContext = JsonNodeFactory.instance.objectNode();
        servletContext.put("servletRegistrations", registrations);
        configJson.put("servletContext", servletContext);

        res.setContentType("text/json");
        OutputStream outputStream = res.getOutputStream();
        outputStream.write(configJson.toString().getBytes());
        outputStream.flush();
    }
}