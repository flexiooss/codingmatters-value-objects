package org.codingmatters.value.objects.json.property;

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
        public void arrayStatement(MethodSpec.Builder method, PropertySpec propertySpec) {
            method.addStatement("this.writeStringArray(generator, value.$L())", propertySpec.name());
        }
    },
    NUMBER(Integer.class, Long.class, Float.class, Double.class) {
        @Override
        public void singleStatement(MethodSpec.Builder method, PropertySpec propertySpec) {
            method.addStatement("generator.writeNumber(value.$L())", propertySpec.name());
        }

        @Override
        public void arrayStatement(MethodSpec.Builder method, PropertySpec propertySpec) {
            method.addStatement("generator.writeNull()");
        }
    },
    BOOLEAN(Boolean.class) {
        @Override
        public void singleStatement(MethodSpec.Builder method, PropertySpec propertySpec) {
            method.addStatement("generator.writeBoolean(value.$L())", propertySpec.name());
        }

        @Override
        public void arrayStatement(MethodSpec.Builder method, PropertySpec propertySpec) {
            method.addStatement("generator.writeNull()");
        }
    },
    DATE(LocalDate.class) {
        @Override
        public void singleStatement(MethodSpec.Builder method, PropertySpec propertySpec) {
            method.addStatement("generator.writeString(value.$L().format($T.ISO_LOCAL_DATE))", propertySpec.name(), DateTimeFormatter.class);
        }

        @Override
        public void arrayStatement(MethodSpec.Builder method, PropertySpec propertySpec) {
            method.addStatement("generator.writeNull()");
        }
    },
    TIME(LocalTime.class) {
        @Override
        public void singleStatement(MethodSpec.Builder method, PropertySpec propertySpec) {
            method.addStatement("generator.writeString(value.$L().format($T.ISO_LOCAL_TIME))", propertySpec.name(), DateTimeFormatter.class);
        }

        @Override
        public void arrayStatement(MethodSpec.Builder method, PropertySpec propertySpec) {
            method.addStatement("generator.writeNull()");
        }
    },
    DATE_TIME(LocalDateTime.class) {
        @Override
        public void singleStatement(MethodSpec.Builder method, PropertySpec propertySpec) {
            method.addStatement("generator.writeString(value.$L().format($T.ISO_LOCAL_DATE_TIME))", propertySpec.name(), DateTimeFormatter.class);
        }

        @Override
        public void arrayStatement(MethodSpec.Builder method, PropertySpec propertySpec) {
            method.addStatement("generator.writeNull()");
        }
    },
    TZ_DATE_TIME(ZonedDateTime.class) {
        @Override
        public void singleStatement(MethodSpec.Builder method, PropertySpec propertySpec) {
            method.addStatement("generator.writeString(value.$L().format($T.ISO_OFFSET_DATE_TIME))", propertySpec.name(), DateTimeFormatter.class);
        }

        @Override
        public void arrayStatement(MethodSpec.Builder method, PropertySpec propertySpec) {
            method.addStatement("generator.writeNull()");
        }
    },

    NYIMPL() {
        @Override
        public void singleStatement(MethodSpec.Builder method, PropertySpec propertySpec) {
            method.addStatement("generator.writeNull()");
        }

        @Override
        public void arrayStatement(MethodSpec.Builder method, PropertySpec propertySpec) {
            method.addStatement("generator.writeNull()");
        }
    };

    private final Set<Class> classes;

    SimplePropertyWriter(Class ... classes) {
        this.classes = classes != null ? new HashSet<>(Arrays.asList(classes)) : Collections.emptySet();
    }

    public abstract void singleStatement(MethodSpec.Builder method, PropertySpec propertySpec);
    public abstract void arrayStatement(MethodSpec.Builder method, PropertySpec propertySpec);

    static public SimplePropertyWriter forClass(String className) {
        Class clazz = null;
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
