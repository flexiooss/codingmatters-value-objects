package org.codingmatters.value.objects.generation;

import org.codingmatters.tests.compile.CompiledCode;
import org.codingmatters.value.objects.spec.PropertyCardinality;
import org.codingmatters.value.objects.spec.Spec;
import org.codingmatters.value.objects.spec.TypeKind;
import org.junit.Before;
import org.junit.Ignore;
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
 * Created by nelt on 11/23/16.
 */
@Ignore
public class InSpecListPropertySpecGenerationTest {

    @Rule
    public TemporaryFolder dir = new TemporaryFolder();

    private final Spec spec  = spec()
            .addValue(
                    valueSpec().name("ref")
            )
            .addValue(
                    valueSpec().name("val")
                            .addProperty(property().name("prop").type(type()
                                    .typeRef("ref")
                                    .typeKind(TypeKind.IN_SPEC_VALUE_OBJECT)
                                    .cardinality(PropertyCardinality.SINGLE)))
                            .addProperty(property().name("listProp").type(type()
                                    .typeRef("ref")
                                    .typeKind(TypeKind.IN_SPEC_VALUE_OBJECT)
                                    .cardinality(PropertyCardinality.LIST)))
            )
            .build();
    private CompiledCode compiled;

    @Before
    public void setUp() throws Exception {
        new SpecCodeGenerator(this.spec, "org.generated", dir.getRoot()).generate();
        this.compiled = CompiledCode.compile(this.dir.getRoot());
    }

    @Test
    public void ok() throws Exception {
        System.out.println("BLABLABLABLA : " + this.compiled.getClass("org.generated.Ref"));
        assertThat(this.compiled.getClass("org.generated.Val$Builder"), is(aStatic().class_()
                .with(aPublic().method()
                        .named("listProp")
                        .withParameters(this.compiled.getClass("org.generated.Ref[]"))
                        .returning(this.compiled.getClass("org.generated.Val$Builder"))
                )
                .with(aPublic().method()
                        .named("listProp")
                        .withParameters(genericType().baseClass(this.compiled.getClass("org.generated.ValueList")).withParameters(typeParameter().named(String.class.getName())))
                        .returning(this.compiled.getClass("org.generated.Val$Builder"))
                )
        ));
    }
}
