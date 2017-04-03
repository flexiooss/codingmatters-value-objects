package org.codingmatters.value.objects.generation;

import org.codingmatters.tests.compile.CompiledCode;
import org.codingmatters.value.objects.spec.PropertyCardinality;
import org.codingmatters.value.objects.spec.Spec;
import org.codingmatters.value.objects.spec.TypeKind;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.codingmatters.tests.reflect.ReflectMatchers.*;
import static org.codingmatters.value.objects.spec.AnonymousValueSpec.anonymousValueSpec;
import static org.codingmatters.value.objects.spec.PropertySpec.property;
import static org.codingmatters.value.objects.spec.PropertyTypeSpec.type;
import static org.codingmatters.value.objects.spec.Spec.spec;
import static org.codingmatters.value.objects.spec.ValueSpec.valueSpec;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 4/1/17.
 */
public class DeepCollectionPropertyGenerationTest {

    @Rule
    public TemporaryFolder dir = new TemporaryFolder();

    private final Spec spec  = spec()
            .addValue(
                    valueSpec().name("val")
                            .addProperty(property().name("container")
                                    .type(type().typeKind(TypeKind.EMBEDDED)
                                            .embeddedValueSpec(anonymousValueSpec()
                                                    .addProperty(property().name("listProp").type(type()
                                                            .typeRef(String.class.getName())
                                                            .typeKind(TypeKind.JAVA_TYPE)
                                                            .cardinality(PropertyCardinality.LIST))
                                                    )
                                            ))
                                    )
            )
            .build();
    private CompiledCode compiled;

    @Before
    public void setUp() throws Exception {
        new SpecCodeGenerator(this.spec, "org.generated", dir.getRoot()).generate();
        this.compiled = CompiledCode.builder().source(this.dir.getRoot()).compile();
    }



    @Test
    public void valueMethods() throws Exception {
        assertThat(this.compiled.getClass("org.generated.Val"), is(anInterface()
                .with(aPublic().method().named("container").returning(
                        this.compiled.getClass("org.generated.val.Container")
                ))
        ));
        assertThat(this.compiled.getClass("org.generated.val.Container"), is(anInterface()
                .with(aPublic().method().named("listProp").returning(
                        genericType().baseClass(this.compiled.getClass("org.generated.ValueList")).withParameters(typeParameter().named(String.class.getName()))
                ))
        ));
    }
}
