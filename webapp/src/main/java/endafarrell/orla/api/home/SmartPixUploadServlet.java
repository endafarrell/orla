package endafarrell.orla.api.home;

import endafarrell.orla.api.OrlaHttpServlet;
import endafarrell.orla.service.processor.ProcessResults;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.Collection;

@WebServlet(urlPatterns = {SmartPixUploadServlet.URL}, name = "SmartPix data file upload")
@MultipartConfig(
        location = SmartPixUploadServlet.FILE_UPLOAD_LOCATION,
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 1024 * 1024 * 5,
        maxRequestSize = 1024 * 1024 * 5 * 5)
public class SmartPixUploadServlet extends OrlaHttpServlet {
    public static final String URL = "/api/home/smartpix";
    public static final String FILE_UPLOAD_LOCATION = "/var/data/endafarrell/orla/SmartPix";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        getServletContext().getRequestDispatcher("/smartpix.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Collection<Part> parts = req.getParts();
        for (Part part : parts) {
            if (getFileName(part)!=null) {
                ProcessResults result = orla.readSmartPix(part);
            }
        }
        res.sendRedirect(req.getContextPath());
    }

    String getFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        String[] cds = contentDisposition.split(";");
        for (String cd : cds) {
            if (cd.startsWith(" filename")) {
                String[] kv = cd.split("=");
                return kv[1].substring(1, kv[1].length() - 1);
            }
        }
        return null;
    }
}
