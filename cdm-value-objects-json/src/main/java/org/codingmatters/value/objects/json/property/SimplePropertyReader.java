package org.codingmatters.value.objects.json.property;

import com.fasterxml.jackson.core.JsonToken;
import com.squareup.javapoet.MethodSpec;
import org.codingmatters.value.objects.spec.PropertySpec;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by nelt on 4/11/17.
 */
public enum SimplePropertyReader {
    STRING("getText", String.class, JsonToken.VALUE_STRING),
    INTEGER("getIntValue", Integer.class, JsonToken.VALUE_NUMBER_INT),
    LONG("getLongValue", Long.class, JsonToken.VALUE_NUMBER_INT),
    FLOAT("getFloatValue", Float.class, JsonToken.VALUE_NUMBER_FLOAT),
    DOUBLE("getDoubleValue", Double.class, JsonToken.VALUE_NUMBER_FLOAT),
    BOOLEAN("getBooleanValue", Boolean.class, JsonToken.VALUE_TRUE, JsonToken.VALUE_FALSE),
    DATE("getText", LocalDate.class, JsonToken.VALUE_STRING) {
        @Override
        public void addSingleStatement(MethodSpec.Builder method, PropertySpec propertySpec) {
            method.addStatement("builder.$L(this.readValue(parser, jsonParser -> $T.parse(jsonParser.$L()), $S, expectedTokens))",
                    propertySpec.name(), LocalDate.class, this.parserMethod(), propertySpec.name()
            );
        }

        @Override
        public void addMultipleStatement(MethodSpec.Builder method, PropertySpec propertySpec) {
            method.addStatement("builder.$L(this.readListValue(parser, jsonParser -> $T.parse(jsonParser.$L()), $S))",
                    propertySpec.name(), LocalDate.class, this.parserMethod(), propertySpec.name()
            );
        }
    },
    TIME("getText", LocalTime.class, JsonToken.VALUE_STRING){
        @Override
        public void addSingleStatement(MethodSpec.Builder method, PropertySpec propertySpec) {
            method.addStatement("builder.$L(this.readValue(parser, jsonParser -> $T.parse(jsonParser.$L()), $S, expectedTokens))",
                    propertySpec.name(), LocalTime.class, this.parserMethod(), propertySpec.name()
            );
        }

        @Override
        public void addMultipleStatement(MethodSpec.Builder method, PropertySpec propertySpec) {
            method.addStatement("builder.$L(this.readListValue(parser, jsonParser -> $T.parse(jsonParser.$L()), $S))",
                    propertySpec.name(), LocalTime.class, this.parserMethod(), propertySpec.name()
            );
        }
    },
    DATE_TIME("getText", LocalDateTime.class, JsonToken.VALUE_STRING){
        @Override
        public void addSingleStatement(MethodSpec.Builder method, PropertySpec propertySpec) {
            method.addStatement("builder.$L(this.readValue(parser, jsonParser -> $T.parse(jsonParser.$L()), $S, expectedTokens))",
                    propertySpec.name(), LocalDateTime.class, this.parserMethod(), propertySpec.name()
            );
        }

        @Override
        public void addMultipleStatement(MethodSpec.Builder method, PropertySpec propertySpec) {
            method.addStatement("builder.$L(this.readListValue(parser, jsonParser -> $T.parse(jsonParser.$L()), $S))",
                    propertySpec.name(), LocalDateTime.class, this.parserMethod(), propertySpec.name()
            );
        }
    },
    TZ_DATE_TIME("getText", ZonedDateTime.class, JsonToken.VALUE_STRING){
        @Override
        public void addSingleStatement(MethodSpec.Builder method, PropertySpec propertySpec) {
            method.addStatement("builder.$L(this.readValue(parser, jsonParser -> $T.parse(jsonParser.$L()), $S, expectedTokens))",
                    propertySpec.name(), ZonedDateTime.class, this.parserMethod(), propertySpec.name()
            );
        }

        @Override
        public void addMultipleStatement(MethodSpec.Builder method, PropertySpec propertySpec) {
            method.addStatement("builder.$L(this.readListValue(parser, jsonParser -> $T.parse(jsonParser.$L()), $S))",
                    propertySpec.name(), ZonedDateTime.class, this.parserMethod(), propertySpec.name()
            );
        }
    },
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

    public void addSingleStatement(MethodSpec.Builder method, PropertySpec propertySpec) {
        method.addStatement("builder.$L(this.readValue(parser, jsonParser -> jsonParser.$L(), $S, expectedTokens))",
                propertySpec.name(), this.parserMethod(), propertySpec.name()
        );
    }

    public void addMultipleStatement(MethodSpec.Builder method, PropertySpec propertySpec) {
        method.addStatement("builder.$L(this.readListValue(parser, jsonParser -> jsonParser.$L(), $S))",
                propertySpec.name(), this.parserMethod(), propertySpec.name()
        );
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
