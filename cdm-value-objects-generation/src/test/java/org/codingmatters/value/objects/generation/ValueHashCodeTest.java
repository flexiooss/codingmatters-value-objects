package org.codingmatters.value.objects.generation;

import org.codingmatters.tests.compile.CompiledCode;
import org.codingmatters.tests.compile.FileHelper;
import org.codingmatters.tests.compile.helpers.ClassLoaderHelper;
import org.codingmatters.tests.compile.helpers.helpers.ObjectHelper;
import org.codingmatters.value.objects.spec.Spec;
import org.codingmatters.value.objects.spec.TypeKind;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.codingmatters.tests.reflect.ReflectMatchers.aPackagePrivate;
import static org.codingmatters.tests.reflect.ReflectMatchers.aPublic;
import static org.codingmatters.value.objects.spec.PropertySpec.property;
import static org.codingmatters.value.objects.spec.PropertyTypeSpec.type;
import static org.codingmatters.value.objects.spec.Spec.spec;
import static org.codingmatters.value.objects.spec.ValueSpec.valueSpec;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 9/27/16.
 */
public class ValueHashCodeTest {

    @Rule
    public TemporaryFolder dir = new TemporaryFolder();

    @Rule
    public FileHelper fileHelper = new FileHelper();

    private final Spec spec = spec()
            .addValue(valueSpec().name("val")
                    .addProperty(property().name("prop1").type(type().typeRef(String.class.getName()).typeKind(TypeKind.JAVA_TYPE)))
                    .addProperty(property().name("prop2").type(type().typeRef(String.class.getName()).typeKind(TypeKind.JAVA_TYPE)))
                    .addProperty(property().name("binary").type(type().typeRef(byte[].class.getName()).typeKind(TypeKind.JAVA_TYPE)))
            )
            .addValue(valueSpec().name("noPropertyVal"))
            .addValue(valueSpec().name("complexVal")
                    .addProperty(property().name("prop").type(type().typeRef("val").typeKind(TypeKind.IN_SPEC_VALUE_OBJECT)))
            )
            .build();

    private ClassLoaderHelper classes;

    @Before
    public void setUp() throws Exception {
        new SpecCodeGenerator(this.spec, "org.generated", dir.getRoot()).generate();
        this.classes = CompiledCode.builder().source(this.dir.getRoot()).compile().classLoader();
    }

    @Test
    public void signature() throws Exception {
        assertThat(classes.get("org.generated.ValImpl").get(),
                is(aPackagePrivate().class_()
                        .with(aPublic().method()
                                .named("hashCode")
                                .withoutParameters()
                                .returning(int.class)
                        )
                )
        );
    }

    @Test
    public void hashCodeIsStable() throws Exception {
        ObjectHelper value = classes.get("org.generated.Val").call("builder")
                .call("prop1", String.class).with("v1")
                .call("binary", byte[].class).with("binary".getBytes())
                .call("build");

        int hash1 = (int) value.as("org.generated.Val").call("hashCode").get();
        int hash2 = (int) value.as("org.generated.Val").call("hashCode").get();

        assertEquals(hash1, hash2);
    }

    @Test
    public void hashCodeEqualsWhenSameValue() throws Exception {
        this.fileHelper.printFile(this.dir.getRoot(), "ValImpl.java");

        ObjectHelper aValue = classes.get("org.generated.Val").call("builder")
                .call("prop1", String.class).with("v")
                .call("binary", byte[].class).with("binary".getBytes())
                .call("build");

        ObjectHelper anotherValue = classes.get("org.generated.Val").call("builder")
                .call("prop1", String.class).with("v")
                .call("binary", byte[].class).with("binary".getBytes())
                .call("build");

        System.out.println(aValue.get());
        System.out.println(anotherValue.get());

        assertThat(aValue.get().hashCode(), is(anotherValue.get().hashCode()));
    }

    @Test
    public void hashCodeDifferentWhenDifferentValue() throws Exception {
        ObjectHelper aValue = classes.get("org.generated.Val").call("builder")
                .call("prop1", String.class).with("v1")
                .call("binary", byte[].class).with("a value".getBytes())
                .call("build");

        ObjectHelper anotherValue = classes.get("org.generated.Val").call("builder")
                .call("prop1", String.class).with("v2")
                .call("binary", byte[].class).with("not the same".getBytes())
                .call("build");

        assertThat(aValue.get().hashCode(), is(not(anotherValue.get().hashCode())));
    }

    @Test
    public void noPropertyValueHash() throws Exception {
        ObjectHelper aValue = classes.get("org.generated.NoPropertyVal").call("builder")
                .call("build");

        ObjectHelper anotherValue = classes.get("org.generated.NoPropertyVal").call("builder")
                .call("build");

        assertThat(aValue.get().hashCode(), is(anotherValue.get().hashCode()));
    }

    @Test
    public void complexValue() throws Exception {
        ObjectHelper builded = classes.get("org.generated.Val").call("builder")
                .call("prop1", String.class).with("v1")
                .call("prop2", String.class).with("v2")
                .call("build");

        ObjectHelper complexValue = classes.get("org.generated.ComplexVal").call("builder")
                .call("prop", classes.get("org.generated.Val").get()).with(builded.get())
                .call("build");

        ObjectHelper sameComplexValue = classes.get("org.generated.ComplexVal").call("builder")
                .call("prop", classes.get("org.generated.Val").get()).with(builded.get())
                .call("build");

        assertThat(complexValue.get().hashCode(), is(sameComplexValue.get().hashCode()));
    }
}
