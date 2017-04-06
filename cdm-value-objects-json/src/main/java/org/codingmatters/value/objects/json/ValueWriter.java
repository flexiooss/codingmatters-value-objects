package org.codingmatters.value.objects.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.squareup.javapoet.*;
import org.codingmatters.value.objects.generation.ValueConfiguration;
import org.codingmatters.value.objects.json.property.SimplePropertyWriter;
import org.codingmatters.value.objects.spec.PropertySpec;
import org.codingmatters.value.objects.spec.TypeKind;

import javax.lang.model.element.Modifier;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

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
                .addField(FieldSpec
                        .builder(JsonFactory.class, "factory")
                        .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                        .initializer("new $T()", JsonFactory.class)
                        .build())
                .addMethod(this.buildTopLevelWriteMethod())
                .addMethod(this.buildWriteWithGeneratorMethod())
                .addMethod(this.writeStringArrayMethod())
                .build();
    }

    private MethodSpec buildTopLevelWriteMethod() {
        return MethodSpec.methodBuilder("write")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(this.types.valueType(), "value")
                .returns(String.class)
                .addException(ClassName.get(IOException.class))
                .beginControlFlow("try($T out = new $T())", OutputStream.class, ByteArrayOutputStream.class)
                    .addStatement("$T generator = this.factory.createGenerator(out)", JsonGenerator.class)
                    .addStatement("this.write(generator, value)")
                    .addStatement("generator.close()")
                    .addStatement("return out.toString()")
                .endControlFlow()
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

    private void writePropertyStatements(MethodSpec.Builder method, PropertySpec propertySpec) {
        method.addStatement("generator.writeFieldName($S)", propertySpec.name());

        method.beginControlFlow("if(value.$L() != null)", propertySpec.name());
        if(propertySpec.typeSpec().typeKind() == TypeKind.JAVA_TYPE) {
            this.writeSimpleProperty(method, propertySpec);
        } else if(propertySpec.typeSpec().typeKind().isValueObject()) {
            this.valueObjectWriteStatements(method, propertySpec);
        } else {
            System.out.println(propertySpec.typeSpec().typeKind() + " : " + propertySpec.typeSpec().typeRef());
            method.addStatement("generator.writeNull()");
        }
        method.nextControlFlow("else")
                .addStatement("generator.writeNull()")
                .endControlFlow();
    }

    private void writeSimpleProperty(MethodSpec.Builder method, PropertySpec propertySpec) {
        if (!propertySpec.typeSpec().cardinality().isCollection()) {
            SimplePropertyWriter.forClass(propertySpec.typeSpec().typeRef()).singleStatement(method, propertySpec);
        } else {
            SimplePropertyWriter.forClass(propertySpec.typeSpec().typeRef()).arrayStatement(method, propertySpec);
        }
    }

    private void valueObjectWriteStatements(MethodSpec.Builder method, PropertySpec propertySpec) {
        int lastDot = propertySpec.typeSpec().typeRef().lastIndexOf(".");
        ClassName propertyWriter = ClassName.get(
                propertySpec.typeSpec().typeRef().substring(0, lastDot) + ".json",
                propertySpec.typeSpec().typeRef().substring(lastDot + 1) + "Writer"
        );
        if (!propertySpec.typeSpec().cardinality().isCollection()) {
            method.addStatement("new $T().write(generator, value.$L())", propertyWriter, propertySpec.name());
        } else {
            ClassName propertyType = ClassName.get(
                    propertySpec.typeSpec().typeRef().substring(0, propertySpec.typeSpec().typeRef().lastIndexOf(".")),
                    propertySpec.typeSpec().typeRef().substring(propertySpec.typeSpec().typeRef().lastIndexOf(".") + 1)
            );
            method.addStatement("generator.writeStartArray()");
            method.beginControlFlow("for ($T element : value.$L())", propertyType, propertySpec.name())
                    .addStatement("new $T().write(generator, element)", propertyWriter)
                    .endControlFlow();
            method.addStatement("generator.writeEndArray()");
        }
    }

    private MethodSpec writeStringArrayMethod() {
        MethodSpec.Builder method = MethodSpec.methodBuilder("writeStringArray")
                .addModifiers(Modifier.PRIVATE)
                .addParameter(JsonGenerator.class, "generator")
                .addParameter(this.types.collectionConfiguration().valueListOfType(ClassName.get(String.class)), "elements");
        method
                .beginControlFlow("if(elements != null)")
                    .addStatement("generator.writeStartArray()")
                    .beginControlFlow("for($T elmt : elements)", String.class)
                        .addStatement("generator.writeString(elmt)")
                    .endControlFlow()
                    .addStatement("generator.writeEndArray()")
                .nextControlFlow("else")
                    .addStatement("generator.writeNull()")
                .endControlFlow();

        return method
                .addException(IOException.class)
                .returns(TypeName.VOID)
                .build();
    }

}
