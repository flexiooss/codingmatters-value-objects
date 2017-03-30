package org.codingmatters.value.objects.json.value.explore.examplevalue;

import com.fasterxml.jackson.core.JsonGenerator;
import org.generated.examplevalue.Complex;

import java.io.IOException;

/**
 * Created by nelt on 3/27/17.
 */
public class ComplexWriter {

    public void write(JsonGenerator generator, Complex value) throws IOException {
        if(value != null) {
            generator.writeStartObject();

            // simple string property
            generator.writeFieldName("sub");
            generator.writeString(value.sub());

            generator.writeEndObject();
        } else {
            generator.writeNull();
        }
    }

}
