package org.codingmatters.value.objects.json.value.explore;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.codingmatters.value.objects.json.value.explore.examplevalue.ComplexListReader;
import org.codingmatters.value.objects.json.value.explore.examplevalue.ComplexReader;
import org.generated.ExampleValue;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by nelt on 3/27/17.
 */
public class ExampleValueReader {
    private final JsonFactory factory = new JsonFactory();

    public ExampleValue read(String json) throws IOException {
        try (JsonParser parser = this.factory.createParser(json.getBytes())) {
            parser.nextToken();
            return this.read(parser);
        }
    }

    public ExampleValue read(JsonParser parser) throws IOException {
        if(parser.currentToken() == JsonToken.VALUE_NULL) return null;

        if(parser.currentToken() != JsonToken.START_OBJECT) {
            throw new IOException(
                    String.format("reading a %s object, was expecting %s, but was %s",
                            ExampleValue.class.getName(), JsonToken.START_OBJECT, parser.currentToken()
                    )
            );
        }
        ExampleValue.Builder builder = ExampleValue.builder();
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = parser.getCurrentName();
            switch (fieldName) {
                case "prop":
                    builder.prop(this.readValue(parser, JsonToken.VALUE_STRING, jsonParser -> jsonParser.getText(), "prop"));
                    break;
                case "listProp":
                    builder.listProp(this.readListValue(parser, jsonParser -> jsonParser.getText(), "listProp"));
                    break;
                case "complex":
                    parser.nextToken();
                    builder.complex(new ComplexReader().read(parser));
                    break;
                case "complexList":
                    ComplexListReader reader = new ComplexListReader();
                    builder.complexList(this.readListValue(parser, jsonParser -> reader.read(jsonParser), "complexList"));
                    break;
            }
        }
        return builder.build();
    }

    @FunctionalInterface
    private interface Reader<T> {
        T read(JsonParser parser) throws IOException;
    }

    private <T> T readValue(JsonParser parser, JsonToken expectedToken, Reader<T> reader, String propertyName) throws IOException {
        parser.nextToken();
        if (parser.currentToken() == JsonToken.VALUE_NULL) return null;
        if (parser.currentToken() == expectedToken) return reader.read(parser);
        throw new IOException(
                String.format("reading property %s, was expecting %s, but was %s",
                        propertyName, expectedToken, parser.currentToken()
                )
        );
    }

    private <T> List<T> readListValue(JsonParser parser, Reader<T> reader, String propertyName) throws IOException {
        parser.nextToken();
        if (parser.currentToken() == JsonToken.VALUE_NULL) return null;
        if (parser.currentToken() == JsonToken.START_ARRAY) {
            LinkedList<T> listValue = new LinkedList<>();
            while (parser.nextToken() != JsonToken.END_ARRAY) {
                listValue.add(reader.read(parser));
            }
            return listValue;
        }
        throw new IOException(
                String.format("reading property %s, was expecting %s, but was %s",
                        propertyName, JsonToken.START_ARRAY, parser.currentToken()
                )
        );
    }
}
