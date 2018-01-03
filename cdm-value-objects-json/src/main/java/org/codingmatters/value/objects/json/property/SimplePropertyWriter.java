package org.codingmatters.value.objects.json.property;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import org.codingmatters.value.objects.spec.PropertySpec;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by nelt on 4/6/17.
 */
public enum SimplePropertyWriter {
    STRING(String.class) {
        @Override
        public void singleStatement(MethodSpec.Builder method, PropertySpec propertySpec) {
            method.addStatement("generator.writeString(value.$L())", propertySpec.name());
        }

        @Override
        protected void arrayElementStatement(MethodSpec.Builder method) {
            method.addStatement("generator.writeString(element)");
        }
    },
    NUMBER(Integer.class, Long.class, Float.class, Double.class) {
        @Override
        public void singleStatement(MethodSpec.Builder method, PropertySpec propertySpec) {
            method.addStatement("generator.writeNumber(value.$L())", propertySpec.name());
        }

        @Override
        protected void arrayElementStatement(MethodSpec.Builder method) {
            method.addStatement("generator.writeNumber(element)");
        }
    },
    BOOLEAN(Boolean.class) {
        @Override
        public void singleStatement(MethodSpec.Builder method, PropertySpec propertySpec) {
            method.addStatement("generator.writeBoolean(value.$L())", propertySpec.name());
        }

        @Override
        protected void arrayElementStatement(MethodSpec.Builder method) {
            method.addStatement("generator.writeBoolean(element)");
        }
    },
    BINARY(byte[].class) {
        @Override
        public void singleStatement(MethodSpec.Builder method, PropertySpec propertySpec) {
            method.addStatement("generator.writeBinary(value.$L())", propertySpec.name());
        }

        @Override
        protected void arrayElementStatement(MethodSpec.Builder method) {
            method.addStatement("generator.writeBinary(element)");
        }
    },DATE(LocalDate.class) {
        @Override
        public void singleStatement(MethodSpec.Builder method, PropertySpec propertySpec) {
            method.addStatement("generator.writeString(value.$L().format($T.ISO_LOCAL_DATE))", propertySpec.name(), DateTimeFormatter.class);
        }

        @Override
        protected void arrayElementStatement(MethodSpec.Builder method) {
            method.addStatement("generator.writeString(element.format($T.ISO_LOCAL_DATE))", DateTimeFormatter.class);
        }
    },
    TIME(LocalTime.class) {
        @Override
        public void singleStatement(MethodSpec.Builder method, PropertySpec propertySpec) {
            method.addStatement("generator.writeString(value.$L().format($T.ISO_LOCAL_TIME))", propertySpec.name(), DateTimeFormatter.class);
        }

        @Override
        protected void arrayElementStatement(MethodSpec.Builder method) {
            method.addStatement("generator.writeString(element.format($T.ISO_LOCAL_TIME))", DateTimeFormatter.class);
        }
    },
    DATE_TIME(LocalDateTime.class) {
        @Override
        public void singleStatement(MethodSpec.Builder method, PropertySpec propertySpec) {
            method.addStatement("generator.writeString(value.$L().format($T.ISO_LOCAL_DATE_TIME))", propertySpec.name(), DateTimeFormatter.class);
        }

        @Override
        protected void arrayElementStatement(MethodSpec.Builder method) {
            method.addStatement("generator.writeString(element.format($T.ISO_LOCAL_DATE_TIME))", DateTimeFormatter.class);
        }
    },
    TZ_DATE_TIME(ZonedDateTime.class) {
        @Override
        public void singleStatement(MethodSpec.Builder method, PropertySpec propertySpec) {
            method.addStatement("generator.writeString(value.$L().format($T.ISO_OFFSET_DATE_TIME))", propertySpec.name(), DateTimeFormatter.class);
        }

        @Override
        protected void arrayElementStatement(MethodSpec.Builder method) {
            method.addStatement("generator.writeString(element.format($T.ISO_OFFSET_DATE_TIME))", DateTimeFormatter.class);
        }
    },

    ENUM() {
        @Override
        public void singleStatement(MethodSpec.Builder method, PropertySpec propertySpec) {
            method.addStatement("generator.writeString(value.$L().name())", propertySpec.name());
        }

        @Override
        protected void arrayElementStatement(MethodSpec.Builder method) {
            method.addStatement("generator.writeString(element.name())");
        }
    },

    NYIMPL() {
        @Override
        public void singleStatement(MethodSpec.Builder method, PropertySpec propertySpec) {
            System.err.println("WARN : property " + propertySpec.name() + " has no simple writer (" + propertySpec.toString() + ")");
            method.addStatement("generator.writeNull()");
        }

        @Override
        public void arrayStatement(MethodSpec.Builder method, PropertySpec propertySpec, ClassName type) {
            System.err.println("WARN : property " + propertySpec.name() + " has no simple writer (" + propertySpec.toString() + ")");
            method.addStatement("generator.writeNull()");
        }
    }

    ;

    private final Set<Class> classes;

    SimplePropertyWriter(Class ... classes) {
        this.classes = classes != null ? new HashSet<>(Arrays.asList(classes)) : Collections.emptySet();
    }

    public abstract void singleStatement(MethodSpec.Builder method, PropertySpec propertySpec);

    public void arrayStatement(MethodSpec.Builder method, PropertySpec propertySpec, ClassName type) {
        method.addStatement("generator.writeStartArray()");
        method.beginControlFlow("for ($T element : value.$L())",
                type,
                propertySpec.name()
        );
        method.beginControlFlow("if(element != null)");
        arrayElementStatement(method);
        method.nextControlFlow("else");
        method.addStatement("generator.writeNull()");
        method.endControlFlow();
        method.endControlFlow();
        method.addStatement("generator.writeEndArray()");
    }

    protected void arrayElementStatement(MethodSpec.Builder method) {}

    static public SimplePropertyWriter forClass(String className) {
        Class clazz;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            System.err.println("no simple writer as not a class : " + className);
            return NYIMPL;
        }
        for (SimplePropertyWriter writer : values()) {
            if (writer.classes.contains(clazz)) {
                return writer;
            }
        }
        System.err.println("no simple writer implementation for : " + className);
        return NYIMPL;
    }
}
