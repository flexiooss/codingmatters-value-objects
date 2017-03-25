package org.codingmatters.value.objects.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.codingmatters.value.objects.json.value.SimpleValue;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 3/25/17.
 */
public class ExploreTest {

    @Test
    public void simpleValueWrite() throws Exception {
        assertThat(
                this.writeValueAsString(SimpleValue.Builder.builder()
                        .prop("a value")
                        .build()),
                is("{\"prop\":\"a value\"}")
        );
    }

    @Test
    public void simpleReadValue() throws Exception {
        assertThat(
                this.readValue("{\"prop\":\"a value\"}"),
                is(SimpleValue.Builder.builder()
                        .prop("a value")
                        .build())
        );
    }


    private JsonFactory factory = new JsonFactory();

    private String writeValueAsString(SimpleValue value) throws IOException {
        try(
                OutputStream out = new ByteArrayOutputStream()) {

            JsonGenerator generator = this.factory.createGenerator(out);
            generator.writeStartObject();
            generator.writeStringField("prop", value.prop());
            generator.writeEndObject();
            generator.close();
            return out.toString();
        }
    }

    private SimpleValue readValue(String json) throws IOException {
        try(JsonParser parser = this.factory.createParser(json.getBytes())) {
            SimpleValue.Builder builder = SimpleValue.Builder.builder();
            if (parser.nextToken() != JsonToken.START_OBJECT) {
                throw new IOException("Expected data to start with an Object");
            }
            while (parser.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = parser.getCurrentName();
                if("prop".equals(fieldName)) {
                    parser.nextToken();
                    builder.prop(parser.getText());
                }
            }
            return builder.build();
        }
    }
}
