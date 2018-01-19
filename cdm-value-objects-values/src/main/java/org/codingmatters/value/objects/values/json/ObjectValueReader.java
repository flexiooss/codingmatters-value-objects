package org.codingmatters.value.objects.values.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.codingmatters.value.objects.values.ObjectValue;
import org.codingmatters.value.objects.values.PropertyValue;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ObjectValueReader {

    public ObjectValue read(JsonParser parser) throws IOException {
        if(parser.getCurrentToken() == null) {
            parser.nextToken();
        }
        if(parser.currentToken() == JsonToken.VALUE_NULL) return null;
        if(parser.currentToken() != JsonToken.START_OBJECT) {
            throw new IOException(
                    String.format("reading a %s object, was expecting %s, but was %s",
                            ObjectValue.class.getName(), JsonToken.START_OBJECT, parser.currentToken()
                    )
            );
        }

        return this.objectValue(parser).build();
    }

    private ObjectValue.Builder objectValue(JsonParser parser) throws IOException {
        ObjectValue.Builder builder = ObjectValue.builder();
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String propertyName = parser.getCurrentName();
            PropertyValue propertyValue = null;

            parser.nextToken();
            if(parser.currentToken() == JsonToken.START_ARRAY) {
                propertyValue = this.multiplePropertyValue(parser);
            } else {
                propertyValue = this.singlePropertyValue(parser);
            }

            builder.property(propertyName, propertyValue);

        }
        return builder;
    }

    private PropertyValue multiplePropertyValue(JsonParser parser) throws IOException {
        List<PropertyValue.Value> values = new LinkedList<>();
        while (parser.nextToken() != JsonToken.END_ARRAY) {
            values.add(this.valueBuilder(parser).buildValue());
        }

        PropertyValue.Type type = values.isEmpty() ? PropertyValue.Type.STRING : values.get(0).type();
        return PropertyValue.multiple(type, values.toArray(new PropertyValue.Value[values.size()]));
    }

    private PropertyValue.Builder valueBuilder(JsonParser parser) throws IOException {
        if(parser.currentToken() == JsonToken.VALUE_NULL) {
            return PropertyValue.builder()
                    .stringValue(null);
        } else if(parser.currentToken().isScalarValue()) {
            if (parser.currentToken().isBoolean()) {
                return PropertyValue.builder()
                        .booleanValue(Boolean.parseBoolean(parser.getText()));
            } else if (parser.currentToken().isNumeric()) {
                if (parser.getText().contains(".")) {
                    return PropertyValue.builder()
                            .doubleValue(Double.parseDouble(parser.getText()));
                } else {
                    return PropertyValue.builder()
                            .longValue(Long.parseLong(parser.getText()));
                }
            } else {
                return PropertyValue.builder()
                        .stringValue(parser.getText());
            }
        } else if(parser.currentToken() == JsonToken.START_OBJECT) {
            return PropertyValue.builder().objectValue(this.objectValue(parser));
        } else {
            return PropertyValue.builder();
        }
    }

    private PropertyValue singlePropertyValue(JsonParser parser) throws IOException {
        return this.valueBuilder(parser).build();
    }

    public ObjectValue[] readArray(JsonParser parser) throws IOException {
        parser.nextToken();
        if (parser.currentToken() == JsonToken.VALUE_NULL) return null;
        if (parser.currentToken() == JsonToken.START_ARRAY) {
            LinkedList<ObjectValue> listValue = new LinkedList<>();
            while (parser.nextToken() != JsonToken.END_ARRAY) {
                if(parser.currentToken() == JsonToken.VALUE_NULL) {
                    listValue.add(null);
                } else {
                    listValue.add(this.read(parser));
                }
            }
            return listValue.toArray(new ObjectValue[listValue.size()]);
        }
        throw new IOException(String.format("failed reading org.codingmatters.value.objects.demo.books.Book array, current token was %s", parser.currentToken()));
    }
}
