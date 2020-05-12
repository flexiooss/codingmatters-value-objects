package org.codingmatters.value.objects.generation;

import com.squareup.javapoet.CodeBlock;
import org.codingmatters.value.objects.spec.PropertySpec;

import java.util.Collection;
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
            block.beginControlFlow("if(value.get($S) != null)", this.types.fieldName(propertySpec));
            if(propertySpec.typeSpec().cardinality().isCollection()) {
                if(propertySpec.typeSpec().isInSpecEnum()) {
                    block.addStatement("builder.$L(($T) (($T)value.get($S)).stream().map(v -> { try { return $T.valueOf(($T) v); } catch($T e) { return null;} }).collect($T.toList()))",
                            propertySpec.name(),
                            Collection.class,
                            List.class,
                            this.types.fieldName(propertySpec),
                            this.types.propertySingleType(propertySpec),
                            String.class,
                            IllegalArgumentException.class,
                            Collectors.class
                    );
                } else if(propertySpec.typeSpec().typeKind().isValueObject()) {
                    block.addStatement("builder.$L(($T) (($T)value.get($S)).stream().map(v -> $T.fromMap(($T) v).build()).collect($T.toList()))",
                            propertySpec.name(),
                            Collection.class,
                            List.class,
                            this.types.fieldName(propertySpec),
                            this.types.propertySingleType(propertySpec),
                            Map.class,
                            Collectors.class
                    );
                } else {
                    block.addStatement("builder.$L(($T) (($T)value.get($S)).stream().collect($T.toList()))",
                            propertySpec.name(),
                            Collection.class,
                            List.class,
                            this.types.fieldName(propertySpec),
                            Collectors.class
                    );
                }
            } else {
                if(propertySpec.typeSpec().isInSpecEnum()) {
                    block
                            .beginControlFlow("try")
                            .addStatement("builder.$L($T.valueOf(($T) value.get($S)))",
                                propertySpec.name(),
                                this.types.propertyType(propertySpec),
                                String.class,
                                this.types.fieldName(propertySpec))
                            .nextControlFlow("catch($T e)", IllegalArgumentException.class)
                            .endControlFlow();
                } else if (propertySpec.typeSpec().typeKind().isValueObject()) {
                    block.addStatement("builder.$L($T.fromMap(($T) value.get($S)).build())",
                            propertySpec.name(),
                            this.types.propertyType(propertySpec),
                            Map.class,
                            this.types.fieldName(propertySpec)
                    );
                } else {
                    block.addStatement("builder.$L(($T) value.get($S))",
                            propertySpec.name(),
                            this.types.propertyType(propertySpec),
                            this.types.fieldName(propertySpec)
                    );
                }
            }
            block.endControlFlow();
        }

        block
                .addStatement("return builder")
                .endControlFlow();
        return block.build();
    }
}
