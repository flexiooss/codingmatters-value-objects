package org.codingmatters.value.objects.generation;

import org.codingmatters.tests.compile.CompiledCode;
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

    private final Spec spec = spec()
            .addValue(valueSpec().name("val")
                    .addProperty(property().name("prop1").type(type().typeRef(String.class.getName()).typeKind(TypeKind.JAVA_TYPE)))
                    .addProperty(property().name("prop2").type(type().typeRef(String.class.getName()).typeKind(TypeKind.JAVA_TYPE)))
            )
            .addValue(valueSpec().name("noPropertyVal"))
            .addValue(valueSpec().name("complexVal")
                    .addProperty(property().name("prop").type(type().typeRef("val").typeKind(TypeKind.IN_SPEC_VALUE_OBJECT)))
            )
            .build();
    private CompiledCode compiled;


    @Before
    public void setUp() throws Exception {
        new SpecCodeGenerator(this.spec, "org.generated", dir.getRoot()).generate();
        this.compiled = CompiledCode.compile(this.dir.getRoot());
    }

    @Test
    public void signature() throws Exception {
        assertThat(compiled.getClass("org.generated.ValImpl"),
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
        Object builder = compiled.onClass("org.generated.Val$Builder").invoke("builder");
        compiled.on(builder).invoke("prop1", String.class).with("v1");
        compiled.on(builder).invoke("prop2", String.class).with("v2");
        Object value = compiled.on(builder).invoke("build");

        int hash1 = compiled.on(value).castedTo("org.generated.Val").invoke("hashCode");
        int hash2 = compiled.on(value).castedTo("org.generated.Val").invoke("hashCode");

        assertEquals(hash1, hash2);
    }

    @Test
    public void hashCodeEqualsWhenSameValue() throws Exception {
        Object aBuilder = compiled.onClass("org.generated.Val$Builder").invoke("builder");
        compiled.on(aBuilder).invoke("prop1", String.class).with("v");
        Object aValue = compiled.on(aBuilder).invoke("build");

        Object anotherBuilder = compiled.onClass("org.generated.Val$Builder").invoke("builder");
        compiled.on(anotherBuilder).invoke("prop1", String.class).with("v");
        Object anotherValue = compiled.on(anotherBuilder).invoke("build");

        assertThat(aValue.hashCode(), is(anotherValue.hashCode()));
    }

    @Test
    public void hashCodeDifferentWhenDifferentValue() throws Exception {
        Object aBuilder = compiled.onClass("org.generated.Val$Builder").invoke("builder");
        compiled.on(aBuilder).invoke("prop1", String.class).with("v1");
        Object aValue = compiled.on(aBuilder).invoke("build");

        Object anotherBuilder = compiled.onClass("org.generated.Val$Builder").invoke("builder");
        compiled.on(anotherBuilder).invoke("prop1", String.class).with("v2");
        Object anotherValue = compiled.on(anotherBuilder).invoke("build");

        assertThat(aValue.hashCode(), is(not(anotherValue.hashCode())));
    }

    @Test
    public void noPropertyValueHash() throws Exception {
        Object aBuilder = compiled.onClass("org.generated.NoPropertyVal$Builder").invoke("builder");
        Object aValue = compiled.on(aBuilder).invoke("build");

        Object anotherBuilder = compiled.onClass("org.generated.NoPropertyVal$Builder").invoke("builder");
        Object anotherValue = compiled.on(anotherBuilder).invoke("build");

        assertThat(aValue.hashCode(), is(anotherValue.hashCode()));
    }

    @Test
    public void complexValue() throws Exception {
        Object builder = compiled.onClass("org.generated.Val$Builder").invoke("builder");
        compiled.on(builder).invoke("prop1", String.class).with("v1");
        compiled.on(builder).invoke("prop2", String.class).with("v2");
        Object builded = compiled.on(builder).invoke("build");

        Object complexBuilder = compiled.onClass("org.generated.ComplexVal$Builder").invoke("builder");
        compiled.on(complexBuilder).invoke("prop", compiled.getClass("org.generated.Val")).with(builded);
        Object complexValue = compiled.on(complexBuilder).invoke("build");

        Object sameComplexBuilder = compiled.onClass("org.generated.ComplexVal$Builder").invoke("builder");
        compiled.on(sameComplexBuilder).invoke("prop", compiled.getClass("org.generated.Val")).with(builded);
        Object sameComplexValue = compiled.on(sameComplexBuilder).invoke("build");

        assertThat(complexValue.hashCode(), is(sameComplexValue.hashCode()));
    }
}
