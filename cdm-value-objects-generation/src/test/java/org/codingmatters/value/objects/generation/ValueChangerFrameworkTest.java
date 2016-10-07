package org.codingmatters.value.objects.generation;

import org.codingmatters.tests.compile.CompiledCode;
import org.codingmatters.value.objects.spec.Spec;
import org.codingmatters.value.objects.spec.TypeKind;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.codingmatters.tests.reflect.ReflectMatchers.*;
import static org.codingmatters.value.objects.spec.PropertySpec.property;
import static org.codingmatters.value.objects.spec.PropertyTypeSpec.type;
import static org.codingmatters.value.objects.spec.Spec.spec;
import static org.codingmatters.value.objects.spec.ValueSpec.valueSpec;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 10/6/16.
 */
public class ValueChangerFrameworkTest {
    @Rule
    public TemporaryFolder dir = new TemporaryFolder();

    private final Spec spec = spec()
            .addValue(valueSpec().name("val")
                    .addProperty(property().name("prop").type(type().typeRef(String.class.getName()).typeKind(TypeKind.JAVA_TYPE)))
            )
            .build();
    private CompiledCode compiled;

    @Before
    public void setUp() throws Exception {
        new SpecCodeGenerator(this.spec, "org.generated").generateTo(dir.getRoot());
        this.compiled = CompiledCode.compile(this.dir.getRoot());
    }

    @Test
    public void valueChangerFunctionalInterface() throws Exception {
        assertThat(compiled.getClass("org.generated.Val$Changer"), is(
                aStatic().interface_()
                        .with(aMethod().named("configure")
                                .withParameters(compiled.getClass("org.generated.Val$Builder"))
                                .returning(Void.TYPE))
        ));
    }

    @Test
    public void changedMethod() throws Exception {
        assertThat(compiled.getClass("org.generated.Val"), is(
                anInterface()
                        .with(aMethod().named("changed")
                                .withParameters(compiled.getClass("org.generated.Val$Changer"))
                                .returning(compiled.getClass("org.generated.Val"))
                        )
        ));
    }
}
