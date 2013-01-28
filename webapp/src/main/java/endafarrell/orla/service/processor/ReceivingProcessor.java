package endafarrell.orla.service.processor;

import endafarrell.orla.service.Orla;

import javax.servlet.http.Part;

public abstract class ReceivingProcessor extends BaseProcessor {

    Part part;

    public ReceivingProcessor(Orla orla) {
        super(orla);
    }

    public void setInput(Part part) {
        this.part = part;
    }
}
