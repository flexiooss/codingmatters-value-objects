package org.codingmatters.value.objects;

import org.codingmatters.tests.compile.CompiledCode;
import org.codingmatters.value.objects.spec.Spec;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.codingmatters.tests.reflect.ReflectMatchers.aClass;
import static org.codingmatters.tests.reflect.ReflectMatchers.anInterface;
import static org.codingmatters.value.objects.spec.Spec.spec;
import static org.codingmatters.value.objects.spec.ValueSpec.valueSpec;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 9/13/16.
 */
public class SpecCodeGeneratorTest {

    @Rule
    public TemporaryFolder dir = new TemporaryFolder();

    private final Spec spec  = spec()
            .addValue(
                    valueSpec().name("val")
            )
            .build();
    private CompiledCode compiled;

    @Before
    public void setUp() throws Exception {
        new SpecCodeGenerator(this.spec, "org.generated").generateTo(dir.getRoot());
        this.compiled = CompiledCode.compile(this.dir.getRoot());
    }

    @Test
    public void valueInterface() throws Exception {
        assertThat(compiled.getClass("org.generated.Val"), is(anInterface().public_()));
    }

    @Test
    public void valueBuilder() throws Exception {
        assertThat(compiled.getClass("org.generated.Val$Builder"), is(aClass().public_().static_()));
    }

    @Test
    public void valueImplementationClass() throws Exception {
        assertThat(compiled.getClass("org.generated.ValImpl"),
                is(
                        aClass().packagePrivate()
                                .implementing(compiled.getClass("org.generated.Val"))
                )
        );
    }
}