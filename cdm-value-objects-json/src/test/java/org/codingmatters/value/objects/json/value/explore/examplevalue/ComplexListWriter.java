package org.codingmatters.value.objects.json.value.explore.examplevalue;

import com.fasterxml.jackson.core.JsonGenerator;
import org.generated.ValueList;
import org.generated.examplevalue.ComplexList;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by nelt on 3/29/17.
 */
public class ComplexListWriter {

    public void write(JsonGenerator generator, ValueList<ComplexList> value) throws IOException {
        if(value != null) {
            generator.writeStartArray();
            for (ComplexList element : value) {
                this.writeElement(generator, element);
            }
            generator.writeEndArray();
        } else {
            generator.writeNull();
        }
    }

    private String writeElement(JsonGenerator generator, ComplexList value) throws IOException {
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
