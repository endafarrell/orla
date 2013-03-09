package endafarrell.orla.service.processor;

import endafarrell.orla.service.Orla;

import javax.servlet.http.Part;

public abstract class ReceivingProcessor extends BaseProcessor {

    Part part;
    String fileName;

    public ReceivingProcessor(Orla orla) {
        super(orla);
    }

    public void setInput(String fileName, Part part) {
        this.fileName = fileName;
        this.part = part;
    }
}
