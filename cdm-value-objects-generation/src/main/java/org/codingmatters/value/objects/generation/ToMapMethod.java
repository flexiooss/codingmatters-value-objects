package org.codingmatters.value.objects.generation;

import com.squareup.javapoet.CodeBlock;
import org.codingmatters.value.objects.spec.PropertySpec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ToMapMethod {
    private final ValueConfiguration types;

    public ToMapMethod(ValueConfiguration types) {
        this.types = types;
    }

    public CodeBlock block() {
        CodeBlock.Builder block = CodeBlock.builder()
                .addStatement("Map result = new $T()", HashMap.class)
                ;
        for (PropertySpec propertySpec : this.types.valueSpec().propertySpecs()) {
            block.beginControlFlow("if(this.$L() != null)", propertySpec.name());
            if(propertySpec.typeSpec().cardinality().isCollection()) {
                if(propertySpec.typeSpec().isInSpecEnum()) {
                    block.addStatement("result.put($S, this.$L().stream().map(v -> v.name()).collect($T.toList()))",
                            this.types.fieldName(propertySpec),
                            propertySpec.name(),
                            Collectors.class
                    );
                } else if(propertySpec.typeSpec().typeKind().isValueObject()) {
                    block.addStatement("result.put($S, this.$L().stream().map(v -> v.toMap()).collect($T.toList()))",
                            this.types.fieldName(propertySpec),
                            propertySpec.name(),
                            Collectors.class
                    );
                } else {
                    block.addStatement("result.put($S, this.$L().stream().collect($T.toList()))",
                            this.types.fieldName(propertySpec),
                            propertySpec.name(),
                            Collectors.class
                    );
                }
            } else {
                if(propertySpec.typeSpec().isInSpecEnum()) {
                    block.addStatement("result.put($S, this.$L().name())", this.types.fieldName(propertySpec), propertySpec.name());
                } else if (propertySpec.typeSpec().typeKind().isValueObject()) {
                    block.addStatement("result.put($S, this.$L().toMap())", this.types.fieldName(propertySpec), propertySpec.name());
                } else {
                    block.addStatement("result.put($S, this.$L())", this.types.fieldName(propertySpec), propertySpec.name());
                }
            }
            block.endControlFlow();
        }
        return block.addStatement("return result").build();
    }
}
