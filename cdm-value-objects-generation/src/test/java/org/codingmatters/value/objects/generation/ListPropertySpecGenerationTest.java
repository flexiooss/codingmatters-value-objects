package org.codingmatters.value.objects.generation;

import org.codingmatters.tests.compile.CompiledCode;
import org.codingmatters.tests.reflect.ReflectMatchers;
import org.codingmatters.value.objects.spec.PropertyCardinality;
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
 * Created by nelt on 11/12/16.
 */
public class ListPropertySpecGenerationTest {

    @Rule
    public TemporaryFolder dir = new TemporaryFolder();

    private final Spec spec  = spec()
            .addValue(
                    valueSpec().name("val")
                            .addProperty(property().name("listProp").type(type()
                                    .typeRef(String.class.getName())
                                    .typeKind(TypeKind.JAVA_TYPE)
                                    .cardinality(PropertyCardinality.LIST)))
            )
            .build();
    private CompiledCode compiled;

    @Before
    public void setUp() throws Exception {
        new SpecCodeGenerator(this.spec, "org.generated").generateTo(dir.getRoot());
        this.compiled = CompiledCode.compile(this.dir.getRoot());
    }

    @Test
    public void valueListHarness() throws Exception {
        assertThat(this.compiled.getClass("org.generated.ValueList"), is(aPublic().interface_()));
        assertThat(this.compiled.getClass("org.generated.ValueListImpl"), is(aPackagePrivate().class_()));
    }

    @Test
    public void listPropMethod() throws Exception {
        assertThat(this.compiled.getClass("org.generated.Val"), is(ReflectMatchers.anInterface()
                .with(aPublic().method().named("listProp").returning(
                        genericType().baseClass(this.compiled.getClass("org.generated.ValueList")).withParameters(typeParameter().named(String.class.getName()))
                ))
        ));
    }
}
