package org.codingmatters.value.objects.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.squareup.javapoet.*;
import org.codingmatters.value.objects.generation.ValueConfiguration;
import org.codingmatters.value.objects.json.property.SimplePropertyReaderProducer;
import org.codingmatters.value.objects.json.property.SimplePropertyReaders;
import org.codingmatters.value.objects.json.property.statement.EnumPropertyStatement;
import org.codingmatters.value.objects.spec.PropertySpec;
import org.codingmatters.value.objects.spec.TypeKind;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

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
                .addMethod(this.readArrayWithParserMethod())
                .addType(this.readerFunctionalInterface())
                .addMethod(this.readValueMethod())
                .addMethod(this.readListValueMethod())
                .addMethod(this.consumeUnexpectedProperty())
                .addType(this.tokensEnum())
                ;

        for (PropertySpec propertySpec : this.propertySpecs) {
            SimplePropertyReaderProducer propertyReaderProducer = this.propertyReaderProducer(propertySpec);
            if(propertyReaderProducer != null) {
                this.addPropertyReaderStatements(result, propertySpec, propertyReaderProducer);
            }
        }


        return result.build();
    }

    private TypeSpec tokensEnum() {
        TypeSpec.Builder result = TypeSpec.enumBuilder("Token");
        result
                .addField(FieldSpec.builder(String.class, "name", Modifier.PRIVATE, Modifier.FINAL)
                        .build()
                )
                .addField(FieldSpec.builder(String.class, "rawName", Modifier.PRIVATE, Modifier.FINAL)
                        .build()
                )
                .addMethod(MethodSpec.constructorBuilder()
                        .addParameter(String.class, "name")
                        .addParameter(String.class, "rawName")
                        .addStatement("this.name = name")
                        .addStatement("this.rawName = rawName")
                        .build())
                .addEnumConstant(
                        "__UNKNOWN__",
                        TypeSpec.anonymousClassBuilder("$S, $S",
                                "__UNKNOWN__",
                                "__UNKNOWN__"
                        ).build()
                )
        ;
        result.addMethod(this.enumNormalizeFieldName());

        for (PropertySpec propertySpec : this.propertySpecs) {
            result.addEnumConstant(
                    this.enumConstant(propertySpec),
                    TypeSpec.anonymousClassBuilder("$S, $S",
                            propertySpec.name(),
                            this.rawName(propertySpec)
                    ).build()
            );
        }

        result.addMethod(MethodSpec.methodBuilder("from")
                .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                .addParameter(String.class, "str")
                .returns(ClassName.bestGuess("Token"))
                .beginControlFlow("for(Token token : Token.values())")
                    .beginControlFlow("if(token.name.equals(str))")
                        .addStatement("return token")
                    .nextControlFlow("else if(token.rawName.equals(str))")
                        .addStatement("return token")
                    .nextControlFlow("else if(token.name.equals(normalizeFieldName(str)))")
                        .addStatement("return token")
                    .nextControlFlow("else if(token.rawName.equals(normalizeFieldName(str)))")
                        .addStatement("return token")
                    .endControlFlow()
                .endControlFlow()
                .addStatement("return __UNKNOWN__")
                .build());

        return result.build();
    }

    private String rawName(PropertySpec propertySpec) {
        Optional<Matcher> hint = propertySpec.matchingHint("property:raw\\(([^)]*)\\)");
        if(hint.isPresent()) {
            return hint.get().group(1);
        } else {
            return propertySpec.name();
        }
    }

    private String enumConstant(PropertySpec propertySpec) {
        return propertySpec.name().toUpperCase();
    }

    private MethodSpec enumNormalizeFieldName() {
        MethodSpec.Builder result = MethodSpec.methodBuilder("normalizeFieldName")
                .addModifiers(Modifier.STATIC, Modifier.PRIVATE)
                .addParameter(String.class, "fieldName")
                .returns(String.class)
                ;
        result.addStatement("if(fieldName == null) return null");
        result.addStatement("if(fieldName.trim().equals(\"\")) return \"\"");
        result.addStatement("fieldName = $T.stream(fieldName.split($S)).map(s -> s.substring(0, 1).toUpperCase() + s.substring(1)).collect($T.joining())",
                Arrays.class,
                "(\\s|-)+",
                Collectors.class
        );
        result.addStatement("fieldName =  fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1)");
        result.addStatement("return fieldName");

        return result.build();
    }


    private MethodSpec consumeUnexpectedProperty() {
        MethodSpec.Builder result = MethodSpec.methodBuilder("consumeUnexpectedProperty")
                .addModifiers(Modifier.PRIVATE)
                .addParameter(ClassName.get(JsonParser.class), "parser")
                .returns(TypeName.VOID)
                .addException(ClassName.get(IOException.class))
                ;
        /*
        parser.nextToken();
        if(parser.currentToken().isStructStart()) {
          int level = 1;
          do {
            parser.nextToken();
            if (parser.currentToken().isStructStart()) {
              level++;
            } else if (parser.currentToken().isStructEnd()) {
              level--;
            }
          } while(level > 0);
        }
         */
        result.addStatement("parser.nextToken()");
        result.beginControlFlow("if(parser.currentToken().isStructStart())")
                .addStatement("int level = 1")
                    .beginControlFlow("do")
                    .addStatement("parser.nextToken()")
                        .beginControlFlow("if (parser.currentToken().isStructStart())")
                            .addStatement("level++")
                        .nextControlFlow("if (parser.currentToken().isStructEnd())")
                            .addStatement("level--")
                        .endControlFlow()
                    .endControlFlow("while(level > 0)")
                .endControlFlow();


        return result.build();
    }

    private SimplePropertyReaderProducer propertyReaderProducer(PropertySpec propertySpec) {
        SimplePropertyReaderProducer propertyReaderProducer = null;
        if(propertySpec.typeSpec().typeKind() == TypeKind.JAVA_TYPE) {
            propertyReaderProducer = SimplePropertyReaders.forClassName(propertySpec.typeSpec().typeRef()).producer();
        } else if(propertySpec.typeSpec().typeKind() == TypeKind.ENUM) {
            //"getText", String.class, JsonToken.VALUE_STRING
            propertyReaderProducer = new SimplePropertyReaderProducer(
                    new HashSet<>(Arrays.asList(JsonToken.VALUE_STRING)),
                    "getText",
                    new EnumPropertyStatement(this.types)
            );
        }
        return propertyReaderProducer;
    }

    private void addPropertyReaderStatements(TypeSpec.Builder result, PropertySpec propertySpec, SimplePropertyReaderProducer propertyReader) {
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

    private String expectedTokenField(PropertySpec propertySpec) {
        return propertySpec.name().toUpperCase() + "_EXPECTEDTOKENS";
    }

    private MethodSpec readArrayWithParserMethod() {
        MethodSpec.Builder method = MethodSpec.methodBuilder("readArray")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(JsonParser.class, "parser")
                .returns(ArrayTypeName.of(this.types.valueType()))
                .addException(IOException.class);

        method
                .addStatement("parser.nextToken()")
                .addStatement("if (parser.currentToken() == JsonToken.VALUE_NULL) return null")
                .beginControlFlow("if (parser.currentToken() == JsonToken.START_ARRAY)")
                    .addStatement("LinkedList<$T> listValue = new LinkedList<>()", this.types.valueType())
                    .beginControlFlow("while (parser.nextToken() != JsonToken.END_ARRAY)")
                        .beginControlFlow("if(parser.currentToken() == JsonToken.VALUE_NULL)")
                            .addStatement("listValue.add(null)")
                        .nextControlFlow("else")
                            .addStatement("listValue.add(this.read(parser))")
                        .endControlFlow()
                    .endControlFlow()
                    .addStatement("return listValue.toArray(new $T[listValue.size()])", this.types.valueType())
                .endControlFlow()
                .addStatement("throw new IOException(String.format($S, parser.currentToken()))",
                        "failed reading " + this.types.valueType() + " array, current token was %s"
                )
                 ;

        return method.build();
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
        method.addStatement("$T builder = $T.builder()", this.types.valueBuilderType(), this.types.valueType());
        method.beginControlFlow("while (parser.nextToken() != $T.END_OBJECT)", JsonToken.class)
                .addStatement("Token token = Token.from(parser.getCurrentName())")
                .beginControlFlow("if(token != null)")
                    .beginControlFlow("switch (token)");
        for (PropertySpec propertySpec : this.propertySpecs) {
            this.propertyStatements(method, propertySpec);
        }
        method.beginControlFlow("default:")
                .addStatement("this.consumeUnexpectedProperty(parser)")
                .endControlFlow()
            ;
        method.endControlFlow()
                .nextControlFlow("else") // token == null
                    .addStatement("this.consumeUnexpectedProperty(parser)")
                .endControlFlow()
                .endControlFlow();

        method.addStatement("return builder.build()");

        return method.build();
    }

    private void propertyStatements(MethodSpec.Builder method, PropertySpec propertySpec) {
        if(propertySpec.typeSpec().typeKind() == TypeKind.JAVA_TYPE || propertySpec.typeSpec().typeKind() == TypeKind.ENUM) {
            SimplePropertyReaderProducer propertyReaderProducer = this.propertyReaderProducer(propertySpec);
            if(propertyReaderProducer!= null) {
                if (!propertySpec.typeSpec().cardinality().isCollection()) {
                    this.singleSimplePropertyStatement(method, propertySpec, propertyReaderProducer);
                } else {
                    this.multipleSimplePropertyStatement(method, propertySpec, propertyReaderProducer);
                }
            } else {
                System.err.println("NYIMPL type ref for simple property: " + propertySpec.typeSpec().typeRef());
            }
        } else if(propertySpec.typeSpec().typeKind().isValueObject()) {
            if(! propertySpec.typeSpec().cardinality().isCollection()) {
                this.singleComplexPropertyStatement(method, propertySpec);
            } else {
                ClassName propertyClass = this.types.valueObjectSingleType(propertySpec);
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
                method.beginControlFlow("case $L:", this.enumConstant(propertySpec))
                        .addStatement("$T reader = new $T()", propertyReader, propertyReader)
                        .addStatement("builder.$L(this.readListValue(parser, jsonParser -> reader.read(jsonParser), $S))",
                                propertySpec.name(), propertySpec.name())
                        .addStatement("break")
                        .endControlFlow();
            }
        }
    }

    private void singleComplexPropertyStatement(MethodSpec.Builder method, PropertySpec propertySpec) {
        ClassName propertyClass = this.types.valueObjectSingleType(propertySpec);
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
            method.beginControlFlow("case $L:", this.enumConstant(propertySpec))
                    .addStatement("parser.nextToken()")
                    .addStatement("builder.$L(new $T().read(parser))", propertySpec.name(), propertyReader)
                    .addStatement("break")
                    .endControlFlow();
        }
    }

    private void singleSimplePropertyStatement(MethodSpec.Builder method, PropertySpec propertySpec, SimplePropertyReaderProducer propertyReaderProducer) {
        /*
        case "prop":
                Set<JsonToken> expectedTokens = PROP_EXPECTEDTOKENS;
         */
        method.beginControlFlow("case $L:", this.enumConstant(propertySpec))
                .addStatement("$T<$T> expectedTokens = $L", Set.class, JsonToken.class, this.expectedTokenField(propertySpec));

        propertyReaderProducer.addSingleStatement(method, propertySpec);

        /*
                break;
         */
        method
                .addStatement("break")
                .endControlFlow();
    }

    private void multipleSimplePropertyStatement(MethodSpec.Builder method, PropertySpec propertySpec, SimplePropertyReaderProducer propertyReaderProducer) {
        /*
        case "listProp":
         */
        method.beginControlFlow("case $L:", this.enumConstant(propertySpec));
        propertyReaderProducer.addMultipleStatement(method, propertySpec);
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
                        .beginControlFlow("if(parser.currentToken() == $T.VALUE_NULL)", JsonToken.class)
                            .addStatement("listValue.add(null)")
                        .nextControlFlow("else")
                            .addStatement("listValue.add(reader.read(parser))")
                        .endControlFlow()
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
