package org.codingmatters.value.objects.generation.collection;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import org.codingmatters.tests.compile.CompiledCode;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.ArrayList;

import static org.codingmatters.tests.reflect.ReflectMatchers.aPackagePrivate;
import static org.codingmatters.tests.reflect.ReflectMatchers.variableType;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 11/11/16.
 */
public class ValueListImplementationTest {

    @Rule
    public TemporaryFolder dir = new TemporaryFolder();
    private CompiledCode compiled;

    @Before
    public void setUp() throws Exception {
        String packageName = "org.generated";
        File dest = this.dir.newFolder();
        TypeSpec valueListType = new ValueList(packageName).type();
        TypeSpec valueListImplementation = new ValueListImplementation(packageName, valueListType).type();

        JavaFile.builder(packageName, valueListType)
                .build()
                .writeTo(dest);

        JavaFile javaFile = JavaFile.builder(packageName, valueListImplementation)
                .build();
        javaFile
                .writeTo(dest);

        javaFile.writeTo(System.out);

        this.compiled = CompiledCode.compile(dest);
    }

    @Test
    public void name() throws Exception {
        assertThat(
                compiled.getClass("org.generated.ValueListImpl"),
                is(
                        aPackagePrivate().class_()
                                .extending(ArrayList.class)
                                .withParameter(variableType().named("E"))
                                .implementing(compiled.getClass("org.generated.ValueList"))
                )
        );


    }
}