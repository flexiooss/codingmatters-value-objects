package org.codingmatters.value.objects.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.squareup.javapoet.*;
import org.codingmatters.value.objects.generation.ValueConfiguration;
import org.codingmatters.value.objects.json.property.SimplePropertyWriter;
import org.codingmatters.value.objects.spec.PropertySpec;
import org.codingmatters.value.objects.spec.TypeKind;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;

/**
 * Created by nelt on 3/30/17.
 */
public class ValueWriter {
    private final ValueConfiguration types;
    private final List<PropertySpec> propertySpecs;

    public ValueWriter(ValueConfiguration types, List<PropertySpec> propertySpecs) {
        this.types = types;
        this.propertySpecs = propertySpecs;
    }

    public TypeSpec type() {
        return TypeSpec.classBuilder(this.types.valueType().simpleName() + "Writer")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(this.buildWriteWithGeneratorMethod())
                .addMethod(this.buildWriteArrayWithGeneratorMethod())
                .build();
    }

    private MethodSpec buildWriteWithGeneratorMethod() {
        MethodSpec.Builder method = MethodSpec.methodBuilder("write")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(JsonGenerator.class, "generator")
                .addParameter(this.types.valueType(), "value")
                .addException(ClassName.get(IOException.class));

        method.addStatement("generator.writeStartObject()");
        for (PropertySpec propertySpec : this.propertySpecs) {
            this.writePropertyStatements(method, propertySpec);
        }
        method.addStatement("generator.writeEndObject()");

        method.returns(TypeName.VOID);
        return method.build();
    }

    private MethodSpec buildWriteArrayWithGeneratorMethod() {
        MethodSpec.Builder method = MethodSpec.methodBuilder("writeArray")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(JsonGenerator.class, "generator")
                .addParameter(ArrayTypeName.of(this.types.valueType()), "values")
                .addException(ClassName.get(IOException.class));

        method.beginControlFlow("if(values == null)")
                    .addStatement("generator.writeNull()")
                .nextControlFlow("else")
                    .addStatement("generator.writeStartArray()")
                    .beginControlFlow("for($T value : values)", this.types.valueType())
                        .addStatement("this.write(generator, value)")
                    .endControlFlow()
                    .addStatement("generator.writeEndArray()")
                .endControlFlow();

        method.returns(TypeName.VOID);
        return method.build();
    }

    private void writePropertyStatements(MethodSpec.Builder method, PropertySpec propertySpec) {
        method.addStatement("generator.writeFieldName($S)", this.fieldName(propertySpec));

        method.beginControlFlow("if(value.$L() != null)", propertySpec.name());
        if(propertySpec.typeSpec().typeKind() == TypeKind.JAVA_TYPE) {
            this.writeSimpleProperty(method, propertySpec);
        } else if(propertySpec.typeSpec().typeKind() == TypeKind.ENUM) {
            this.writeEnumValue(method, propertySpec);
        } else if(propertySpec.typeSpec().typeKind().isValueObject()) {
            this.externalValueObjectWriteStatements(method, propertySpec);
        } else {
            System.out.println(propertySpec.typeSpec().typeKind() + " : " + propertySpec.typeSpec().typeRef());
            method.addStatement("generator.writeNull()");
        }
        method.nextControlFlow("else")
                .addStatement("generator.writeNull()")
                .endControlFlow();
    }

    private String fieldName(PropertySpec propertySpec) {
        Optional<Matcher> hint = propertySpec.matchingHint("property:raw\\(([^)]*)\\)");
        if(hint.isPresent()) {
            return hint.get().group(1);
        } else {
            return propertySpec.name();
        }
    }

    private void writeSimpleProperty(MethodSpec.Builder method, PropertySpec propertySpec) {
        if (!propertySpec.typeSpec().cardinality().isCollection()) {
            SimplePropertyWriter.forClass(propertySpec.typeSpec().typeRef()).singleStatement(method, propertySpec);
        } else {
            ClassName type = this.types.propertyClass(propertySpec);
            SimplePropertyWriter.forClass(propertySpec.typeSpec().typeRef()).arrayStatement(method, propertySpec, type);
        }
    }

    private void writeEnumValue(MethodSpec.Builder method, PropertySpec propertySpec) {
        if (!propertySpec.typeSpec().cardinality().isCollection()) {
            SimplePropertyWriter.ENUM.singleStatement(method, propertySpec);
        } else {
            SimplePropertyWriter.ENUM.arrayStatement(method, propertySpec, this.types.propertyClass(propertySpec));
        }
    }

    private void externalValueObjectWriteStatements(MethodSpec.Builder method, PropertySpec propertySpec) {
        ClassName propertyClass = this.types.valueObjectSingleType(propertySpec);
        ClassName propertyWriter = ClassName.get(
                propertyClass.packageName() + ".json",
                propertyClass.simpleName() + "Writer"
        );
        if (!propertySpec.typeSpec().cardinality().isCollection()) {
            method.addStatement("new $T().write(generator, value.$L())", propertyWriter, propertySpec.name());
        } else {
            method.addStatement("generator.writeStartArray()");
            method.beginControlFlow("for ($T element : value.$L())", propertyClass, propertySpec.name())
                    .beginControlFlow("if(element != null)")
                        .addStatement("new $T().write(generator, element)", propertyWriter)
                    .nextControlFlow("else")
                        .addStatement("generator.writeNull()")
                    .endControlFlow()
                    .endControlFlow();
            method.addStatement("generator.writeEndArray()");
        }
    }

}
