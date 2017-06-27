package org.codingmatters.value.objects.generation;

import org.codingmatters.tests.compile.CompiledCode;
import org.codingmatters.value.objects.spec.Spec;
import org.codingmatters.value.objects.spec.TypeKind;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.codingmatters.tests.reflect.ReflectMatchers.aStatic;
import static org.codingmatters.tests.reflect.ReflectMatchers.anInterface;
import static org.codingmatters.value.objects.spec.PropertySpec.property;
import static org.codingmatters.value.objects.spec.PropertyTypeSpec.type;
import static org.codingmatters.value.objects.spec.Spec.spec;
import static org.codingmatters.value.objects.spec.ValueSpec.valueSpec;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 9/27/16.
 */
public class BuilderFromTest {

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
        this.compiled = CompiledCode.builder().source(this.dir.getRoot()).compile();
    }

    @Test
    public void signature() throws Exception {
        assertThat(
                compiled.getClass("org.generated.Val").getDeclaredMethod("from", compiled.getClass("org.generated.Val")),
                is(aStatic().public_().method()
                        .named("from")
                        .withParameters(compiled.getClass("org.generated.Val"))
                        .returning(compiled.getClass("org.generated.Val$Builder"))
                )
        );

        assertThat(compiled.getClass("org.generated.Val"),
                is(anInterface()
                        .with(
                                aStatic().public_().method()
                                        .named("from")
                                        .withParameters(compiled.getClass("org.generated.Val"))
                                        .returning(compiled.getClass("org.generated.Val$Builder"))
                        )
                )
        );
    }

    @Test
    public void simpleValue() throws Exception {
        Object aBuilder = compiled.onClass("org.generated.Val").invoke("builder");
        compiled.on(aBuilder).invoke("prop1", String.class).with("v1");
        compiled.on(aBuilder).invoke("prop2", String.class).with("v2");
        Object value = compiled.on(aBuilder).invoke("build");

        Object anotherBuilder = compiled.onClass("org.generated.Val")
                .invoke("from", compiled.getClass("org.generated.Val")).with(value);

        assertThat(compiled.on(anotherBuilder).invoke("build"), is(value));
    }

    @Test
    public void simpleValueWithNulls() throws Exception {
        Object aBuilder = compiled.onClass("org.generated.Val").invoke("builder");
        compiled.on(aBuilder).invoke("prop1", String.class).with(new Object[] {null});
        compiled.on(aBuilder).invoke("prop2", String.class).with(new Object[] {null});
        Object value = compiled.on(aBuilder).invoke("build");

        Object anotherBuilder = compiled.onClass("org.generated.Val")
                .invoke("from", compiled.getClass("org.generated.Val")).with(value);

        assertThat(compiled.on(anotherBuilder).invoke("build"), is(value));
    }

    @Test
    public void complex() throws Exception {
        Object builder = compiled.onClass("org.generated.Val").invoke("builder");
        compiled.on(builder).invoke("prop1", String.class).with("v1");
        compiled.on(builder).invoke("prop2", String.class).with("v2");
        Object builded = compiled.on(builder).invoke("build");

        Object complexBuilder = compiled.onClass("org.generated.ComplexVal").invoke("builder");
        compiled.on(complexBuilder).invoke("prop", compiled.getClass("org.generated.Val")).with(builded);
        Object complexValue = compiled.on(complexBuilder).invoke("build");


        Object anotherBuilder = compiled.onClass("org.generated.ComplexVal")
                .invoke("from", compiled.getClass("org.generated.ComplexVal")).with(complexValue);

        assertThat(compiled.on(anotherBuilder).invoke("build"), is(complexValue));
    }

    @Test
    public void complexWithNulls() throws Exception {
        Object complexBuilder = compiled.onClass("org.generated.ComplexVal").invoke("builder");
        compiled.on(complexBuilder).invoke("prop", compiled.getClass("org.generated.Val")).with(new Object[] {null});
        Object complexValue = compiled.on(complexBuilder).invoke("build");


        Object anotherBuilder = compiled.onClass("org.generated.ComplexVal")
                .invoke("from", compiled.getClass("org.generated.ComplexVal")).with(complexValue);

        assertThat(compiled.on(anotherBuilder).invoke("build"), is(complexValue));
    }

    @Test
    public void noPropertyValue() throws Exception {
        Object aBuilder = compiled.onClass("org.generated.NoPropertyVal").invoke("builder");
        Object aValue = compiled.on(aBuilder).invoke("build");

        Object anotherBuilder = compiled.onClass("org.generated.NoPropertyVal")
                .invoke("from", compiled.getClass("org.generated.NoPropertyVal")).with(aValue);

        assertThat(compiled.on(anotherBuilder).invoke("build"), is(aValue));
    }
}
