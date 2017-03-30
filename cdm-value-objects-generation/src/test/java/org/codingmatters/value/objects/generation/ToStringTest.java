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
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 9/27/16.
 */
public class ToStringTest {

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
        assertThat(compiled.getClass("org.generated.ValImpl"),
                is(aPackagePrivate().class_()
                        .with(aPublic().method()
                                .named("toString")
                                .withoutParameters()
                                .returning(String.class)
                        )
                )
        );
    }

    @Test
    public void simple() throws Exception {
        Object aBuilder = compiled.onClass("org.generated.Val$Builder").invoke("builder");
        compiled.on(aBuilder).invoke("prop1", String.class).with("v1");
        compiled.on(aBuilder).invoke("prop2", String.class).with("v2");
        Object aValue = compiled.on(aBuilder).invoke("build");

        assertThat(aValue.toString(), is("Val{prop1=v1, prop2=v2}"));
    }

    @Test
    public void simpleWithNulls() throws Exception {
        Object aBuilder = compiled.onClass("org.generated.Val$Builder").invoke("builder");
        compiled.on(aBuilder).invoke("prop1", String.class).with("v1");
        Object aValue = compiled.on(aBuilder).invoke("build");

        assertThat(aValue.toString(), is("Val{prop1=v1, prop2=null}"));
    }

    @Test
    public void complex() throws Exception {
        Object builder = compiled.onClass("org.generated.Val$Builder").invoke("builder");
        compiled.on(builder).invoke("prop1", String.class).with("v1");
        compiled.on(builder).invoke("prop2", String.class).with("v2");
        Object builded = compiled.on(builder).invoke("build");

        Object complexBuilder = compiled.onClass("org.generated.ComplexVal$Builder").invoke("builder");
        compiled.on(complexBuilder).invoke("prop", compiled.getClass("org.generated.Val")).with(builded);
        Object complexValue = compiled.on(complexBuilder).invoke("build");

        assertThat(complexValue.toString(), is("ComplexVal{prop=Val{prop1=v1, prop2=v2}}"));
    }

    @Test
    public void noPropertyValue() throws Exception {
        Object aBuilder = compiled.onClass("org.generated.NoPropertyVal$Builder").invoke("builder");
        Object aValue = compiled.on(aBuilder).invoke("build");

        assertThat(aValue.toString(), is("NoPropertyVal{}"));
    }
}
