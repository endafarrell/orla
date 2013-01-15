package endafarrell.orla.service.processor;

import javax.servlet.http.Part;
import java.io.IOException;

public abstract class ReceivingProcessor extends BaseProcessor {

    Part part;

    public void setInput(Part part) {
        this.part = part;
    }
}
