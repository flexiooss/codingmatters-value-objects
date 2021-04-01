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
        return TypeSpec.interfaceBuilder("ValueList")
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(Iterable.class), TypeVariableName.get("E")))
                .addModifiers(Modifier.PUBLIC)
                .addTypeVariable(TypeVariableName.get("E"))
                .addMethod(MethodSpec.methodBuilder("builder")
                        .addTypeVariable(TypeVariableName.get("E"))
                        .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                        .returns(ParameterizedTypeName.get(ClassName.bestGuess("Builder"), TypeVariableName.get("E")))
                        .addStatement("return new ValueList.Builder<E>()")
                        .build())
                .addMethod(MethodSpec.methodBuilder("from")
                        .addParameter(ParameterizedTypeName.get(ClassName.get(Iterable.class), TypeVariableName.get("E")), "elements")
                        .addTypeVariable(TypeVariableName.get("E"))
                        .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                        .returns(ParameterizedTypeName.get(ClassName.bestGuess("Builder"), TypeVariableName.get("E")))
                        .addStatement("return new ValueList.Builder<E>().with(elements)")
                        .build())
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
                .addMethod(MethodSpec.methodBuilder("to")
                        .addModifiers(Modifier.DEFAULT, Modifier.PUBLIC)
                        .returns(ParameterizedTypeName.get(ClassName.bestGuess("Builder"), TypeVariableName.get("E")))
                        .addStatement("return from(this)")
                        .build())

                .addType(new CollectionBuilder(
                        ClassName.get(this.packageName, "ValueList"),
                        ClassName.get(this.packageName, "ValueListImpl")
                        ).type()
                )
                .addType(TypeSpec.interfaceBuilder("Changer")
                        .addTypeVariable(TypeVariableName.get("E"))
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addMethod(MethodSpec.methodBuilder("configure")
                                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                                .addParameter(ParameterizedTypeName.get(ClassName.bestGuess("Builder"), TypeVariableName.get("E")), "builder")
                                .returns(ParameterizedTypeName.get(ClassName.bestGuess("Builder"), TypeVariableName.get("E")))
                                .build())
                        .build()
                )
                .build();
    }

}
