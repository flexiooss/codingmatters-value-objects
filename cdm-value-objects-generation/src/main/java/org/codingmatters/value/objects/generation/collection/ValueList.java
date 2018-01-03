package org.codingmatters.value.objects.generation.collection;

import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.util.Collection;
import java.util.stream.Stream;

/**
 * Created by nelt on 10/11/16.
 */
public class ValueList {

    private final String packageName;

    public ValueList(String packageName) {
        this.packageName = packageName;
    }

    public TypeSpec type() {

        ClassName valueCollectionInterface = ClassName.get(this.packageName, "ValueList");
        ClassName valueCollectionImpl = ClassName.get(this.packageName, "ValueListImpl");

        return TypeSpec.interfaceBuilder("ValueList")
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(Iterable.class), TypeVariableName.get("E")))
                .addModifiers(Modifier.PUBLIC)
                .addTypeVariable(TypeVariableName.get("E"))
                .addMethod(MethodSpec.methodBuilder("contains")
                        .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                        .addParameter(TypeName.OBJECT, "o")
                        .returns(TypeName.BOOLEAN)
                        .build())
                .addMethod(MethodSpec.methodBuilder("containsAll")
                        .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                        .addParameter(ParameterizedTypeName.get(ClassName.get(Collection.class), TypeVariableName.get("?")), "c")
                        .returns(TypeName.BOOLEAN)
                        .build())
                .addMethod(MethodSpec.methodBuilder("get")
                        .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                        .addParameter(TypeName.INT, "index")
                        .returns(TypeVariableName.get("E"))
                        .build())
                .addMethod(MethodSpec.methodBuilder("size")
                        .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                        .returns(TypeName.INT)
                        .build())
                .addMethod(MethodSpec.methodBuilder("indexOf")
                        .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                        .addParameter(Object.class, "o")
                        .returns(TypeName.INT)
                        .build())
                .addMethod(MethodSpec.methodBuilder("isEmpty")
                        .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                        .returns(TypeName.BOOLEAN)
                        .build())
                .addMethod(MethodSpec.methodBuilder("toArray")
                        .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                        .addTypeVariable(TypeVariableName.get("T"))
                        .addParameter(ArrayTypeName.of(TypeVariableName.get("T")), "a")
                        .returns(ArrayTypeName.of(TypeVariableName.get("T")))
                        .build())
                //Object[] toArray()
                .addMethod(MethodSpec.methodBuilder("toArray")
                        .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                        .returns(Object[].class)
                        .build())
                .addMethod(MethodSpec.methodBuilder("stream")
                        .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                        .returns(ParameterizedTypeName.get(ClassName.get(Stream.class), TypeVariableName.get("E")))
                        .build())

                .addType(new CollectionBuilder(
                        ClassName.get(this.packageName, "ValueList"),
                        ClassName.get(this.packageName, "ValueListImpl")
                        ).type()
                )
                .build();
    }

}
