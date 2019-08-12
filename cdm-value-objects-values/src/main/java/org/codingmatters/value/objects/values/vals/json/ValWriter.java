package org.codingmatters.value.objects.values.vals.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.SerializableString;
import org.codingmatters.value.objects.values.ObjectValue;
import org.codingmatters.value.objects.values.vals.Val;
import org.codingmatters.value.objects.values.vals.ValVisitor;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class ValWriter {
    public void write(JsonGenerator generator, Val value) throws IOException {
        Optional<IOException> result = value.accept(new WriterVisitor(generator));
        if(result.isPresent()) {
            throw result.get();
        }
    }

    public void writeArray(JsonGenerator generator, Val[] values) throws IOException {
        this.write(generator, Val.array().values(values).build());
    }

    static class WriterVisitor implements ValVisitor<Optional<IOException>> {
        private final JsonGenerator generator;

        WriterVisitor(JsonGenerator generator) {
            this.generator = generator;
        }

        @Override
        public Optional<IOException> visitObject(Val.ObjectVal object) {
            try {
                this.generator.writeStartObject();
                for (String propertyName : object.propertyNames()) {
                    this.generator.writeFieldName(propertyName);
                    if(object.property(propertyName) != null) {
                        object.property(propertyName).accept(this);
                    } else {
                        generator.writeNull();
                    }
                }
                this.generator.writeEndObject();
            } catch (IOException e) {
                return Optional.of(e);
            }
            return Optional.empty();
        }

        @Override
        public Optional<IOException> visitArray(Val.ArrayVal array) {
            try {
                this.generator.writeStartArray();
                for (Val value : array.values()) {
                    value.accept(this);
                }
                this.generator.writeEndArray();
            } catch(IOException e) {
                return Optional.of(e);
            }
            return Optional.empty();
        }

        @Override
        public <V> Optional<IOException> visitBaseValue(Val.BaseTypeVal<V> value) {
            try {
                switch (value.type()) {
                    case STRING:
                        generator.writeString((String) value.value());
                        break;
                    case LONG:
                        generator.writeNumber((Long) value.value());
                        break;
                    case DOUBLE:
                        generator.writeNumber((Double) value.value());
                        break;
                    case BOOLEAN:
                        generator.writeBoolean((Boolean) value.value());
                        break;
                    case BYTES:
                        generator.writeBinary((byte[]) value.value());
                        break;
                    case DATE:
                        generator.writeString(((LocalDate)value.value()).format(DateTimeFormatter.ISO_LOCAL_DATE));
                        break;
                    case TIME:
                        generator.writeString(((LocalTime)value.value()).format(DateTimeFormatter.ISO_LOCAL_TIME));
                        break;
                    case DATETIME:
                        generator.writeString(((LocalDateTime)value.value()).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                        break;
                }
            } catch(IOException e) {
                return Optional.of(e);
            }
            return Optional.empty();
        }
    }
}
