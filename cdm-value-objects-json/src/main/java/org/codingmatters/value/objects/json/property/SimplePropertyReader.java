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
    DATE("getText", LocalDate.class, new TemporalPropertyReader(LocalDate.class), JsonToken.VALUE_STRING),
    TIME("getText", LocalTime.class, new TemporalPropertyReader(LocalTime.class), JsonToken.VALUE_STRING),
    DATE_TIME("getText", LocalDateTime.class, new TemporalPropertyReader(LocalDateTime.class), JsonToken.VALUE_STRING),
    TZ_DATE_TIME("getText", ZonedDateTime.class, new TemporalPropertyReader(ZonedDateTime.class), JsonToken.VALUE_STRING),
    ;

    private final Set<JsonToken> expectedToken;
    private final String parserMethod;
    private final String className;
    private final PropertyStatement propertyStatement;

    SimplePropertyReader(String parserMethod, Class clazz, JsonToken... expectedTokens) {
        this(parserMethod, clazz, new SimplePropertyStatement(), expectedTokens);
    }
    SimplePropertyReader(String parserMethod, Class clazz, PropertyStatement propertyStatement, JsonToken... expectedTokens) {
        this.propertyStatement = propertyStatement;
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
        this.propertyStatement.addSingleStatement(method, propertySpec, this);
    }

    public void addMultipleStatement(MethodSpec.Builder method, PropertySpec propertySpec) {
        this.propertyStatement.addMultipleStatement(method, propertySpec, this);
    }

    static public SimplePropertyReader forClassName(String className) {
        for (SimplePropertyReader value : values()) {
            if(value.className.equals(className)) {
                return value;
            }
        }
        return null;
    }

    interface PropertyStatement {
        void addSingleStatement(MethodSpec.Builder method, PropertySpec propertySpec, SimplePropertyReader propertyReader);
        void addMultipleStatement(MethodSpec.Builder method, PropertySpec propertySpec, SimplePropertyReader propertyReader);
    }

    static class SimplePropertyStatement implements PropertyStatement{
        @Override
        public void addSingleStatement(MethodSpec.Builder method, PropertySpec propertySpec, SimplePropertyReader propertyReader) {
        /*
            builder.prop(this.readValue(parser, jsonParser -> jsonParser.getText(), "prop", expectedTokens));
         */
            method.addStatement("builder.$L(this.readValue(parser, jsonParser -> jsonParser.$L(), $S, expectedTokens))",
                    propertySpec.name(), propertyReader.parserMethod(), propertySpec.name()
            );
        }

        @Override
        public void addMultipleStatement(MethodSpec.Builder method, PropertySpec propertySpec, SimplePropertyReader propertyReader) {
        /*
        builder.listProp(this.readListValue(parser, jsonParser -> jsonParser.getText(), "listProp"));
         */
            method.addStatement("builder.$L(this.readListValue(parser, jsonParser -> jsonParser.$L(), $S))",
                    propertySpec.name(), propertyReader.parserMethod(), propertySpec.name()
            );
        }
    }

    static class TemporalPropertyReader implements PropertyStatement {
        private final Class temporalClass;

        TemporalPropertyReader(Class temporalClass) {
            this.temporalClass = temporalClass;
        }

        @Override
        public void addSingleStatement(MethodSpec.Builder method, PropertySpec propertySpec, SimplePropertyReader propertyReader) {
            method.addStatement("builder.$L(this.readValue(parser, jsonParser -> $T.parse(jsonParser.$L()), $S, expectedTokens))",
                    propertySpec.name(), this.temporalClass, propertyReader.parserMethod(), propertySpec.name()
            );
        }

        @Override
        public void addMultipleStatement(MethodSpec.Builder method, PropertySpec propertySpec, SimplePropertyReader propertyReader) {
            method.addStatement("builder.$L(this.readListValue(parser, jsonParser -> $T.parse(jsonParser.$L()), $S))",
                    propertySpec.name(), this.temporalClass, propertyReader.parserMethod(), propertySpec.name()
            );
        }
    }
}
