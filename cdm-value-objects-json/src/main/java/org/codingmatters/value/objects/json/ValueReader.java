package org.codingmatters.value.objects.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.codingmatters.value.objects.generation.ValueConfiguration;
import org.codingmatters.value.objects.spec.PropertySpec;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.List;

/**
 * Created by nelt on 4/6/17.
 */
public class ValueReader {
    private final ValueConfiguration types;
    private final List<PropertySpec> propertySpecs;

    public ValueReader(ValueConfiguration types, List<PropertySpec> propertySpecs) {
        this.types = types;
        this.propertySpecs = propertySpecs;
    }

    public TypeSpec type() {
        return TypeSpec.classBuilder(this.types.valueType().simpleName() + "Reader")
                .addModifiers(Modifier.PUBLIC)
                .addField(FieldSpec
                        .builder(JsonFactory.class, "factory")
                        .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                        .initializer("new $T()", JsonFactory.class)
                        .build())
                .addMethod(this.buildTopLevelReadMethod())
                .addMethod(this.buildReadWithParserMethod())
                .build();
    }

    private MethodSpec buildTopLevelReadMethod() {
        /*
        try (JsonParser parser = this.factory.createParser(json.getBytes())) {
            return this.read(parser);
        }
         */
        return MethodSpec.methodBuilder("read")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(String.class, "json")
                .returns(this.types.valueType())
                .addException(IOException.class)
                .beginControlFlow("try ($T parser = this.factory.createParser(json.getBytes()))", JsonParser.class)
                .addStatement("return this.read(parser)")
                .endControlFlow()
                .build();
    }

    private MethodSpec buildReadWithParserMethod() {
        return MethodSpec.methodBuilder("read")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(JsonParser.class, "parser")
                .returns(this.types.valueType())
                .addException(IOException.class)
                .addStatement("return null")
                .build();
    }
}
