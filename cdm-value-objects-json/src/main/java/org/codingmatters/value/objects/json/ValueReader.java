package org.codingmatters.value.objects.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.squareup.javapoet.*;
import org.codingmatters.value.objects.generation.ValueConfiguration;
import org.codingmatters.value.objects.json.property.SimplePropertyReader;
import org.codingmatters.value.objects.spec.PropertySpec;
import org.codingmatters.value.objects.spec.PropertyTypeSpec;
import org.codingmatters.value.objects.spec.TypeKind;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.Temporal;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
            parser.nextToken();
            return this.read(parser);
        }
         */
        return MethodSpec.methodBuilder("read")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(String.class, "json")
                .returns(this.types.valueType())
                .addException(IOException.class)
                .beginControlFlow("try($T parser = this.factory.createParser(json.getBytes()))", JsonParser.class)
                    .addStatement("parser.nextToken()")
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
        if(parser.currentToken() == JsonToken.VALUE_NULL) return null;
        */
        method.addStatement("if(parser.currentToken() == $T.VALUE_NULL) return null", JsonToken.class);
        /*
        if (parser.currentToken() != JsonToken.START_OBJECT) {
            throw new IOException(
                    String.format("reading a %s object, was expecting %s, but was %s",
                            ExampleValue.class.getName(), JsonToken.START_OBJECT, parser.currentToken()
                    )
            );
        }
        */
        method.beginControlFlow("if(parser.currentToken() != $T.START_OBJECT)", JsonToken.class)
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
            SimplePropertyReader propertyReader = SimplePropertyReader.forClassName(propertySpec.typeSpec().typeRef());
            if(propertyReader != null) {
                if (!propertySpec.typeSpec().cardinality().isCollection()) {
                    this.singleSimplePropertyStatement(method, propertySpec, propertyReader);
                } else {
                    this.multipleSimplePropertyStatement(method, propertySpec, propertyReader);
                }
            } else {
                System.err.println("NYIMPL type ref for simple propert: " + propertySpec.typeSpec().typeRef());
            }
//            String parserMethod = this.parserMethodForType(propertySpec);
//            if(parserMethod != null) {
//                if (!propertySpec.typeSpec().cardinality().isCollection()) {
//                    this.singleSimplePropertyStatement(method, propertySpec, parserMethod);
//                } else {
//                    this.multipleSimplePropertyStatement(method, propertySpec, parserMethod);
//                }
//            }
        } else if(propertySpec.typeSpec().typeKind().isValueObject()) {
            if(! propertySpec.typeSpec().cardinality().isCollection()) {
                this.singleComplexPropertyStatement(method, propertySpec);
            } else {
                ClassName propertyClass = this.types.propertySingleType(propertySpec);
                ClassName propertyReader = ClassName.get(
                        propertyClass.packageName() + ".json",
                        propertyClass.simpleName() + "Reader"
                );
                /*
                    case "complexList":
                        ComplexListReader reader = new ComplexListReader();
                        builder.complexList(this.readListValue(parser, jsonParser -> reader.read(jsonParser), "complexList"));
                        break;
                 */
                method.beginControlFlow("case $S:", propertySpec.name())
                        .addStatement("$T reader = new $T()", propertyReader, propertyReader)
                        .addStatement("builder.$L(this.readListValue(parser, jsonParser -> reader.read(jsonParser), $S))",
                                propertySpec.name(), propertySpec.name())
                        .addStatement("break")
                        .endControlFlow();
            }
        }
    }

    private void singleComplexPropertyStatement(MethodSpec.Builder method, PropertySpec propertySpec) {
        ClassName propertyClass = this.types.propertySingleType(propertySpec);
        ClassName propertyReader = ClassName.get(
                propertyClass.packageName() + ".json",
                propertyClass.simpleName() + "Reader"
        );
            /*
                case "complex":
                    parser.nextToken();
                    builder.complex(new ComplexReader().read(parser));
                    break;
             */
        if(! propertySpec.typeSpec().cardinality().isCollection()) {
            method.beginControlFlow("case $S:", propertySpec.name())
                    .addStatement("parser.nextToken()")
                    .addStatement("builder.$L(new $T().read(parser))", propertySpec.name(), propertyReader)
                    .addStatement("break")
                    .endControlFlow();
        }
    }

    private void singleSimplePropertyStatement(MethodSpec.Builder method, PropertySpec propertySpec, SimplePropertyReader propertyReader) {
        /*
        case "prop":
                Set<JsonToken> expectedTokens = new HashSet();
                expectedTokens.add(JsonToken.VALUE_STRING);
                builder.prop(this.readValue(parser, jsonParser -> jsonParser.getText(), "prop", expectedTokens));
                break;
         */
        method.beginControlFlow("case $S:", propertySpec.name())
                .addStatement("$T<$T> expectedTokens = new $T<>()", Set.class, JsonToken.class, HashSet.class);
        for (JsonToken jsonToken : propertyReader.expectedTokens()) {
            method.addStatement("expectedTokens.add($T.$L)", JsonToken.class, jsonToken.name());
        }

        if(this.isTemporal(propertySpec.typeSpec())) {
            method
                    .addStatement("builder.$L(this.readValue(parser, jsonParser -> $T.parse(jsonParser.$L()), $S, expectedTokens))",
                            propertySpec.name(), LocalDate.class, propertyReader.parserMethod(), propertySpec.name()
                    );
        } else {
            method
                    .addStatement("builder.$L(this.readValue(parser, jsonParser -> jsonParser.$L(), $S, expectedTokens))",
                            propertySpec.name(), propertyReader.parserMethod(), propertySpec.name()
                    );
        }
        method
                .addStatement("break")
                .endControlFlow();
    }

    private boolean isTemporal(PropertyTypeSpec typeSpec) {
        try {
            return Temporal.class.isAssignableFrom(Class.forName(typeSpec.typeRef()));
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private void multipleSimplePropertyStatement(MethodSpec.Builder method, PropertySpec propertySpec, SimplePropertyReader propertyReader) {
        /*
        case "listProp":
            builder.listProp(this.readListValue(parser, jsonParser -> jsonParser.getText(), "listProp"));
            break;
         */
        method.beginControlFlow("case $S:", propertySpec.name());
        if(this.isTemporal(propertySpec.typeSpec())) {
            method
                    .addStatement("builder.$L(this.readListValue(parser, jsonParser -> $T.parse(jsonParser.$L()), $S))",
                            propertySpec.name(), LocalDate.class, propertyReader.parserMethod(), propertySpec.name());
        } else {
            method
                    .addStatement("builder.$L(this.readListValue(parser, jsonParser -> jsonParser.$L(), $S))",
                            propertySpec.name(), propertyReader.parserMethod(), propertySpec.name());
        }
        method
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
        private <T> T readValue(JsonParser parser, Reader<T> reader, String propertyName, Set<JsonToken> expectedTokens) throws IOException
         */
        return MethodSpec.methodBuilder("readValue")
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
                .addParameter(
                        ParameterizedTypeName.get(ClassName.get(Set.class), ClassName.get(JsonToken.class)),
                        "expectedTokens")
                .returns(TypeVariableName.get("T"))
                .addException(IOException.class)
                .addStatement("parser.nextToken()")
                .addStatement("if (parser.currentToken() == $T.VALUE_NULL) return null", JsonToken.class)
                .addStatement("if (expectedTokens.contains(parser.currentToken())) return reader.read(parser)")
                .addStatement("" +
                        "throw new $T(\n" +
                        "    $T.format(\"reading property %s, was expecting %s, but was %s\",\n" +
                        "        propertyName, expectedTokens, parser.currentToken()\n" +
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
                .addStatement("parser.nextToken()")
                .addStatement("if (parser.currentToken() == $T.VALUE_NULL) return null", JsonToken.class)
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
