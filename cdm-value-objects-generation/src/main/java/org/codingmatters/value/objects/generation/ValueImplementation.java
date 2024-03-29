package org.codingmatters.value.objects.generation;

import com.squareup.javapoet.*;
import org.codingmatters.value.objects.spec.PropertyCardinality;
import org.codingmatters.value.objects.spec.PropertySpec;
import org.codingmatters.value.objects.spec.TypeKind;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static javax.lang.model.element.Modifier.*;

/**
 * Created by nelt on 9/22/16.
 */
public class ValueImplementation {

    private final ValueConfiguration types;
    private final List<PropertySpec> propertySpecs;

    private final MethodSpec constructor;
    private final List<FieldSpec> fields;
    private final List<MethodSpec> getters;
    private final List<MethodSpec> withers;
    private final MethodSpec equalsMethod;
    private final MethodSpec hashCodeMethod;
    private final MethodSpec toStringMethod;
    private final MethodSpec changedMethod;
    private final MethodSpec toMapMethod;
    private final FieldSpec localDateTemporalFormatterField;
    private final FieldSpec localTimeTemporalFormatterField;

    public ValueImplementation(ValueConfiguration types, List<PropertySpec> propertySpecs) {
        this.types = types;
        this.propertySpecs = propertySpecs;

        this.constructor = this.createConstructor();
        this.fields = this.createFields();
        this.getters = this.createGetters();
        this.withers = this.createWithers();
        this.equalsMethod = this.createEquals();
        this.hashCodeMethod = this.createHashCode();
        this.toStringMethod = this.createToString();
        this.changedMethod = this.createChangedMethod();
        this.toMapMethod = this.createToMapMethod();
        this.localDateTemporalFormatterField = this.createLocalDateTemporalFormatterField();
        this.localTimeTemporalFormatterField = this.createLocalTimeTemporalFormatterField();

    }

    public TypeSpec type() {
        return TypeSpec.classBuilder(this.types.valueImplType())
                .addSuperinterface(this.types.valueType())
                .addMethod(this.constructor)
                .addFields(this.fields)
                .addMethods(this.getters)
                .addMethods(this.withers)
                .addMethod(this.changedMethod)
                .addMethod(this.equalsMethod)
                .addMethod(this.hashCodeMethod)
                .addMethod(this.toStringMethod)
                .addMethod(this.optMethod())
                .addMethod(this.toMapMethod)
                .addField(this.localDateTemporalFormatterField)
                .addField(this.localTimeTemporalFormatterField)
                .build();
    }

    private MethodSpec createConstructor() {
        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder();

        for (PropertySpec propertySpec : propertySpecs) {
            constructorBuilder
                    .addParameter(this.types.propertyType(propertySpec), propertySpec.name())
                    .addStatement("this.$N = $N", propertySpec.name(), propertySpec.name())
            ;
        }

        return constructorBuilder.build();
    }

    private List<FieldSpec> createFields() {
        List<FieldSpec> fields = new LinkedList<>();
        for (PropertySpec propertySpec : propertySpecs) {
            fields.add(
                    FieldSpec.builder(this.types.propertyType(propertySpec), propertySpec.name(), PRIVATE, FINAL).build()
            );
        }
        return fields;
    }

    private List<MethodSpec> createGetters() {
        List<MethodSpec> getters = new LinkedList<>();
        for (PropertySpec propertySpec : propertySpecs) {
            getters.add(
                    MethodSpec.methodBuilder(propertySpec.name())
                            .returns(this.types.propertyType(propertySpec))
                            .addModifiers(PUBLIC)
                            .addStatement("return this.$N", propertySpec.name())
                            .build()
            );
        }
        return getters;
    }

    private MethodSpec createEquals() {

        String statement;
        List<Object> bindings= new LinkedList<>();
        if(propertySpecs.size() > 0) {
            statement = "$T that = ($T) o;\n";
            bindings.add(this.types.valueImplType());
            bindings.add(this.types.valueImplType());

            statement += "return ";
            boolean started = false;
            for (PropertySpec propertySpec : propertySpecs) {
                if(started) {
                    statement += " && \n";
                }
                started = true;

                statement += "$T.equals(this." + propertySpec.name() + ", that." + propertySpec.name() + ")";
                if(this.isByteArrayType(propertySpec)) {
                    bindings.add(ClassName.get(Arrays.class));
                } else {
                    bindings.add(ClassName.get(Objects.class));
                }
            }

        } else {
            statement = "return true";
        }

        return MethodSpec.methodBuilder("equals")
                .addModifiers(PUBLIC)
                .addParameter(ClassName.bestGuess(Object.class.getName()), "o")
                .returns(boolean.class)
                .addAnnotation(ClassName.get(Override.class))
                .addStatement("if (this == o) return true")
                .addStatement("if (o == null || getClass() != o.getClass()) return false")
                .addStatement(statement, bindings.toArray())
                .build();
    }

    private boolean isByteArrayType(PropertySpec propertySpec) {
        return propertySpec.typeSpec().typeKind().equals(TypeKind.JAVA_TYPE) && propertySpec.typeSpec().typeRef().equals(byte[].class.getName());
    }

    private MethodSpec createHashCode() {

        String statement = propertySpecs.stream()
                .map(propertySpec -> "this." + propertySpec.name())
                .collect(Collectors.joining(
                        ", ",
                        "return $T.deepHashCode(new $T[]{",
                        "})"
                ));

        return MethodSpec.methodBuilder("hashCode")
                .addModifiers(PUBLIC)
                .returns(int.class)
                .addAnnotation(ClassName.get(Override.class))
                .addStatement(statement, Arrays.class, Object.class)
                .build();
    }

    private MethodSpec createToString() {
        String statement =
                propertySpecs.stream()
                        .map(propertySpec -> "\"" + propertySpec.name() + "=\" + $T.toString(this." + propertySpec.name() + ") +\n")
                        .collect(Collectors.joining(
                                "\", \" + ",
                                "return \"" + this.types.valueType().simpleName() + "{\" +\n",
                                "'}'"
                                )
                        )
                ;

        Object [] args = new Object[propertySpecs.size()];
        for (int i = 0; i < propertySpecs.size() ; i++) {
            PropertySpec propertySpec = propertySpecs.get(i);
            if(this.isByteArrayType(propertySpec)) {
                args[i] = Arrays.class;
            } else {
                args[i] = Objects.class;
            }
        }

        return MethodSpec.methodBuilder("toString")
                .addModifiers(PUBLIC)
                .returns(String.class)
                .addAnnotation(Override.class)
                .addStatement(statement, args)
                .build();
    }


    private List<MethodSpec> createWithers() {
        List<MethodSpec> result = new LinkedList<>();

        for (PropertySpec propertySpec : propertySpecs) {
            result.add(
                    MethodSpec.methodBuilder(this.types.witherMethodName(propertySpec))
                            .returns(this.types.valueType())
                            .addModifiers(PUBLIC)
                            .addParameter(this.types.propertyType(propertySpec), "value")
                            .addStatement("return $T.from(this)." + propertySpec.name() + "(value).build()", this.types.valueType())
                            .build()
            );
            if(propertySpec.typeSpec().typeKind().isValueObject()) {
                this.createChangedWithers(result, propertySpec);
            }
            if(propertySpec.typeSpec().cardinality().isCollection()) {
                result.add(
                        MethodSpec.methodBuilder(this.types.witherMethodName(propertySpec))
                                .returns(this.types.valueType())
                                .addModifiers(PUBLIC)
                                .addParameter(ParameterizedTypeName.get(ClassName.get(Iterable.class), this.types.propertySingleType(propertySpec)), "values")
                                .addStatement(
                                        "return $T.from(this).$L(values != null ? $L.<$T>builder().with(values).build() : $L.<$T>builder().build()).build()",
                                        this.types.valueType(),
                                        propertySpec.name(),
                                        propertySpec.typeSpec().cardinality().equals(PropertyCardinality.LIST) ? "ValueList" : "ValueSet",
                                        this.types.propertySingleType(propertySpec),
                                        propertySpec.typeSpec().cardinality().equals(PropertyCardinality.LIST) ? "ValueList" : "ValueSet",
                                        this.types.propertySingleType(propertySpec)
                                )
                                .build()
                );
            }
        }
        return result;
    }

    private void createChangedWithers(List<MethodSpec> result, PropertySpec propertySpec) {
        if(! propertySpec.typeSpec().cardinality().isCollection()) {
            result.add(
                    MethodSpec.methodBuilder(this.types.changedWitherMethodName(propertySpec))
                            .returns(this.types.valueType())
                            .addModifiers(PUBLIC)
                            .addParameter(this.types.valueObjectSingleType(propertySpec).nestedClass("Changer"), "changer")
                            .addStatement("$T builder = $T.from(this)", this.types.valueType().nestedClass("Builder"), this.types.valueType())
                            .addStatement("$T valueBuilder = $T.from(this.$L())",
                                    this.types.valueObjectSingleType(propertySpec).nestedClass("Builder"),
                                    this.types.valueObjectSingleType(propertySpec),
                                    propertySpec.name()
                            )
                            .addStatement("changer.configure(valueBuilder)")
                            .addStatement("builder.$L(valueBuilder.build())", propertySpec.name())
                            .addStatement("return builder.build()")
                            .build()
            );
            result.add(MethodSpec.methodBuilder(this.types.fromWitherMethodName(propertySpec))
                    .returns(this.types.valueType())
                    .addModifiers(PUBLIC)
                    .addParameter(ParameterizedTypeName.get(
                            ClassName.get(Function.class),
                            this.types.propertySingleType(propertySpec),
                            this.types.propertySingleType(propertySpec)
                            ),
                            "current"
                    )
                    .addStatement("return this.$L(current.apply(this.$L()))", this.types.witherMethodName(propertySpec), propertySpec.name())
                    .build()
            );
        } else {
            TypeName type = this.types.propertySingleType(propertySpec);
            TypeName collectionType = this.types.collectionRawType(propertySpec);
            ParameterizedTypeName changerType =
                    propertySpec.typeSpec().cardinality().equals(PropertyCardinality.LIST) ?
                            this.types.collectionConfiguration().valueListOfTypeChanger(type) :
                            this.types.collectionConfiguration().valueSetOfTypeChanger(type);

            result.add(MethodSpec.methodBuilder(this.types.changedWitherMethodName(propertySpec))
                    .returns(this.types.valueType())
                    .addModifiers(PUBLIC)
                    .addParameter(changerType, "changer")
                    /*
                      ValueList.Builder<Review> builder = ValueList.builder();
                      builder.with(this.reviews);
                      return this.withReviews(changer.configure(builder).build());
                     */
                    .addStatement("$T.Builder<$T> builder = $T.builder()",
                            collectionType, type, collectionType)
                    .addStatement("builder.with(this.$L)", propertySpec.name())
                    .addStatement("return this.$L(changer.configure(builder).build())", this.types.witherMethodName(propertySpec))
                    .build()
            );
            result.add(MethodSpec.methodBuilder(this.types.fromWitherMethodName(propertySpec))
                    .returns(this.types.valueType())
                    .addModifiers(PUBLIC)
                    .addParameter(
                            ParameterizedTypeName.get(
                                    ClassName.get(Function.class),
                                    this.types.propertyType(propertySpec),
                                    this.types.propertyType(propertySpec)
                            ),
                            "current"
                    )
                    .addStatement("return this.$L(current.apply(this.$L()))", this.types.witherMethodName(propertySpec), propertySpec.name())
                    .build()
            );
        }
    }

    private MethodSpec createChangedMethod() {
        return MethodSpec.methodBuilder("changed")
                .addModifiers(PUBLIC)
                .addParameter(this.types.valueChangerType(), "changer")
                .returns(this.types.valueType())
                .addStatement("return changer.configure($T.from(this)).build()", this.types.valueType())
                .build();
    }

    private MethodSpec createToMapMethod() {
        return MethodSpec.methodBuilder("toMap")
                .addModifiers(PUBLIC)
                .returns(Map.class)
                .addCode(new ToMapMethod(this.types).block())
                .build();
    }

    private MethodSpec optMethod() {
        return MethodSpec.methodBuilder("opt")
                .addModifiers(PUBLIC)
                .returns(this.types.optionalValueType())
                .addStatement("return $T.of(this)", this.types.optionalValueType())
                .build();
    }


    private FieldSpec createLocalDateTemporalFormatterField() {
        return FieldSpec.builder(DateTimeFormatter.class, "LOCAL_DATE_TEMPORAL_FORMATTER", STATIC, PUBLIC, FINAL)
                .initializer(CodeBlock.builder()
                        .addStatement("new $T().appendPattern($S)" +
                                ".parseDefaulting($T.MONTH_OF_YEAR, 1)" +
                                ".parseDefaulting(ChronoField.DAY_OF_MONTH, 1)\n" +
                                ".parseDefaulting(ChronoField.HOUR_OF_DAY, 0)\n" +
                                ".parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)\n" +
                                ".parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)\n" +
                                ".toFormatter()",
                                DateTimeFormatterBuilder.class,
                                "yyyy[-MM[-dd['T'HH[:mm[:ss[.SSSSSSSSS][.SSSSSSSS][.SSSSSSS][.SSSSSS][.SSSSS][.SSSS][.SSS][.SS][.S]]]",
                                ChronoField.class
                        )
                        .build())
                .build();
    }

    private FieldSpec createLocalTimeTemporalFormatterField() {
        return FieldSpec.builder(DateTimeFormatter.class, "LOCAL_TIME_TEMPORAL_FORMATTER", STATIC, PUBLIC, FINAL)
                .initializer(CodeBlock.builder()
                        .addStatement("new $T().appendPattern($S)" +
                                ".parseDefaulting($T.MINUTE_OF_HOUR, 0)\n" +
                                ".parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)\n" +
                                ".toFormatter()",
                                DateTimeFormatterBuilder.class,
                                "HH[:mm[:ss[.SSSSSSSSS][.SSSSSSSS][.SSSSSSS][.SSSSSS][.SSSSS][.SSSS][.SSS][.SS][.S]]]",
                                ChronoField.class
                        )
                        .build())
                .build();
    }
}
