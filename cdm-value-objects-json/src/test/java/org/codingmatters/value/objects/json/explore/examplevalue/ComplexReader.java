package org.codingmatters.value.objects.json.explore.examplevalue;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.codingmatters.value.objects.json.value.ExampleValue;
import org.codingmatters.value.objects.json.value.examplevalue.Complex;

import java.io.IOException;

/**
 * Created by nelt on 3/27/17.
 */
public class ComplexReader {
    private final JsonFactory factory = new JsonFactory();

    public Complex readValue(JsonParser parser) throws IOException {
        JsonToken firstToken = parser.nextToken();
        if(firstToken == JsonToken.VALUE_NULL) return null;

        Complex.Builder builder = Complex.Builder.builder();
        if (firstToken != JsonToken.START_OBJECT) {
            throw new IOException(
                    String.format("reading a %s object, was expecting %s, but was %s",
                            ExampleValue.class.getName(), JsonToken.START_OBJECT, parser.currentToken()
                    )
            );
        }
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = parser.getCurrentName();
            if ("sub".equals(fieldName)) {
                this.readStringProperty(parser, builder);
            }
        }
        return builder.build();
    }

    private void readStringProperty(JsonParser parser, Complex.Builder builder) throws IOException {
        parser.nextToken();
        if (parser.currentToken() == JsonToken.VALUE_STRING) {
            builder.sub(parser.getText());
        } else if (parser.currentToken() == JsonToken.VALUE_NULL) {
            builder.sub(null);
        } else {
            throw new IOException(
                    String.format("reading property %s, was expecting %s, but was %s",
                            "prop", JsonToken.VALUE_STRING, parser.currentToken()
                    )
            );
        }
    }
}
