package org.codingmatters.value.objects.values.vals.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.codingmatters.value.objects.values.vals.Val;

import java.io.IOException;

public class ValReader {
    public Val read(JsonParser parser) throws IOException {
        if(parser.currentToken() == null) {
            parser.nextToken();
        }

        return this.parse(parser);
    }

    private Val parse(JsonParser parser) throws IOException {
        if(parser.currentToken() == JsonToken.VALUE_NULL) return null;

        if(parser.currentToken() == JsonToken.START_OBJECT) {
            return this.parseObject(parser);
        } else if(parser.currentToken() == JsonToken.START_ARRAY) {
            return this.parseArray(parser);
        } else if(parser.currentToken().isScalarValue()) {
            return this.parseBaseType(parser);
        } else {
            throw new IOException("don't know what to do with token : " + parser.currentToken());
        }
    }

    private Val parseBaseType(JsonParser parser) throws IOException {
        if (parser.currentToken().isBoolean()) {
            return Val.booleanValue(Boolean.parseBoolean(parser.getText()));
        } else if (parser.currentToken().isNumeric()) {
            if (parser.getText().contains(".")) {
                return Val.doubleValue(Double.parseDouble(parser.getText()));
            } else {
                return Val.longValue(Long.parseLong(parser.getText()));
            }
        } else {
            return Val.stringValue(parser.getText());
        }
    }

    private Val parseArray(JsonParser parser) throws IOException {
        Val.ArrayVal.Builder builder = Val.array();
        while (parser.nextToken() != JsonToken.END_ARRAY) {
            builder.with(this.parse(parser));
        }
        return builder.build();
    }

    private Val parseObject(JsonParser parser) throws IOException {
        Val.ObjectVal.Builder builder = Val.object();
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String propertyName = parser.getCurrentName();
            parser.nextToken();
            builder.property(propertyName, this.parse(parser));
        }
        return builder.build();
    }
}
