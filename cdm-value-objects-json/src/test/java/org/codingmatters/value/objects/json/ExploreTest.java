package org.codingmatters.value.objects.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.codingmatters.value.objects.json.value.ExampleValue;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 3/25/17.
 */
public class ExploreTest {

    @Test
    public void simpleValueWrite() throws Exception {
        assertThat(
                this.writeValue(ExampleValue.Builder.builder()
                        .prop("a value")
                        .listProp("a", "b", "c")
                        .build()),
                is("{\"prop\":\"a value\",\"listProp\":[\"a\",\"b\",\"c\"]}")
        );
    }

    @Test
    public void simpleReadValue() throws Exception {
        assertThat(
                this.readValue("{\"prop\":\"a value\",\"listProp\":[\"a\",\"b\",\"c\"]}"),
                is(ExampleValue.Builder.builder()
                        .prop("a value")
                        .listProp("a", "b", "c")
                        .build())
        );
    }


    private JsonFactory factory = new JsonFactory();

    private String writeValue(ExampleValue value) throws IOException {
        try(OutputStream out = new ByteArrayOutputStream()) {
            JsonGenerator generator = this.factory.createGenerator(out);
            generator.writeStartObject();

            generator.writeFieldName("prop");
            this.writeSimpleValue(value, generator);

            generator.writeFieldName("listProp");
            this.writeSimpleArray(value, generator);

            generator.writeEndObject();
            generator.close();
            return out.toString();
        }
    }

    private void writeSimpleValue(ExampleValue value, JsonGenerator generator) throws IOException {
        generator.writeString(value.prop());
    }

    private void writeSimpleArray(ExampleValue value, JsonGenerator generator) throws IOException {
        generator.writeStartArray();
        for (String elmt : value.listProp()) {
            generator.writeString(elmt);
        }
        generator.writeEndArray();
    }

    private ExampleValue readValue(String json) throws IOException {
        try(JsonParser parser = this.factory.createParser(json.getBytes())) {
            ExampleValue.Builder builder = ExampleValue.Builder.builder();
            if (parser.nextToken() != JsonToken.START_OBJECT) {
                throw new IOException(
                        String.format("reading a %s object, was expecting %s, but was %s",
                                ExampleValue.class.getName(), JsonToken.START_ARRAY, parser.currentToken()
                        )
                );
            }
            while (parser.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = parser.getCurrentName();
                if("prop".equals(fieldName)) {
                    this.readSimpleProperty(parser, builder);
                } else if("listProp".equals(fieldName)) {
                    this.readSimpleArrayValue(parser, builder);
                }
            }
            return builder.build();
        }
    }

    private void readSimpleProperty(JsonParser parser, ExampleValue.Builder builder) throws IOException {
        parser.nextToken();
        if(parser.currentToken() == JsonToken.VALUE_STRING) {
            builder.prop(parser.getText());
        } else if(parser.currentToken() == JsonToken.VALUE_NULL) {
            builder.prop(null);
        }
    }

    private void readSimpleArrayValue(JsonParser parser, ExampleValue.Builder builder) throws IOException {
        parser.nextToken();
        if(parser.currentToken() == JsonToken.VALUE_NULL) {
            builder.listProp();
        } else {
            if(parser.currentToken() != JsonToken.START_ARRAY) {
                throw new IOException(
                        String.format("reading property %s, was expecting %s, but was %s",
                                "listProp", JsonToken.START_ARRAY, parser.currentToken()
                        )
                );
            }
            LinkedList<String> listValue = new LinkedList<>();
            while(parser.nextToken() != JsonToken.END_ARRAY) {
                listValue.add(parser.getText());
            }
            builder.listProp(listValue);
        }
    }
}
