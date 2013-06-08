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
import java.util.Collection;
import java.util.Map;
import java.util.Set;

@WebServlet(urlPatterns = {"/api/sys/config"}, name = "see config")
public class ConfigServlet extends OrlaHttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        String contextPath = this.getServletContext().getContextPath();
        ArrayNode registrations = JsonNodeFactory.instance.arrayNode();

        Map<String, ? extends ServletRegistration> servletRegistrations =
                this.getServletContext().getServletRegistrations();
        for (String registration : servletRegistrations.keySet()) {
            ObjectNode registrationNode = JsonNodeFactory.instance.objectNode();
            ArrayNode mappings = JsonNodeFactory.instance.arrayNode();

            ServletRegistration servletRegistration = servletRegistrations.get(registration);
            for (String mapping : servletRegistration.getMappings()) {
                mappings.add(mapping);
            }
            registrationNode.put("mappings", mappings);
            registrationNode.put("name", servletRegistration.getName());
            registrationNode.put("className", servletRegistration.getClassName());

            registrations.add(registrationNode);
        }

        ArrayNode navigations = JsonNodeFactory.instance.arrayNode();
        for (String registration : servletRegistrations.keySet()) {
            ServletRegistration servletRegistration = servletRegistrations.get(registration);
            Collection<String> mappings = servletRegistration.getMappings();
            if (mappings.size() == 1) {
                ObjectNode navigation = JsonNodeFactory.instance.objectNode();
                navigation.put("href", contextPath + mappings.toArray(new String[1])[0]);
                navigation.put("text", servletRegistration.getName());
                navigations.add(navigation);
            }
        }

        Set<String> resourcePaths = this.getServletContext().getResourcePaths("/");
        for (String resourcePath : resourcePaths) {
            System.out.println("resourcePath " + resourcePath);
            if (resourcePath.endsWith(".jsp")) {
                ObjectNode navigation = JsonNodeFactory.instance.objectNode();
                navigation.put("href", contextPath + resourcePath);
                navigation.put("text", resourcePath);
                navigations.add(navigation);
            } else if (resourcePath.endsWith("/")) {
                Set<String> subResourcePaths = this.getServletContext().getResourcePaths(resourcePath);
                for (String subResourcePath : subResourcePaths) {
                    if (subResourcePath.endsWith(".jsp")) {
                        ObjectNode navigation = JsonNodeFactory.instance.objectNode();
                        navigation.put("href", contextPath + subResourcePath);
                        navigation.put("text", subResourcePath);
                        navigations.add(navigation);
                    }
                }
            }
        }

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode configJson = JsonNodeFactory.instance.objectNode();
        configJson.put("orlaConfig", mapper.convertValue(OrlaConfig.getInstance(), ObjectNode.class));
        configJson.put("navigations", navigations);
        ObjectNode servletContext = JsonNodeFactory.instance.objectNode();
        servletContext.put("servletRegistrations", registrations);
        configJson.put("servletContext", servletContext);

        res.setContentType("text/json");
        OutputStream outputStream = res.getOutputStream();
        outputStream.write(configJson.toString().getBytes());
        outputStream.flush();
    }
}