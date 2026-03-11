package org.codingmatters.value.objects.generation;

import com.squareup.javapoet.CodeBlock;
import org.codingmatters.value.objects.spec.PropertySpec;
import org.codingmatters.value.objects.spec.TypeKind;
import org.codingmatters.value.objects.spec.TypeToken;

import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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

            if (propertySpec.typeSpec().cardinality().isCollection()) {
                this.multipleProperty(block, propertySpec);
            } else {
                block.beginControlFlow("if ($LPropertyValue != null)", propName);
                this.singleProperty(block, propertySpec);
                block.endControlFlow();
            }
        }

        block
                .addStatement("return builder")
                .endControlFlow();
        return block.build();
    }

    private void singleProperty(CodeBlock.Builder block, PropertySpec propertySpec) {
        if (propertySpec.typeSpec().typeKind().equals(TypeKind.ENUM)) {
            block.beginControlFlow("if ($LPropertyValue instanceof $T __str)", propertySpec.name(), String.class);
            block
                    .beginControlFlow("try")
                    .addStatement("builder.$L($T.valueOf(__str))",
                            propertySpec.name(),
                            this.types.propertyType(propertySpec)
                    )
                    .nextControlFlow("catch($T e)", IllegalArgumentException.class)
                    .endControlFlow();
            block.endControlFlow();
        } else if (propertySpec.typeSpec().typeKind().isValueObject()) {
            block.beginControlFlow("if ($LPropertyValue instanceof $T __map)", propertySpec.name(), Map.class);
            block.addStatement("builder.$L($T.fromMap(__map).build())",
                    propertySpec.name(),
                    this.types.propertyType(propertySpec)
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
                block.beginControlFlow("if ($LPropertyValue instanceof $T __num)", propertySpec.name(), Number.class);
                block.addStatement("$T $LValue = __num.$L()",
                        this.types.propertyType(propertySpec),
                        propertySpec.name(),
                        this.numberMethodFor(propertySpec.typeSpec().typeRef())
                );
            } else {
                block.beginControlFlow("if ($LPropertyValue instanceof $T $LValue)", propertySpec.name(), this.types.propertyType(propertySpec), propertySpec.name());
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
        block.beginControlFlow("if ($LPropertyValue instanceof $T __coll)", propertySpec.name(), Collection.class);
        String listVar = propertySpec.name() + "Elements";

        if (propertySpec.typeSpec().typeKind().equals(TypeKind.ENUM)) {
            block.addStatement("$T $L = new $T(__coll.size())",
                    List.class, listVar, ArrayList.class);
            block.beginControlFlow("for ($T v : __coll)", Object.class);
            block.beginControlFlow("if (v instanceof $T __str)", String.class);
            block.beginControlFlow("try");
            block.addStatement("$L.add($T.valueOf(__str))", listVar, this.types.propertySingleType(propertySpec));
            block.nextControlFlow("catch($T e)", IllegalArgumentException.class);
            block.endControlFlow();
            block.endControlFlow();
            block.endControlFlow();
            block.addStatement("builder.$L($L)", propertySpec.name(), listVar);
        } else if (propertySpec.typeSpec().typeKind().isValueObject()) {
            block.addStatement("$T $L = new $T(__coll.size())",
                    List.class, listVar, ArrayList.class);
            block.beginControlFlow("for ($T v : __coll)", Object.class);
            block.beginControlFlow("if (v instanceof $T __map)", Map.class);
            block.addStatement("$L.add($T.fromMap(__map).build())", listVar, this.types.propertySingleType(propertySpec));
            block.endControlFlow();
            block.endControlFlow();
            block.addStatement("builder.$L($L)", propertySpec.name(), listVar);
        } else if (TypeKind.JAVA_TYPE.equals(propertySpec.typeSpec().typeKind())) {
            if (this.types.isDateOrTimeType(propertySpec.typeSpec().typeRef())) {
                this.dateOrTypeCollection(block, propertySpec);
            } else {
                block.addStatement("$T $L = new $T(__coll.size())",
                        List.class, listVar, ArrayList.class);
                block.beginControlFlow("for ($T v : __coll)", Object.class);
                block.beginControlFlow("if (v instanceof $T __val)", this.types.propertySingleType(propertySpec));
                block.addStatement("$L.add(__val)", listVar);
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

        block.beginControlFlow("if ($LPropertyValue instanceof $T __str)", propertySpec.name(), String.class)
                .addStatement("$T __temporal = $T.$L.parse(__str)",
                        TemporalAccessor.class,
                        this.types.valueImplType(),
                        this.formatterFor(propertySpec.typeSpec().typeRef())
                )
                .addStatement("$LValue = $T.from(__temporal)",
                        propertySpec.name(),
                        this.types.propertyType(propertySpec)
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
        block.addStatement("$T $LElements = new $T(__coll.size())", List.class, propertySpec.name(), ArrayList.class);
        block.beginControlFlow("for ($T __raw : __coll)", Object.class)
                .addStatement("$T $LValue = null",
                        this.types.propertySingleType(propertySpec),
                        propertySpec.name()
                )

                .beginControlFlow("if (__raw instanceof $T __str)", String.class)
                .addStatement("$T __temporal = $T.$L.parse(__str)",
                        TemporalAccessor.class,
                        this.types.valueImplType(),
                        this.formatterFor(propertySpec.typeSpec().typeRef())
                )
                .addStatement("$LValue = $T.from(__temporal)",
                        propertySpec.name(),
                        this.types.propertySingleType(propertySpec)
                )
                .nextControlFlow("else if (__raw instanceof $T __val)", this.types.propertySingleType(propertySpec))
                .addStatement("$LValue = __val", propertySpec.name())
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
