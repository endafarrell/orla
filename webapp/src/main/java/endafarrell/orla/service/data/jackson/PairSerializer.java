package endafarrell.orla.service.data.jackson;

import org.apache.commons.lang3.tuple.Pair;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;

import java.io.IOException;

public class PairSerializer<Integer,Double> extends SerializerBase<Pair<Integer,Double>> {
    public PairSerializer(Class<Pair<Integer, Double>> t) {
        super(t);
    }

    @Override
    public void serialize(Pair<Integer, Double> value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
        jgen.writeStartArray();
        jgen.writeNumber((java.lang.Integer) value.getLeft());
        jgen.writeNumber((java.lang.Double) value.getRight());
        jgen.writeEndArray();
    }
}
