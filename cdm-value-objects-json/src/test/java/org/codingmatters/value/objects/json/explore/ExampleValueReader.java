package org.codingmatters.value.objects.json.explore;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.codingmatters.value.objects.json.explore.examplevalue.ComplexListReader;
import org.codingmatters.value.objects.json.explore.examplevalue.ComplexReader;
import org.codingmatters.value.objects.json.value.ExampleValue;

import java.io.IOException;
import java.util.LinkedList;

/**
 * Created by nelt on 3/27/17.
 */
public class ExampleValueReader {
    private final JsonFactory factory = new JsonFactory();

    public ExampleValue readValue(String json) throws IOException {
        try (JsonParser parser = this.factory.createParser(json.getBytes())) {
            JsonToken firstToken = parser.nextToken();
            if(firstToken == JsonToken.VALUE_NULL) return null;

            ExampleValue.Builder builder = ExampleValue.Builder.builder();
            if (firstToken != JsonToken.START_OBJECT) {
                throw new IOException(
                        String.format("reading a %s object, was expecting %s, but was %s",
                                ExampleValue.class.getName(), JsonToken.START_OBJECT, parser.currentToken()
                        )
                );
            }
            while (parser.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = parser.getCurrentName();
                if ("prop".equals(fieldName)) {
                    this.readStringProperty(parser, builder);
                } else if ("listProp".equals(fieldName)) {
                    this.readSimpleArrayValue(parser, builder);
                } else if ("complex".equals(fieldName)) {
                    builder.complex(new ComplexReader().readValue(parser));
                } else if ("complexList".equals(fieldName)) {
                    builder.complexList(new ComplexListReader().readValue(parser));
                }
            }
            return builder.build();
        }
    }

    private void readStringProperty(JsonParser parser, ExampleValue.Builder builder) throws IOException {
        parser.nextToken();
        if (parser.currentToken() == JsonToken.VALUE_STRING) {
            builder.prop(parser.getText());
        } else if (parser.currentToken() == JsonToken.VALUE_NULL) {
            builder.prop(null);
        } else {
            throw new IOException(
                    String.format("reading property %s, was expecting %s, but was %s",
                            "prop", JsonToken.VALUE_STRING, parser.currentToken()
                    )
            );
        }
    }

    private void readSimpleArrayValue(JsonParser parser, ExampleValue.Builder builder) throws IOException {
        parser.nextToken();
        if (parser.currentToken() == JsonToken.VALUE_NULL) {
            builder.listProp();
        } else {
            if (parser.currentToken() != JsonToken.START_ARRAY) {
                throw new IOException(
                        String.format("reading property %s, was expecting %s, but was %s",
                                "listProp", JsonToken.START_ARRAY, parser.currentToken()
                        )
                );
            }
            LinkedList<String> listValue = new LinkedList<>();
            while (parser.nextToken() != JsonToken.END_ARRAY) {
                listValue.add(parser.getText());
            }
            builder.listProp(listValue);
        }
    }
}
