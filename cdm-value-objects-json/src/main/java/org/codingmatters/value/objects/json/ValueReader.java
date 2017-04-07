package org.codingmatters.value.objects.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.squareup.javapoet.*;
import org.codingmatters.value.objects.generation.ValueConfiguration;
import org.codingmatters.value.objects.spec.PropertySpec;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.LinkedList;
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
                .addMethod(this.topLevelReadMethod())
                .addMethod(this.readWithParserMethod())
                .addType(this.readerFunctionalInterface())
                .addMethod(this.readValueMethod())
                .addMethod(this.readListValueMethod())
                .build();
    }

    private MethodSpec topLevelReadMethod() {
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

    private MethodSpec readWithParserMethod() {
        return MethodSpec.methodBuilder("read")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(JsonParser.class, "parser")
                .returns(this.types.valueType())
                .addException(IOException.class)
                .addStatement("return null")
                .build();
    }

    private TypeSpec readerFunctionalInterface() {
        /*
        @FunctionalInterface
        private interface Reader<T> {
            T read(JsonParser parser) throws IOException;
        }
         */
        return TypeSpec.interfaceBuilder("Reader")
                .addModifiers(Modifier.PRIVATE)
                .addAnnotation(FunctionalInterface.class)
                .addTypeVariable(TypeVariableName.get("T"))
                .addMethod(MethodSpec.methodBuilder("read")
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .addParameter(JsonParser.class, "parser")
                        .addException(IOException.class)
                        .returns(TypeVariableName.get("T"))
                        .build())
                .build();
    }

    private MethodSpec readValueMethod() {
        /*
        private <T> T readValue(JsonParser parser, JsonToken expectedToken, Reader<T> reader, String propertyName) throws IOException
         */
        return MethodSpec.methodBuilder("readValue")
                .addModifiers(Modifier.PRIVATE)
                .addTypeVariable(TypeVariableName.get("T"))
                .addParameter(JsonParser.class, "parser")
                .addParameter(JsonToken.class, "expectedToken")
                .addParameter(
                        ParameterizedTypeName.get(
                                ClassName.bestGuess("Reader"),
                                TypeVariableName.get("T")
                        ),
                        "reader"
                )
                .addParameter(String.class, "propertyName")
                .returns(TypeVariableName.get("T"))
                .addException(IOException.class)
                .addStatement("parser.nextToken()")
                .addStatement("if (parser.currentToken() == $T.VALUE_NULL) return null", JsonToken.class)
                .addStatement("if (parser.currentToken() == expectedToken) return reader.read(parser)")
                .addStatement("" +
                        "throw new $T(\n" +
                        "    $T.format(\"reading property %s, was expecting %s, but was %s\",\n" +
                        "        propertyName, expectedToken, parser.currentToken()\n" +
                        "    )\n" +
                        ")"
                        , IOException.class, String.class
                )
                .build();
    }

    private MethodSpec readListValueMethod() {
        //private <T> List<T> readListValue(JsonParser parser, Reader<T> reader, String propertyName) throws IOException
        return MethodSpec.methodBuilder("readListValue")
                .addModifiers(Modifier.PRIVATE)
                .addTypeVariable(TypeVariableName.get("T"))
                .addParameter(JsonParser.class, "parser")
                .addParameter(
                        ParameterizedTypeName.get(
                                ClassName.bestGuess("Reader"),
                                TypeVariableName.get("T")
                        ),
                        "reader"
                )
                .addParameter(String.class, "propertyName")
                .returns(ParameterizedTypeName.get(ClassName.get(List.class), TypeVariableName.get("T")))
                .addException(IOException.class)
                .addStatement("parser.nextToken();")
                .addStatement("if (parser.currentToken() == $T.VALUE_NULL) return null;", JsonToken.class)
                .beginControlFlow("if (parser.currentToken() == $T.START_ARRAY)", JsonToken.class)
                    .addStatement("$T<T> listValue = new $T<>()", LinkedList.class, LinkedList.class)
                    .beginControlFlow("while (parser.nextToken() != $T.END_ARRAY)", JsonToken.class)
                        .addStatement("listValue.add(reader.read(parser))")
                    .endControlFlow()
                    .addStatement("return listValue")
                .endControlFlow()
                .addStatement("" +
                        "throw new $T(\n" +
                        "        $T.format(\"reading property %s, was expecting %s, but was %s\",\n" +
                        "                propertyName, $T.START_ARRAY, parser.currentToken()\n" +
                        "        )\n" +
                        ")",
                        IOException.class, String.class, JsonToken.class
                )
                .build();
    }
}
