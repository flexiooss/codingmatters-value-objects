package org.codingmatters.value.objects.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.squareup.javapoet.*;
import org.codingmatters.value.objects.generation.ValueConfiguration;
import org.codingmatters.value.objects.spec.PropertySpec;
import org.codingmatters.value.objects.spec.TypeKind;

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
        MethodSpec.Builder method = MethodSpec.methodBuilder("read")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(JsonParser.class, "parser")
                .returns(this.types.valueType())
                .addException(IOException.class);

        /*
        JsonToken firstToken = parser.nextToken();
        if(firstToken == JsonToken.VALUE_NULL) return null;
        */
        method.addStatement("$T firstToken = parser.nextToken()", JsonToken.class);
        method.addStatement("if(firstToken == $T.VALUE_NULL) return null", JsonToken.class);
        /*
        if (firstToken != JsonToken.START_OBJECT) {
            throw new IOException(
                    String.format("reading a %s object, was expecting %s, but was %s",
                            ExampleValue.class.getName(), JsonToken.START_OBJECT, parser.currentToken()
                    )
            );
        }
        */
        method.beginControlFlow("if (firstToken != $T.START_OBJECT)", JsonToken.class)
                .addStatement("" +
                        "throw new IOException(\n" +
                        "        String.format(\"reading a %s object, was expecting %s, but was %s\",\n" +
                        "                $T.class.getName(), $T.START_OBJECT, parser.currentToken()\n" +
                        "        )\n" +
                        ")", this.types.valueType(), JsonToken.class)
                .endControlFlow();
        /*
        ExampleValue.Builder builder = ExampleValue.Builder.builder();
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = parser.getCurrentName();
            switch (fieldName) {
         */
        method.addStatement("$T builder = $T.builder()", this.types.valueBuilderType(), this.types.valueBuilderType());
        method.beginControlFlow("while (parser.nextToken() != $T.END_OBJECT)", JsonToken.class)
                .addStatement("$T fieldName = parser.getCurrentName()", String.class)
                .beginControlFlow("switch (fieldName)");
        for (PropertySpec propertySpec : this.propertySpecs) {
            this.propertyStatements(method, propertySpec);
        }
        /*
            }
        }
         */
        method.endControlFlow()
                .endControlFlow();

        /*
        return builder.build();
         */
        method.addStatement("return builder.build()");

        return method.build();
    }

    private void propertyStatements(MethodSpec.Builder method, PropertySpec propertySpec) {
        if(propertySpec.typeSpec().typeKind() == TypeKind.JAVA_TYPE) {
            if(propertySpec.typeSpec().typeRef().equals(String.class.getName())) {
                if(! propertySpec.typeSpec().cardinality().isCollection()) {
                    this.singleSimplePropertyStatement(method, propertySpec, "getText");
                } else {
                    this.multipleSimplePropertyStatement(method, propertySpec, "getText");

                }
            }
        }
    }

    private void multipleSimplePropertyStatement(MethodSpec.Builder method, PropertySpec propertySpec, String parserMethod) {
    /*
    case "listProp":
        builder.listProp(this.readListValue(parser, jsonParser -> jsonParser.getText(), "listProp"));
        break;
     */
        method.beginControlFlow("case $S:", propertySpec.name())
                .addStatement("builder.$L(this.readListValue(parser, jsonParser -> jsonParser.$L(), $S))",
                        propertySpec.name(), parserMethod, propertySpec.name())
                .addStatement("break")
                .endControlFlow();
    }

    private void singleSimplePropertyStatement(MethodSpec.Builder method, PropertySpec propertySpec, String parserMethod) {
    /*
    case "prop":
            builder.prop(this.readValue(parser, JsonToken.VALUE_STRING, jsonParser -> jsonParser.getText(), "prop"));
            break;
     */
        method.beginControlFlow("case $S:", propertySpec.name())
                .addStatement("builder.$L(this.readValue(parser, $T.VALUE_STRING, jsonParser -> jsonParser.$L(), $S))",
                        propertySpec.name(), JsonToken.class, parserMethod, propertySpec.name())
                .addStatement("break")
                .endControlFlow();
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
