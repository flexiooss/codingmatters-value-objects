package org.codingmatters.value.objects.generation;

import com.squareup.javapoet.CodeBlock;
import org.codingmatters.value.objects.spec.PropertySpec;
import org.codingmatters.value.objects.spec.TypeKind;
import org.codingmatters.value.objects.spec.TypeToken;

import java.time.temporal.TemporalAccessor;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FromMapBuilderMethod {
    private final ValueConfiguration types;

    public FromMapBuilderMethod(ValueConfiguration types) {
        this.types = types;
    }

    public CodeBlock block() {
        CodeBlock.Builder block = CodeBlock.builder();
        block
                .beginControlFlow("if(value == null)")
                .addStatement("return null")
                .nextControlFlow("else")
                .addStatement("$T builder = $T.builder()", this.types.valueBuilderType(), this.types.valueType());

        for (PropertySpec propertySpec : this.types.valueSpec().propertySpecs()) {
            block.beginControlFlow("if(value.getOrDefault($S, value.get($S)) != null)", this.types.fieldName(propertySpec), propertySpec.name());
            block.addStatement("$T propertyValue = value.getOrDefault($S, value.get($S))", Object.class, this.types.fieldName(propertySpec), propertySpec.name());
            if(propertySpec.typeSpec().cardinality().isCollection()) {
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
        if(propertySpec.typeSpec().isInSpecEnum()) {
            block.beginControlFlow("if(propertyValue instanceof $T)", String.class);
            block
                    .beginControlFlow("try")
                    .addStatement("builder.$L($T.valueOf(($T) propertyValue))",
                        propertySpec.name(),
                        this.types.propertyType(propertySpec),
                        String.class)
                    .nextControlFlow("catch($T e)", IllegalArgumentException.class)
                    .endControlFlow();
            block.endControlFlow();
        } else if (propertySpec.typeSpec().typeKind().isValueObject()) {
            block.beginControlFlow("if(propertyValue instanceof $T)", Map.class);
            block.addStatement("builder.$L($T.fromMap(($T) propertyValue).build())",
                    propertySpec.name(),
                    this.types.propertyType(propertySpec),
                    Map.class
            );
            block.endControlFlow();
        } else if (TypeKind.JAVA_TYPE.equals(propertySpec.typeSpec().typeKind())) {
            if(this.types.isDateOrTimeType(propertySpec.typeSpec().typeRef())) {
                block.beginControlFlow("if(propertyValue instanceof $T || propertyValue instanceof $T)", this.types.propertyType(propertySpec), String.class);
                this.dateAndTimeType(block, propertySpec);
            } else {
                block.beginControlFlow("if(propertyValue instanceof $T)", this.types.propertyType(propertySpec));
                block.addStatement("$T $LValue = ($T) propertyValue",
                        this.types.propertyType(propertySpec),
                        propertySpec.name(),
                        this.types.propertyType(propertySpec)
                        );
            }
            block.addStatement("builder.$L($LValue)",
                    propertySpec.name(),
                    propertySpec.name()
            );
            block.endControlFlow();
        }
    }

    private void multipleProperty(CodeBlock.Builder block, PropertySpec propertySpec) {
        block.beginControlFlow("if(propertyValue instanceof $T)", Collection.class);
        if(propertySpec.typeSpec().isInSpecEnum()) {
            block.addStatement("builder.$L(($T) (($T)propertyValue).stream().map(v -> { try { return $T.valueOf(($T) v); } catch($T e) { return null;} }).collect($T.toList()))",
                    propertySpec.name(),
                    Collection.class,
                    List.class,
                    this.types.propertySingleType(propertySpec),
                    String.class,
                    IllegalArgumentException.class,
                    Collectors.class
            );
        } else if(propertySpec.typeSpec().typeKind().isValueObject()) {
            block.addStatement("builder.$L(($T) (($T)propertyValue).stream().map(v -> $T.fromMap(($T) v).build()).collect($T.toList()))",
                    propertySpec.name(),
                    Collection.class,
                    List.class,
                    this.types.propertySingleType(propertySpec),
                    Map.class,
                    Collectors.class
            );
        } else if(TypeKind.JAVA_TYPE.equals(propertySpec.typeSpec().typeKind())) {
            if(this.types.isDateOrTimeType(propertySpec.typeSpec().typeRef())) {
                this.dateOrTypeCollection(block, propertySpec);
            } else {
                block.addStatement("builder.$L(($T) (($T)propertyValue).stream().filter(e -> e instanceof $T).collect($T.toList()))",
                        propertySpec.name(),
                        Collection.class,
                        List.class,
                        this.types.propertySingleType(propertySpec),
                        Collectors.class
                );
            }
        }
        block.endControlFlow();
    }

    private void dateAndTimeType(CodeBlock.Builder block, PropertySpec propertySpec) {
        block.addStatement("$T $LValue",
                this.types.propertyType(propertySpec),
                propertySpec.name()
        );

        block.beginControlFlow("if(propertyValue instanceof $T)",
                String.class)
                .addStatement("$T $LTemporal = $T.$L.parse(($T) propertyValue)",
                        TemporalAccessor.class,
                        propertySpec.name(),
                        this.types.valueImplType(),
                        this.formatterFor(propertySpec.typeSpec().typeRef()),
                        String.class
                )
                .addStatement("$LValue = $T.from($LTemporal)",
                        propertySpec.name(),
                        this.types.propertyType(propertySpec),
                        propertySpec.name()
                )
        ;
        block.nextControlFlow("else")
                .addStatement("$LValue = ($T) propertyValue",
                    propertySpec.name(),
                    this.types.propertyType(propertySpec)
                );
        block.endControlFlow();
    }

    private void dateOrTypeCollection(CodeBlock.Builder block, PropertySpec propertySpec) {
        block.addStatement("$T $LElements = new $T()", List.class, propertySpec.name(), LinkedList.class);
        block.beginControlFlow("for($T rawElement : ($T) propertyValue)", Object.class, Collection.class)
                .addStatement("$T $LValue = null",
                        this.types.propertySingleType(propertySpec),
                        propertySpec.name()
                )

                .beginControlFlow("if(rawElement instanceof $T)", String.class)
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
                .nextControlFlow("else if(rawElement instanceof $T)", this.types.propertySingleType(propertySpec))
                    .addStatement("$LValue = ($T) rawElement", propertySpec.name(), this.types.propertySingleType(propertySpec))
                .endControlFlow()
                .beginControlFlow("if($LValue != null)", propertySpec.name())
                    .addStatement("$LElements.add($LValue)", propertySpec.name(), propertySpec.name())
                .endControlFlow()
                .endControlFlow();
        block.addStatement("builder.$L($LElements)", propertySpec.name(), propertySpec.name());
    }

    private String formatterFor(String typeRef) {
        if(typeRef.equals(TypeToken.TIME.getImplementationType())) {
            return "LOCAL_TIME_TEMPORAL_FORMATTER";
        }
        return "LOCAL_DATE_TEMPORAL_FORMATTER";
    }
}
