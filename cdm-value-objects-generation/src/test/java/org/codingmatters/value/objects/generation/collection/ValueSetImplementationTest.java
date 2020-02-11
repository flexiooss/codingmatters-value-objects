package org.codingmatters.value.objects.generation.collection;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import org.codingmatters.tests.compile.CompiledCode;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashSet;

import static org.codingmatters.tests.reflect.ReflectMatchers.*;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 11/19/16.
 */
public class ValueSetImplementationTest {

    @Rule
    public TemporaryFolder dir = new TemporaryFolder();
    private CompiledCode compiled;

    @Before
    public void setUp() throws Exception {
        String packageName = "org.generated";
        File dest = this.dir.newFolder();
        TypeSpec valueSetType = new ValueSet(packageName).type();
        TypeSpec valueSetImplementation = new ValueSetImplementation(packageName, valueSetType).type();

        JavaFile.builder(packageName, valueSetType)
                .build()
                .writeTo(dest);

        JavaFile javaFile = JavaFile.builder(packageName, valueSetImplementation)
                .build();
        javaFile
                .writeTo(dest);

        this.compiled = CompiledCode.builder().source(dest).compile();
    }

    @Test
    public void extendsHashSet() throws Exception {
        assertThat(
                compiled.getClass("org.generated.ValueSetImpl"),
                is(
                        aPackagePrivate().class_()
                                .extending(HashSet.class)
                                .withParameter(variableType().named("E"))
                                .implementing(compiled.getClass("org.generated.ValueSet"))
                )
        );
    }

    @Test
    public void constructorFromArray() throws Exception {
        assertThat(
                compiled.getClass("org.generated.ValueSetImpl"),
                is(
                        aPackagePrivate().class_().with(
                                aConstructor().withParameters(typeArray(variableType().named("E")))
                        )
                )
        );

        Constructor constr = compiled.getClass("org.generated.ValueSetImpl")
                .getConstructor(new Class[]{Object[].class});;
        constr.setAccessible(true);
        Object list = constr.newInstance(new Object[]{new Object[]{"a", "b", "a"}});

        assertThat(this.compiled.on(list).invoke("toArray"), is(new Object [] {"a", "b"}));
    }

    @Test
    public void constructorFromCollection() throws Exception {
        assertThat(
                compiled.getClass("org.generated.ValueSetImpl"),
                is(
                        aPackagePrivate().class_().with(
                                aConstructor().withParameters(genericType().baseClass(Collection.class).withParameters(typeParameter().named("E")))
                        )
                )
        );
    }
}
