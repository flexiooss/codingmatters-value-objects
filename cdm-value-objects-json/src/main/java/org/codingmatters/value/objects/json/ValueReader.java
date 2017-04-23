package org.codingmatters.value.objects.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.squareup.javapoet.*;
import org.codingmatters.value.objects.generation.ValueConfiguration;
import org.codingmatters.value.objects.json.property.SimplePropertyReader;
import org.codingmatters.value.objects.spec.PropertySpec;
import org.codingmatters.value.objects.spec.TypeKind;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.*;

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
        TypeSpec.Builder result = TypeSpec.classBuilder(this.types.valueType().simpleName() + "Reader")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(this.readWithParserMethod())
                .addType(this.readerFunctionalInterface())
                .addMethod(this.readValueMethod())
                .addMethod(this.readListValueMethod());

        for (PropertySpec propertySpec : this.propertySpecs) {
            if(propertySpec.typeSpec().typeKind() == TypeKind.JAVA_TYPE) {
                SimplePropertyReader propertyReader = SimplePropertyReader.forClassName(propertySpec.typeSpec().typeRef());
                if (propertyReader != null) {
                    if (!propertySpec.typeSpec().cardinality().isCollection()) {
                        String initializerFormat = "";
                        List<Object> initializerArgs = new LinkedList<>();

                        initializerFormat += "new $T($T.asList(";
                        initializerArgs.add(HashSet.class);
                        initializerArgs.add(Arrays.class);
                        boolean first = true;
                        for (JsonToken jsonToken : propertyReader.expectedTokens()) {
                            if(! first) {
                                initializerFormat += ", ";
                            }
                            first = false;

                            initializerFormat += "$T.$L";
                            initializerArgs.add(JsonToken.class);
                            initializerArgs.add(jsonToken.name());
                        }
                        initializerFormat += "))";
                        result.addField(
                                FieldSpec
                                        .builder(
                                                ClassName.get(Set.class),
                                                this.expectedTokenField(propertySpec),
                                                Modifier.PRIVATE, Modifier.STATIC)
                                        .initializer(initializerFormat, initializerArgs.toArray())
                                        .build()
                        );
                    }
                }
            }
        }


        return result.build();
    }

    private String expectedTokenField(PropertySpec propertySpec) {
        return propertySpec.name().toUpperCase() + "_EXPECTEDTOKENS";
    }

    private MethodSpec readWithParserMethod() {
        MethodSpec.Builder method = MethodSpec.methodBuilder("read")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(JsonParser.class, "parser")
                .returns(this.types.valueType())
                .addException(IOException.class);
        /*
        if(parser.getCurrentToken() == null) {
                parser.nextToken();
        }
         */
        method
                .beginControlFlow("if(parser.getCurrentToken() == null)")
                .addStatement("parser.nextToken()")
                .endControlFlow();

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
                System.err.println("NYIMPL type ref for simple property: " + propertySpec.typeSpec().typeRef());
            }
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
                Set<JsonToken> expectedTokens = PROP_EXPECTEDTOKENS;
         */
        method.beginControlFlow("case $S:", propertySpec.name())
                .addStatement("$T<$T> expectedTokens = $L", Set.class, JsonToken.class, this.expectedTokenField(propertySpec));

        propertyReader.addSingleStatement(method, propertySpec);

        /*
                break;
         */
        method
                .addStatement("break")
                .endControlFlow();
    }

    private void multipleSimplePropertyStatement(MethodSpec.Builder method, PropertySpec propertySpec, SimplePropertyReader propertyReader) {
        /*
        case "listProp":
         */
        method.beginControlFlow("case $S:", propertySpec.name());
        propertyReader.addMultipleStatement(method, propertySpec);
        /*
            break;
         */
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