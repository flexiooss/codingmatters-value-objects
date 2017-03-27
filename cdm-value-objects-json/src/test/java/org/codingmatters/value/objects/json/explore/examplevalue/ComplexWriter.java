package org.codingmatters.value.objects.json.explore.examplevalue;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.codingmatters.value.objects.json.value.examplevalue.Complex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by nelt on 3/27/17.
 */
public class ComplexWriter {

    private final JsonFactory factory = new JsonFactory();

    public String writeValue(JsonGenerator generator, Complex value) throws IOException {
        try (OutputStream out = new ByteArrayOutputStream()) {
            generator.writeStartObject();

            // simple string property
            generator.writeFieldName("sub");
            generator.writeString(value.sub());

            generator.writeEndObject();
            return out.toString();
        }
    }

}
