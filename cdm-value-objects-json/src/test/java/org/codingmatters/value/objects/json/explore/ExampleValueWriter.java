package org.codingmatters.value.objects.json.explore;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.codingmatters.value.objects.json.explore.examplevalue.ComplexWriter;
import org.codingmatters.value.objects.json.value.ExampleValue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by nelt on 3/27/17.
 */
public class ExampleValueWriter {

    private final JsonFactory factory = new JsonFactory();

    public String writeValue(ExampleValue value) throws IOException {
        try (OutputStream out = new ByteArrayOutputStream()) {
            JsonGenerator generator = this.factory.createGenerator(out);
            generator.writeStartObject();

            // simple string property
            generator.writeFieldName("prop");
            generator.writeString(value.prop());

            // list or set property of simple elements
            generator.writeFieldName("listProp");
            this.writeSimpleArray(value, generator);

            // complex property
            generator.writeFieldName("complex");
            if(value.complex() != null) {
                new ComplexWriter().writeValue(generator, value.complex());
            } else {
                generator.writeNull();
            }

            generator.writeEndObject();
            generator.close();
            return out.toString();
        }
    }

    private void writeSimpleArray(ExampleValue value, JsonGenerator generator) throws IOException {
        if (value.listProp() != null) {
            generator.writeStartArray();
            for (String elmt : value.listProp()) {
                generator.writeString(elmt);
            }
            generator.writeEndArray();
        } else {
            generator.writeNull();
        }
    }
}
