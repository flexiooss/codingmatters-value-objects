package org.codingmatters.value.objects.json.explore.examplevalue;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.codingmatters.value.objects.json.value.ExampleValue;
import org.codingmatters.value.objects.json.value.examplevalue.ComplexList;

import java.io.IOException;
import java.util.LinkedList;

/**
 * Created by nelt on 3/29/17.
 */
public class ComplexListReader {
    public ComplexList [] readValue(JsonParser parser) throws IOException {
        parser.nextToken();
        if (parser.currentToken() == JsonToken.VALUE_NULL) {
            return null;
        } else {
            if (parser.currentToken() != JsonToken.START_ARRAY) {
                throw new IOException(
                        String.format("reading property %s, was expecting %s, but was %s",
                                "listProp", JsonToken.START_ARRAY, parser.currentToken()
                        )
                );
            }
            LinkedList<ComplexList> listValue = new LinkedList<>();
            while (parser.nextToken() != JsonToken.END_ARRAY) {
                listValue.add(this.readElement(parser));
            }
            return listValue.toArray(new ComplexList[listValue.size()]);
        }

    }

    public ComplexList readElement(JsonParser parser) throws IOException {
        JsonToken firstToken = parser.currentToken();
        if(firstToken == JsonToken.VALUE_NULL) return null;

        ComplexList.Builder builder = ComplexList.Builder.builder();
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

    private void readStringProperty(JsonParser parser, ComplexList.Builder builder) throws IOException {
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
