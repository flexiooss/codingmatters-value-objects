package org.codingmatters.value.objects.values.json;

import com.fasterxml.jackson.core.JsonGenerator;
import org.codingmatters.value.objects.values.ObjectValue;
import org.codingmatters.value.objects.values.PropertyValue;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class ObjectValueWriter {
    public void write(JsonGenerator generator, ObjectValue value) throws IOException {
        this.writeObject(generator, value);
    }

    public void writeArray(JsonGenerator generator, ObjectValue[] values) throws IOException {
        if(values == null) {
            generator.writeNull();
        } else {
            generator.writeStartArray();
            for(ObjectValue value : values) {
                this.write(generator, value);
            }
            generator.writeEndArray();
        }
    }

    private void writeValue(JsonGenerator generator, PropertyValue property) throws IOException {
        if(property.isNullValue()) {
            generator.writeNull();
        } else if(PropertyValue.Cardinality.SINGLE.equals(property.cardinality())) {
            this.writeSingleValue(generator, property.single(), property.type());
        } else if(PropertyValue.Cardinality.MULTIPLE.equals(property.cardinality())) {
            this.writeMultipleValue(generator, property.multiple(), property.type());
        }
    }

    private void writeSingleValue(JsonGenerator generator, PropertyValue.Value value, PropertyValue.Type type) throws IOException {
        switch (type) {
            case STRING:
                generator.writeString(value.stringValue());
                break;
            case LONG:
                generator.writeNumber(value.longValue());
                break;
            case DOUBLE:
                generator.writeNumber(value.doubleValue());
                break;
            case BOOLEAN:
                generator.writeBoolean(value.booleanValue());
                break;
            case BYTES:
                generator.writeBinary(value.bytesValue());
                break;
            case DATE:
                generator.writeString(value.dateValue().format(DateTimeFormatter.ISO_LOCAL_DATE));
                break;
            case TIME:
                generator.writeString(value.timeValue().format(DateTimeFormatter.ISO_LOCAL_TIME));
                break;
            case DATETIME:
                generator.writeString(value.datetimeValue().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                break;
            case OBJECT:
                this.writeObject(generator, value.objectValue());
                break;
        }
    }

    private void writeObject(JsonGenerator generator, ObjectValue objectValue) throws IOException {
        generator.writeStartObject();
        for (String property : objectValue.propertyNames()) {
            generator.writeFieldName(property);
            this.writeValue(generator, objectValue.property(property));
        }
        generator.writeEndObject();
    }

    private void writeMultipleValue(JsonGenerator generator, PropertyValue.Value[] multiple, PropertyValue.Type type) throws IOException {
        generator.writeStartArray();
        for (PropertyValue.Value value : multiple) {
            this.writeSingleValue(generator, value, value.type());
        }
        generator.writeEndArray();
    }
}
