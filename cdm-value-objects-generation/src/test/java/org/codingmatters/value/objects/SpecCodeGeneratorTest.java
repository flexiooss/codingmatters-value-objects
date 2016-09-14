package org.codingmatters.value.objects;

import org.codingmatters.tests.compile.CompiledCode;
import org.codingmatters.value.objects.spec.Spec;
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

    @Test
    public void value_generatesInterface() throws Exception {
        new SpecCodeGenerator(this.spec, "org.generated").generateTo(dir.getRoot());
        CompiledCode compiled = CompiledCode.compile(this.dir.getRoot());

        assertThat(compiled.getClass("org.generated.Val"), is(anInterface().public_()));
    }

    @Test
    public void value_generatesBuilder() throws Exception {
        new SpecCodeGenerator(this.spec, "org.generated").generateTo(dir.getRoot());
        CompiledCode compiled = CompiledCode.compile(this.dir.getRoot());

        assertThat(compiled.getClass("org.generated.Val$Builder"), is(aClass()));
    }

    @Test
    public void value_generatesPackagePrivateImplementationClass() throws Exception {
        new SpecCodeGenerator(this.spec, "org.generated").generateTo(dir.getRoot());
        CompiledCode compiled = CompiledCode.compile(this.dir.getRoot());

        assertThat(compiled.getClass("org.generated.ValImpl"),
                is(
                        aClass().packagePrivate()
                                .implementing(compiled.getClass("org.generated.Val"))
                )
        );
    }
}