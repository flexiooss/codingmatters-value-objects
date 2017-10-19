package org.codingmatters.value.objects.generation.collection;

import com.squareup.javapoet.*;
import org.codingmatters.value.objects.generation.OptionalHelper;

import javax.lang.model.element.Modifier;
import java.util.Optional;

public class OptionalCollectionHelper {

    private final OptionalHelper optionalHelper;
    private final ClassName valueCollection;
    private final TypeName optionalCollection;
    private final ClassName optionalCollectionRawType;

    public OptionalCollectionHelper(ClassName valueCollection) {
        this.valueCollection = valueCollection;
        this.optionalCollectionRawType = ClassName.get(
                valueCollection.packageName() + ".optional",
                "Optional" + valueCollection.simpleName()
        );
        this.optionalCollection = ParameterizedTypeName.get(
                this.optionalCollectionRawType,
                TypeVariableName.get("E")
        );
        this.optionalHelper = new OptionalHelper();
    }

    public TypeSpec.Builder baseOptionalCass() {
        TypeSpec.Builder result = TypeSpec.classBuilder(this.optionalCollectionRawType.simpleName())
                .addModifiers(Modifier.PUBLIC)
                .addTypeVariable(TypeVariableName.get("E"))
                .addField(FieldSpec.builder(
                        ParameterizedTypeName.get(ClassName.get(Optional.class), ParameterizedTypeName.get(this.valueCollection, TypeVariableName.get("E"))),
                        "optional")
                        .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                        .build())
                .addMethods(this.optionalHelper.optionalMethods(ParameterizedTypeName.get(this.valueCollection, TypeVariableName.get("E"))))
                ;

        return result;
    }

    public ClassName valueCollection() {
        return valueCollection;
    }
}
