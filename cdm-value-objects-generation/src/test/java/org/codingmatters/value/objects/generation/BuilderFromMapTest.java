package org.codingmatters.value.objects.generation;

import org.codingmatters.tests.compile.CompiledCode;
import org.codingmatters.value.objects.spec.Spec;
import org.codingmatters.value.objects.spec.TypeKind;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.util.Map;

import static org.codingmatters.tests.reflect.ReflectMatchers.aStatic;
import static org.codingmatters.tests.reflect.ReflectMatchers.anInterface;
import static org.codingmatters.value.objects.spec.PropertySpec.property;
import static org.codingmatters.value.objects.spec.PropertyTypeSpec.type;
import static org.codingmatters.value.objects.spec.Spec.spec;
import static org.codingmatters.value.objects.spec.ValueSpec.valueSpec;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class BuilderFromMapTest {

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
                compiled.getClass("org.generated.Val").getDeclaredMethod("fromMap", Map.class),
                is(aStatic().public_().method()
                        .named("fromMap")
                        .withParameters(Map.class)
                        .returning(compiled.getClass("org.generated.Val$Builder"))
                )
        );

        assertThat(compiled.getClass("org.generated.Val"),
                is(anInterface()
                        .with(
                                aStatic().public_().method()
                                        .named("fromMap")
                                        .withParameters(Map.class)
                                        .returning(compiled.getClass("org.generated.Val$Builder"))
                        )
                )
        );
    }
}
