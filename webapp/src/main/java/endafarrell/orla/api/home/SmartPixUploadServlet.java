package endafarrell.orla.api.home;

import endafarrell.orla.service.Event;
import endafarrell.orla.service.Orla;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;

@WebServlet(urlPatterns = {SmartPixUploadServlet.URL}, name = "API smartpix data-file uploads")
@MultipartConfig(
        location = SmartPixUploadServlet.FILE_UPLOAD_LOCATION,
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 1024 * 1024 * 5,
        maxRequestSize = 1024 * 1024 * 5 * 5)
public class SmartPixUploadServlet extends HttpServlet {
    public static final String URL = "/api/home/smartpix";
    public static final String FILE_UPLOAD_LOCATION = Orla.DATA_DIR + "/SmartPix";

    private Orla orla;

    @Override
    public void init() {
        this.orla = Orla.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        getServletContext().getRequestDispatcher("/smartpix.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Collection<Part> parts = req.getParts();
        System.out.println("doPost called with " + parts.size() + " parts.");
        for (Part part : parts) {
            try {
                System.out.println(part);
                // This can be whatever :-)
                String fileName = getFileName(part);
                System.out.println(fileName);

                String date = Event.dateTimeFormat.format(new Date()).replace(" ", "-");
                System.out.println(date);

                part.write(date + "-" + fileName);
                File smartPix = new File(FILE_UPLOAD_LOCATION + "/" + date + "-" + fileName);
                System.out.println(smartPix.getAbsolutePath());
                System.out.println(smartPix.length());
                FileInputStream fis = new FileInputStream(FILE_UPLOAD_LOCATION + "/" + date + "-" + fileName);
                orla.readSmartPixStream(fis);
            } catch (NullPointerException npe) {
                System.err.println("I guess \"" + part.getName() + "\" which has a content-type of "
                        + part.getContentType() + " isn't really a file ;-)");
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
