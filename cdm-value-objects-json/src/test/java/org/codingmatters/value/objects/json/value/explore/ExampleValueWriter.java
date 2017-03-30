package org.codingmatters.value.objects.json.value.explore;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.codingmatters.value.objects.json.value.ExampleValue;
import org.codingmatters.value.objects.json.value.explore.examplevalue.ComplexListWriter;
import org.codingmatters.value.objects.json.value.explore.examplevalue.ComplexWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by nelt on 3/27/17.
 */
public class ExampleValueWriter {

    private final JsonFactory factory = new JsonFactory();

    public String write(ExampleValue value) throws IOException {
        try (OutputStream out = new ByteArrayOutputStream()) {
            JsonGenerator generator = this.factory.createGenerator(out);
            generator.writeStartObject();

            // simple string property
            generator.writeFieldName("prop");
            generator.writeString(value.prop());

            // collection property of simple elewriteValuements
            generator.writeFieldName("listProp");
            this.writeSimpleArray(value, generator);

            // complex property
            generator.writeFieldName("complex");
            new ComplexWriter().write(generator, value.complex());

            // complex array
            generator.writeFieldName("complexList");
            new ComplexListWriter().write(generator, value.complexList());

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
