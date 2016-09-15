package org.codingmatters.value.objects;

import org.codingmatters.tests.compile.CompiledCode;
import org.codingmatters.value.objects.spec.Spec;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.codingmatters.tests.reflect.ReflectMatchers.*;
import static org.codingmatters.value.objects.spec.Spec.spec;
import static org.codingmatters.value.objects.spec.ValueSpec.valueSpec;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 9/13/16.
 */
@SuppressWarnings("unchecked")
public class ValueSpecGenerationTest {

    @Rule
    public TemporaryFolder dir = new TemporaryFolder();

    private final Spec spec  = spec()
            .addValue(
                    valueSpec().name("val")
            )
            .addValue(
                    valueSpec().name("val2")
            )
            .build();
    private CompiledCode compiled;

    @Before
    public void setUp() throws Exception {
        new SpecCodeGenerator(this.spec, "org.generated").generateTo(dir.getRoot());
        this.compiled = CompiledCode.compile(this.dir.getRoot());
    }

    @Test
    public void twoValueSpec_twoInterfacesTwoBuilderTwoImpl() throws Exception {
        assertThat(compiled.getClass("org.generated.Val"), is(anInterface()));
        assertThat(compiled.getClass("org.generated.Val$Builder"), is(aClass()));
        assertThat(compiled.getClass("org.generated.ValImpl"), is(aClass()));

        assertThat(compiled.getClass("org.generated.Val2"), is(anInterface()));
        assertThat(compiled.getClass("org.generated.Val2$Builder"), is(aClass()));
        assertThat(compiled.getClass("org.generated.Val2Impl"), is(aClass()));
    }

    @Test
    public void valueInterface() throws Exception {
        assertThat(compiled.getClass("org.generated.Val"), is(anInterface().public_()));
    }

    @Test
    public void valueBuilder() throws Exception {
        assertThat(compiled.getClass("org.generated.Val$Builder"), is(
                aClass().public_().static_()
                    .with(aStaticMethod().named("builder")
                            .public_()
                            .withParameters()
                            .returning(compiled.getClass("org.generated.Val$Builder"))
                    )
                    .with(anInstanceMethod().named("build")
                            .public_()
                            .withParameters()
                            .returning(compiled.getClass("org.generated.Val"))
                    )
        ));
    }

    @Test
    public void valueBuilder_builder_returnsABuilder() throws Exception {
        Object builder = compiled.onClass("org.generated.Val$Builder").invoke("builder");

        assertThat(builder, is(notNullValue(compiled.getClass("org.generated.Val$Builder"))));
    }

    @Test
    public void valueBuilder_build_returnsAnImplementationInstance() throws Exception {
        Object builder = compiled.onClass("org.generated.Val$Builder").invoke("builder");
        Object value = compiled.on(builder).invoke("build");

        assertThat(value, is(notNullValue(compiled.getClass("org.generated.ValImpl"))));
    }

    @Test
    public void valueImplementationClass() throws Exception {
        assertThat(compiled.getClass("org.generated.ValImpl"),is(
                aClass().packagePrivate()
                        .implementing(compiled.getClass("org.generated.Val"))
        ));
    }
}