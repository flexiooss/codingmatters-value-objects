package org.codingmatters.value.objects.generation;

import org.codingmatters.tests.compile.CompiledCode;
import org.codingmatters.value.objects.spec.Spec;
import org.codingmatters.value.objects.spec.TypeKind;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.codingmatters.tests.reflect.ReflectMatchers.aClass;
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

    private final Spec spec = spec()
            .addValue(valueSpec().name("val")
                    .addProperty(property().name("prop1").type(type().typeRef(String.class.getName()).typeKind(TypeKind.JAVA_TYPE)))
                    .addProperty(property().name("prop2").type(type().typeRef(String.class.getName()).typeKind(TypeKind.JAVA_TYPE)))
            )
            .addValue(valueSpec().name("complexVal")
                    .addProperty(property().name("prop").type(type().typeRef("val").typeKind(TypeKind.IN_SPEC_VALUE_OBJECT)))
            )
            .build();
    private CompiledCode compiled;


    @Before
    public void setUp() throws Exception {
        new SpecCodeGenerator(this.spec, "org.generated").generateTo(dir.getRoot());
        this.compiled = CompiledCode.compile(this.dir.getRoot());
    }

    @Test
    public void signature() throws Exception {
        assertThat(compiled.getClass("org.generated.ValImpl"),
                is(aClass()
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
        Object builder = compiled.onClass("org.generated.Val$Builder").invoke("builder");
        compiled.on(builder).invoke("prop1", String.class).with("v1");
        compiled.on(builder).invoke("prop2", String.class).with("v2");
        Object value = compiled.on(builder).invoke("build");

        assertThat(value, is(value));
    }

    @Test
    public void equalsWhenAllPropertiesAreEqual() throws Exception {
        Object builder = compiled.onClass("org.generated.Val$Builder").invoke("builder");
        compiled.on(builder).invoke("prop1", String.class).with("v1");
        compiled.on(builder).invoke("prop2", String.class).with("v2");
        Object value = compiled.on(builder).invoke("build");

        Object otherBuilder = compiled.onClass("org.generated.Val$Builder").invoke("builder");
        compiled.on(otherBuilder).invoke("prop1", String.class).with("v1");
        compiled.on(otherBuilder).invoke("prop2", String.class).with("v2");
        Object otherValue = compiled.on(otherBuilder).invoke("build");

        assertThat(value, is(otherValue));
    }

    @Test
    public void notEqualsWhenOnePropertyIsNotEqual() throws Exception {
        Object builder = compiled.onClass("org.generated.Val$Builder").invoke("builder");
        compiled.on(builder).invoke("prop1", String.class).with("v1");
        compiled.on(builder).invoke("prop2", String.class).with("v2");
        Object value = compiled.on(builder).invoke("build");

        Object otherBuilder = compiled.onClass("org.generated.Val$Builder").invoke("builder");
        compiled.on(otherBuilder).invoke("prop1", String.class).with("different");
        compiled.on(otherBuilder).invoke("prop2", String.class).with("v2");
        Object otherValue = compiled.on(otherBuilder).invoke("build");

        assertThat(value, is(not(otherValue)));
    }

    @Test
    public void deepEqualityOnComplexValue() throws Exception {
        Object builder = compiled.onClass("org.generated.Val$Builder").invoke("builder");
        compiled.on(builder).invoke("prop1", String.class).with("v1");
        compiled.on(builder).invoke("prop2", String.class).with("v2");

        Object complexBuilder = compiled.onClass("org.generated.ComplexVal$Builder").invoke("builder");
        compiled.on(complexBuilder).invoke("prop", compiled.getClass("org.generated.Val$Builder")).with(builder);
        Object complexValue = compiled.on(complexBuilder).invoke("build");

        Object anotherComplexBuilder = compiled.onClass("org.generated.ComplexVal$Builder").invoke("builder");
        compiled.on(anotherComplexBuilder).invoke("prop", compiled.getClass("org.generated.Val$Builder")).with(builder);
        Object anotherComplexValue = compiled.on(anotherComplexBuilder).invoke("build");

        assertThat(complexValue, is(anotherComplexValue));
    }
}
