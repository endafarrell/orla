package endafarrell.orla.service;

import javax.servlet.http.Part;
import java.io.*;
import java.util.Collection;


public class FilePart implements Part {
    final File file;

    public FilePart(String pathname) {
        this.file = new File(pathname);
    }

    public InputStream getInputStream() throws FileNotFoundException {
        return new FileInputStream(this.file);
    }

    public String getContentType() {
        return "binary";
    }

    public String getName() {
        return this.file.getName();
    }

    public String getSubmittedFileName() { return this.file.getName(); }

    public long getSize() {
        return this.file.length();
    }

    public void write(String fileName) throws IOException { }

    public void delete() throws IOException {
        this.file.delete();
    }

    public String getHeader(String name) {
        return null;
    }

    public Collection<String> getHeaders(String name) {
        return null;
    }

    public Collection<String> getHeaderNames() {
        return null;
    }
}
