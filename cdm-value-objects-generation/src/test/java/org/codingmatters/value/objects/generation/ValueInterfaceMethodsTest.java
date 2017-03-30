package org.codingmatters.value.objects.generation;

import org.codingmatters.tests.compile.CompiledCode;
import org.codingmatters.value.objects.spec.Spec;
import org.codingmatters.value.objects.spec.TypeKind;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.codingmatters.tests.reflect.ReflectMatchers.aMethod;
import static org.codingmatters.tests.reflect.ReflectMatchers.anInterface;
import static org.codingmatters.value.objects.spec.PropertySpec.property;
import static org.codingmatters.value.objects.spec.PropertyTypeSpec.type;
import static org.codingmatters.value.objects.spec.Spec.spec;
import static org.codingmatters.value.objects.spec.ValueSpec.valueSpec;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 10/5/16.
 */
public class ValueInterfaceMethodsTest {
    @Rule
    public TemporaryFolder dir = new TemporaryFolder();

    private final Spec spec = spec()
            .addValue(valueSpec().name("val")
                    .addProperty(property().name("prop").type(type().typeRef(String.class.getName()).typeKind(TypeKind.JAVA_TYPE)))
            )
            .addValue(valueSpec().name("complexVal")
                    .addProperty(property().name("prop").type(type().typeRef("val").typeKind(TypeKind.IN_SPEC_VALUE_OBJECT))))
            .build();
    private CompiledCode compiled;

    @Before
    public void setUp() throws Exception {
        new SpecCodeGenerator(this.spec, "org.generated", dir.getRoot()).generate();
        this.compiled = CompiledCode.builder().source(this.dir.getRoot()).compile();
    }

    @Test
    public void simplePropertySignatures() throws Exception {
        assertThat(compiled.getClass("org.generated.Val"), is(
                anInterface()
                        .with(aMethod().named("prop").returning(String.class))
                        .with(aMethod().named("withProp").withParameters(String.class).returning(compiled.getClass("org.generated.Val")))
                        .with(aMethod().named("hashCode").withoutParameters().returning(int.class))
                        .with(aMethod().named("changed")
                                .withParameters(compiled.getClass("org.generated.Val$Changer"))
                                .returning(compiled.getClass("org.generated.Val"))
                )
        ));
    }

    @Test
    public void complexPropertySignatures() throws Exception {
        assertThat(compiled.getClass("org.generated.ComplexVal"), is(
                anInterface()
                        .with(aMethod().named("prop").returning(compiled.getClass("org.generated.Val")))
                        .with(aMethod().named("withProp")
                                .withParameters(compiled.getClass("org.generated.Val"))
                                .returning(compiled.getClass("org.generated.ComplexVal")))
                        .with(aMethod().named("hashCode").withoutParameters().returning(int.class))
                        .with(aMethod().named("changed")
                                .withParameters(compiled.getClass("org.generated.ComplexVal$Changer"))
                                .returning(compiled.getClass("org.generated.ComplexVal"))
                        )
        ));
    }

}
