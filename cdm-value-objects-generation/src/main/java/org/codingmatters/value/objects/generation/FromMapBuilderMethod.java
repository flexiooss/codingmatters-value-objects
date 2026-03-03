package org.codingmatters.value.objects.generation;

import com.squareup.javapoet.CodeBlock;
import org.codingmatters.value.objects.spec.PropertySpec;
import org.codingmatters.value.objects.spec.TypeKind;
import org.codingmatters.value.objects.spec.TypeToken;

import java.time.temporal.TemporalAccessor;
import java.util.*;

public class FromMapBuilderMethod {
    private final ValueConfiguration types;

    public FromMapBuilderMethod(ValueConfiguration types) {
        this.types = types;
    }

    public CodeBlock block() {
        CodeBlock.Builder block = CodeBlock.builder();
        block
                .beginControlFlow("if (value == null)")
                .addStatement("return $T.builder()", this.types.valueType())
                .nextControlFlow("else")
                .addStatement("$T builder = $T.builder()", this.types.valueBuilderType(), this.types.valueType());

        for (PropertySpec propertySpec : this.types.valueSpec().propertySpecs()) {
            String fieldName = this.types.fieldName(propertySpec);
            String propName = propertySpec.name();

            // Optimization: single lookup when field name equals property name
            block.addStatement("$T $LPropertyValue = value.get($S)", Object.class, propName, fieldName);
            if (!fieldName.equals(propName)) {
                block.beginControlFlow("if ($LPropertyValue == null)", propName);
                block.addStatement("$LPropertyValue = value.get($S)", propName, propName);
                block.endControlFlow();
            }

            block.beginControlFlow("if ($LPropertyValue != null)", propName);
            if (propertySpec.typeSpec().cardinality().isCollection()) {
                this.multipleProperty(block, propertySpec);
            } else {
                this.singleProperty(block, propertySpec);
            }
            block.endControlFlow();
        }

        block
                .addStatement("return builder")
                .endControlFlow();
        return block.build();
    }

    private void singleProperty(CodeBlock.Builder block, PropertySpec propertySpec) {
        if (propertySpec.typeSpec().typeKind().equals(TypeKind.ENUM)) {
            block.beginControlFlow("if ($LPropertyValue instanceof $T)", propertySpec.name(), String.class);
            block
                    .beginControlFlow("try")
                    .addStatement("builder.$L($T.valueOf(($T) $LPropertyValue))",
                            propertySpec.name(),
                            this.types.propertyType(propertySpec),
                            String.class,
                            propertySpec.name()
                    )
                    .nextControlFlow("catch($T e)", IllegalArgumentException.class)
                    .endControlFlow();
            block.endControlFlow();
        } else if (propertySpec.typeSpec().typeKind().isValueObject()) {
            block.beginControlFlow("if ($LPropertyValue instanceof $T)", propertySpec.name(), Map.class);
            block.addStatement("builder.$L($T.fromMap(($T) $LPropertyValue).build())",
                    propertySpec.name(),
                    this.types.propertyType(propertySpec),
                    Map.class,
                    propertySpec.name()
            );
            block.endControlFlow();
        } else if (TypeKind.JAVA_TYPE.equals(propertySpec.typeSpec().typeKind())) {
            if (this.types.isDateOrTimeType(propertySpec.typeSpec().typeRef())) {
                block.beginControlFlow("if ($LPropertyValue instanceof $T || $LPropertyValue instanceof $T)",
                        propertySpec.name(),
                        this.types.propertyType(propertySpec),
                        propertySpec.name(),
                        String.class);
                this.dateAndTimeType(block, propertySpec);
            } else if (this.types.isNumber(propertySpec.typeSpec().typeRef())) {
                block.beginControlFlow("if ($LPropertyValue instanceof $T)", propertySpec.name(), Number.class);
                block.addStatement("$T $LValue = (($T) $LPropertyValue).$L()",
                        this.types.propertyType(propertySpec),
                        propertySpec.name(),
                        Number.class,
                        propertySpec.name(),
                        this.numberMethodFor(propertySpec.typeSpec().typeRef())
                );
            } else {
                block.beginControlFlow("if ($LPropertyValue instanceof $T)", propertySpec.name(), this.types.propertyType(propertySpec));
                block.addStatement("$T $LValue = ($T) $LPropertyValue",
                        this.types.propertyType(propertySpec),
                        propertySpec.name(),
                        this.types.propertyType(propertySpec),
                        propertySpec.name()
                );
            }
            block.addStatement("builder.$L($LValue)",
                    propertySpec.name(),
                    propertySpec.name()
            );
            block.endControlFlow();
        }
    }

    private String numberMethodFor(String typeRef) {
        if (TypeToken.INT.getImplementationType().equals(typeRef)) {
            return "intValue";
        }
        if (TypeToken.LONG.getImplementationType().equals(typeRef)) {
            return "longValue";
        }
        if (TypeToken.FLOAT.getImplementationType().equals(typeRef)) {
            return "floatValue";
        }
        if (TypeToken.DOUBLE.getImplementationType().equals(typeRef)) {
            return "doubleValue";
        }
        return null;
    }

    private void multipleProperty(CodeBlock.Builder block, PropertySpec propertySpec) {
        block.beginControlFlow("if ($LPropertyValue instanceof $T)", propertySpec.name(), Collection.class);
        String listVar = propertySpec.name() + "Elements";

        if (propertySpec.typeSpec().typeKind().equals(TypeKind.ENUM)) {
            block.addStatement("$T $L = new $T((($T)$LPropertyValue).size())",
                    List.class, listVar, ArrayList.class, Collection.class, propertySpec.name());
            block.beginControlFlow("for($T v : ($T) $LPropertyValue)", Object.class, Collection.class, propertySpec.name());
            block.beginControlFlow("if (v instanceof $T)", String.class);
            block.beginControlFlow("try");
            block.addStatement("$L.add($T.valueOf(($T) v))", listVar, this.types.propertySingleType(propertySpec), String.class);
            block.nextControlFlow("catch($T e)", IllegalArgumentException.class);
            block.endControlFlow();
            block.endControlFlow();
            block.endControlFlow();
            block.addStatement("builder.$L($L)", propertySpec.name(), listVar);
        } else if (propertySpec.typeSpec().typeKind().isValueObject()) {
            block.addStatement("$T $L = new $T((($T)$LPropertyValue).size())",
                    List.class, listVar, ArrayList.class, Collection.class, propertySpec.name());
            block.beginControlFlow("for($T v : ($T) $LPropertyValue)", Object.class, Collection.class, propertySpec.name());
            block.beginControlFlow("if (v instanceof $T)", Map.class);
            block.addStatement("$L.add($T.fromMap(($T) v).build())", listVar, this.types.propertySingleType(propertySpec), Map.class);
            block.endControlFlow();
            block.endControlFlow();
            block.addStatement("builder.$L($L)", propertySpec.name(), listVar);
        } else if (TypeKind.JAVA_TYPE.equals(propertySpec.typeSpec().typeKind())) {
            if (this.types.isDateOrTimeType(propertySpec.typeSpec().typeRef())) {
                this.dateOrTypeCollection(block, propertySpec);
            } else {
                block.addStatement("$T $L = new $T((($T)$LPropertyValue).size())",
                        List.class, listVar, ArrayList.class, Collection.class, propertySpec.name());
                block.beginControlFlow("for($T v : ($T) $LPropertyValue)", Object.class, Collection.class, propertySpec.name());
                block.beginControlFlow("if (v instanceof $T)", this.types.propertySingleType(propertySpec));
                block.addStatement("$L.add(($T) v)", listVar, this.types.propertySingleType(propertySpec));
                block.endControlFlow();
                block.endControlFlow();
                block.addStatement("builder.$L($L)", propertySpec.name(), listVar);
            }
        }
        block.endControlFlow();
    }

    private void dateAndTimeType(CodeBlock.Builder block, PropertySpec propertySpec) {
        block.addStatement("$T $LValue",
                this.types.propertyType(propertySpec),
                propertySpec.name()
        );

        block.beginControlFlow("if ($LPropertyValue instanceof $T)", propertySpec.name(), String.class)
                .addStatement("$T $LTemporal = $T.$L.parse(($T) $LPropertyValue)",
                        TemporalAccessor.class,
                        propertySpec.name(),
                        this.types.valueImplType(),
                        this.formatterFor(propertySpec.typeSpec().typeRef()),
                        String.class,
                        propertySpec.name()
                )
                .addStatement("$LValue = $T.from($LTemporal)",
                        propertySpec.name(),
                        this.types.propertyType(propertySpec),
                        propertySpec.name()
                )
        ;
        block.nextControlFlow("else")
                .addStatement("$LValue = ($T) $LPropertyValue",
                        propertySpec.name(),
                        this.types.propertyType(propertySpec),
                        propertySpec.name()
                );
        block.endControlFlow();
    }

    private void dateOrTypeCollection(CodeBlock.Builder block, PropertySpec propertySpec) {
        block.addStatement("$T $LElements = new $T()", List.class, propertySpec.name(), LinkedList.class);
        block.beginControlFlow("for($T rawElement : ($T) $LPropertyValue)", Object.class, Collection.class, propertySpec.name())
                .addStatement("$T $LValue = null",
                        this.types.propertySingleType(propertySpec),
                        propertySpec.name()
                )

                .beginControlFlow("if (rawElement instanceof $T)", String.class)
                .addStatement("$T $LTemporal = $T.$L.parse(($T) rawElement)",
                        TemporalAccessor.class,
                        propertySpec.name(),
                        this.types.valueImplType(),
                        this.formatterFor(propertySpec.typeSpec().typeRef()),
                        String.class
                )
                .addStatement("$LValue = $T.from($LTemporal)",
                        propertySpec.name(),
                        this.types.propertySingleType(propertySpec),
                        propertySpec.name()
                )
                .nextControlFlow("else if (rawElement instanceof $T)", this.types.propertySingleType(propertySpec))
                .addStatement("$LValue = ($T) rawElement", propertySpec.name(), this.types.propertySingleType(propertySpec))
                .endControlFlow()
                .beginControlFlow("if ($LValue != null)", propertySpec.name())
                .addStatement("$LElements.add($LValue)", propertySpec.name(), propertySpec.name())
                .endControlFlow()
                .endControlFlow();
        block.addStatement("builder.$L($LElements)", propertySpec.name(), propertySpec.name());
    }

    private String formatterFor(String typeRef) {
        if (typeRef.equals(TypeToken.TIME.getImplementationType())) {
            return "LOCAL_TIME_TEMPORAL_FORMATTER";
        }
        return "LOCAL_DATE_TEMPORAL_FORMATTER";
    }
}
