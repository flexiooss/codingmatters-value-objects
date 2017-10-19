package org.codingmatters.value.objects.generation.collection;

import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.util.Optional;
import java.util.function.Function;

public class OptionalValueList {
    private final String packageName;
    private final TypeSpec valueListInterface;
    private final OptionalCollectionHelper optionalCollectionHelper;

    public OptionalValueList(String packageName, TypeSpec valueListInterface) {
        this.packageName = packageName;
        this.valueListInterface = valueListInterface;
        this.optionalCollectionHelper = new OptionalCollectionHelper(ClassName.get(this.packageName, valueListInterface.name));
    }

    public TypeSpec type() {
        return this.optionalCollectionHelper.baseOptionalCass()
                .addField(
                        ParameterizedTypeName.get(ClassName.get(Function.class), TypeVariableName.get("E"), TypeVariableName.get("O")),
                        "createOptional",
                        Modifier.PRIVATE)
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(this.optionalCollectionHelper.valueCollection(), "elements")
                        .addParameter(
                                ParameterizedTypeName.get(ClassName.get(Function.class), TypeVariableName.get("E"), TypeVariableName.get("O")),
                                "createOptional"
                        )
                        .addStatement("this.optional = $T.ofNullable(elements)", Optional.class)
                        .addStatement("this.createOptional = createOptional")
                        .build())
                .addTypeVariable(TypeVariableName.get("O"))
                .addMethod(MethodSpec.methodBuilder("get")
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(TypeName.INT, "index")
                    .returns(TypeVariableName.get("O"))
                    .beginControlFlow("if(this.optional.isPresent())")
                        .addStatement("return this.createOptional.apply(this.optional.get().size() > index ? this.optional.get().get(index) : null)")
                    .nextControlFlow("else")
                        .addStatement("return this.createOptional.apply(null)")
                    .endControlFlow()
                    .build())
                .build();
    }
}
