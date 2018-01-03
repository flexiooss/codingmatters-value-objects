package org.codingmatters.value.objects.json.property;

import com.fasterxml.jackson.core.JsonToken;
import org.codingmatters.value.objects.json.property.statement.PropertyStatement;
import org.codingmatters.value.objects.json.property.statement.SimplePropertyStatement;
import org.codingmatters.value.objects.json.property.statement.TemporalPropertyStatement;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by nelt on 4/11/17.
 */
public enum SimplePropertyReaders {
    STRING("getText", String.class, JsonToken.VALUE_STRING),
    INTEGER("getIntValue", Integer.class, JsonToken.VALUE_NUMBER_INT),
    LONG("getLongValue", Long.class, JsonToken.VALUE_NUMBER_INT),
    FLOAT("getFloatValue", Float.class, JsonToken.VALUE_NUMBER_FLOAT),
    DOUBLE("getDoubleValue", Double.class, JsonToken.VALUE_NUMBER_FLOAT),
    BOOLEAN("getBooleanValue", Boolean.class, JsonToken.VALUE_TRUE, JsonToken.VALUE_FALSE),
    BINARY("getBinaryValue", byte[].class, JsonToken.VALUE_STRING),
    DATE("getText", LocalDate.class, new TemporalPropertyStatement(LocalDate.class), JsonToken.VALUE_STRING),
    TIME("getText", LocalTime.class, new TemporalPropertyStatement(LocalTime.class), JsonToken.VALUE_STRING),
    DATE_TIME("getText", LocalDateTime.class, new TemporalPropertyStatement(LocalDateTime.class), JsonToken.VALUE_STRING),
    TZ_DATE_TIME("getText", ZonedDateTime.class, new TemporalPropertyStatement(ZonedDateTime.class), JsonToken.VALUE_STRING),
    ;

    private final String className;
    private final SimplePropertyReaderProducer producer;

    SimplePropertyReaders(String parserMethod, Class clazz, JsonToken... expectedTokens) {
        this(parserMethod, clazz, new SimplePropertyStatement(), expectedTokens);
    }
    SimplePropertyReaders(String parserMethod, Class clazz, PropertyStatement propertyStatement, JsonToken... expectedTokens) {
        this.className = clazz.getName();

        this.producer = new SimplePropertyReaderProducer(
                expectedTokens != null ? new HashSet<>(Arrays.asList(expectedTokens)) : new HashSet<>(),
                parserMethod,
                propertyStatement
                );
    }

    public SimplePropertyReaderProducer producer() {
        return this.producer;
    }

    static public SimplePropertyReaders forClassName(String className) {
        for (SimplePropertyReaders value : values()) {
            if(value.className.equals(className)) {
                return value;
            }
        }
        return null;
    }

}
