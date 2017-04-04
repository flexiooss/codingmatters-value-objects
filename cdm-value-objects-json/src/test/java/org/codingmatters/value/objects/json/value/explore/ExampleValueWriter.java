package org.codingmatters.value.objects.json.value.explore;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.codingmatters.value.objects.json.value.explore.examplevalue.ComplexListWriter;
import org.codingmatters.value.objects.json.value.explore.examplevalue.ComplexWriter;
import org.generated.ExampleValue;
import org.generated.ValueList;

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
            this.write(generator, value);
            generator.close();
            return out.toString();
        }
    }

    public void write(JsonGenerator generator, ExampleValue value) throws IOException {
        generator.writeStartObject();

        // simple string property
        generator.writeFieldName("prop");
        generator.writeString(value.prop());

        // collection property of simple elements
        generator.writeFieldName("listProp");
        this.writeStringArray(generator, value.listProp());

        // complex property
        generator.writeFieldName("complex");
        new ComplexWriter().write(generator, value.complex());

        // complex array
        generator.writeFieldName("complexList");
        new ComplexListWriter().write(generator, value.complexList());

        generator.writeEndObject();
    }

    private void writeStringArray(JsonGenerator generator, ValueList<String> elements) throws IOException {
        if (elements != null) {
            generator.writeStartArray();
            for (String elmt : elements) {
                generator.writeString(elmt);
            }
            generator.writeEndArray();
        } else {
            generator.writeNull();
        }
    }
}
