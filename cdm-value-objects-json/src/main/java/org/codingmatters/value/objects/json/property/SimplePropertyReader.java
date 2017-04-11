package org.codingmatters.value.objects.json.property;

import com.fasterxml.jackson.core.JsonToken;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by nelt on 4/11/17.
 */
public enum SimplePropertyReader {
    /*
    parser.getIntValue();
        parser.getLongValue();
        parser.getFloatValue();
        parser.getDoubleValue();
     */
    STRING("getText", String.class, JsonToken.VALUE_STRING),
    INTEGER("getIntValue", Integer.class, JsonToken.VALUE_NUMBER_INT),
    LONG("getLongValue", Long.class, JsonToken.VALUE_NUMBER_INT),
    FLOAT("getFloatValue", Float.class, JsonToken.VALUE_NUMBER_FLOAT),
    DOUBLE("getDoubleValue", Double.class, JsonToken.VALUE_NUMBER_FLOAT),
    BOOLEAN("getBooleanValue", Boolean.class, JsonToken.VALUE_TRUE, JsonToken.VALUE_FALSE),
    DATE("getText", LocalDate.class, JsonToken.VALUE_STRING),
    ;

    private final Set<JsonToken> expectedToken;
    private final String parserMethod;
    private final String className;

    SimplePropertyReader(String parserMethod, Class clazz, JsonToken ... expectedTokens) {
        this.expectedToken = expectedTokens != null ? new HashSet<>(Arrays.asList(expectedTokens)) : new HashSet<>();
        this.parserMethod = parserMethod;
        this.className = clazz.getName();
    }

    public Set<JsonToken> expectedTokens() {
        return expectedToken;
    }

    public String parserMethod() {
        return parserMethod;
    }

    static public SimplePropertyReader forClassName(String className) {
        for (SimplePropertyReader value : values()) {
            if(value.className.equals(className)) {
                return value;
            }
        }
        return null;
    }
}
