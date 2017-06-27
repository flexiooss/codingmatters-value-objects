package org.codingmatters.value.objects.json.value.explore.examplevalue;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.generated.ExampleValue;
import org.generated.examplevalue.ComplexList;

import java.io.IOException;

/**
 * Created by nelt on 3/29/17.
 */
public class ComplexListReader {

    public ComplexList read(JsonParser parser) throws IOException {
//        parser.nextToken();
        if(parser.currentToken() == JsonToken.VALUE_NULL) return null;

        ComplexList.Builder builder = ComplexList.builder();
        if (parser.currentToken() != JsonToken.START_OBJECT) {
            throw new IOException(
                    String.format("reading a %s object, was expecting %s, but was %s",
                            ExampleValue.class.getName(), JsonToken.START_OBJECT, parser.currentToken()
                    )
            );
        }
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = parser.getCurrentName();
            if ("sub".equals(fieldName)) {
                builder.sub(this.readString(parser, builder));
            }
        }
        return builder.build();
    }

    private String readString(JsonParser parser, ComplexList.Builder builder) throws IOException {
        parser.nextToken();
        if (parser.currentToken() == JsonToken.VALUE_STRING) {
            return parser.getText();
        } else if (parser.currentToken() == JsonToken.VALUE_NULL) {
            return null;
        } else {
            throw new IOException(
                    String.format("reading property %s, was expecting %s, but was %s",
                            "prop", JsonToken.VALUE_STRING, parser.currentToken()
                    )
            );
        }
    }
}
