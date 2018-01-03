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

import java.util.Arrays;

import static org.codingmatters.tests.reflect.ReflectMatchers.aPackagePrivate;
import static org.codingmatters.tests.reflect.ReflectMatchers.aPublic;
import static org.codingmatters.value.objects.spec.PropertySpec.property;
import static org.codingmatters.value.objects.spec.PropertyTypeSpec.type;
import static org.codingmatters.value.objects.spec.Spec.spec;
import static org.codingmatters.value.objects.spec.ValueSpec.valueSpec;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 9/25/16.
 */
public class ValueEqualityTest {

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
                                .named("equals")
                                .withParameters(Object.class)
                                .returning(boolean.class)
                        )
                )
        );
    }

    @Test
    public void equalsToSelf() throws Exception {
        ObjectHelper value = classes.get("org.generated.Val").call("builder")
                .call("prop1", String.class).with("v1")
                .call("prop2", String.class).with("v2")
                .call("build");

        assertThat(value.get(), is(value.get()));
    }

    @Test
    public void equalsWhenAllPropertiesAreEqual() throws Exception {
        ObjectHelper value = classes.get("org.generated.Val").call("builder")
                .call("prop1", String.class).with("v1")
                .call("prop2", String.class).with("v2")
                .call("build");

        ObjectHelper otherValue = classes.get("org.generated.Val").call("builder")
                .call("prop1", String.class).with("v1")
                .call("prop2", String.class).with("v2")
                .call("build");

        assertThat(value.get(), is(otherValue.get()));
    }

    @Test
    public void notEqualsWhenOnePropertyIsNotEqual() throws Exception {
        ObjectHelper value = classes.get("org.generated.Val").call("builder")
                .call("prop1", String.class).with("v1")
                .call("prop2", String.class).with("v2")
                .call("build");

        ObjectHelper otherValue = classes.get("org.generated.Val").call("builder")
                .call("prop1", String.class).with("different")
                .call("prop2", String.class).with("v2")
                .call("build");

        assertThat(value.get(), is(not(otherValue.get())));
    }

    @Test
    public void noPropertyValue() throws Exception {
        ObjectHelper aValue = classes.get("org.generated.NoPropertyVal").call("builder")
                .call("build");

        ObjectHelper anotherValue = classes.get("org.generated.NoPropertyVal").call("builder")
                .call("build");

        assertThat(aValue.get(), is(anotherValue.get()));
    }

    @Test
    public void deepEqualityOnComplexValue() throws Exception {
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

        assertThat(complexValue.get(), is(sameComplexValue.get()));
    }

    @Test
    public void equalityWithBinary() throws Exception {
        this.fileHelper.printFile(this.dir.getRoot(), "ValImpl.java");

        ObjectHelper value = classes.get("org.generated.Val").call("builder")
                .call("binary", byte[].class).with("v1".getBytes())
                .call("build");
        ObjectHelper anotherValue = classes.get("org.generated.Val").call("builder")
                .call("binary", byte[].class).with("v1".getBytes())
                .call("build");

        assertThat(value.get(), is(anotherValue.get()));

    }

    class Toto {
        private byte[] bs;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Toto toto = (Toto) o;

            return Arrays.equals(bs, toto.bs);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(bs);
        }

        @Override
        public String toString() {
            return "Toto{" +
                    "bs=" + Arrays.toString(bs) +
                    '}';
        }
    }
}
