package org.codingmatters.value.objects.generation;

import com.squareup.javapoet.CodeBlock;
import org.codingmatters.value.objects.spec.PropertySpec;
import org.codingmatters.value.objects.spec.TypeKind;

import java.util.ArrayList;
import java.util.HashMap;

public class ToMapMethod {
    private final ValueConfiguration types;

    public ToMapMethod(ValueConfiguration types) {
        this.types = types;
    }

    public CodeBlock block() {
        int propertyCount = this.types.valueSpec().propertySpecs().size();
        CodeBlock.Builder block = CodeBlock.builder()
                .addStatement("$T result = new $T($L)", HashMap.class, HashMap.class, propertyCount);

        for (PropertySpec propertySpec : this.types.valueSpec().propertySpecs()) {
            block.beginControlFlow("if (this.$L() != null)", propertySpec.name());
            if (propertySpec.typeSpec().cardinality().isCollection()) {
                String listVar = "__" + propertySpec.name() + "List";
                block.addStatement("$T<$T> $L = new $T<>(this.$L().size())",
                        ArrayList.class, Object.class, listVar, ArrayList.class, propertySpec.name());

                if (propertySpec.typeSpec().typeKind().equals(TypeKind.ENUM)) {
                    block.beginControlFlow("for ($T __item : this.$L())", this.types.propertySingleType(propertySpec), propertySpec.name());
                    block.addStatement("$L.add(__item.name())", listVar);
                    block.endControlFlow();
                } else if (propertySpec.typeSpec().typeKind().isValueObject()) {
                    block.beginControlFlow("for ($T __item : this.$L())", this.types.propertySingleType(propertySpec), propertySpec.name());
                    block.addStatement("$L.add(__item.toMap())", listVar);
                    block.endControlFlow();
                } else {
                    block.beginControlFlow("for ($T __item : this.$L())", this.types.propertySingleType(propertySpec), propertySpec.name());
                    block.addStatement("$L.add(__item)", listVar);
                    block.endControlFlow();
                }
                block.addStatement("result.put($S, $L)", this.types.fieldName(propertySpec), listVar);
            } else {
                if (propertySpec.typeSpec().typeKind().equals(TypeKind.ENUM)) {
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
