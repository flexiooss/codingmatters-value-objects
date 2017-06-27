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
import static org.codingmatters.value.objects.spec.PropertySpec.property;
import static org.codingmatters.value.objects.spec.PropertyTypeSpec.type;
import static org.codingmatters.value.objects.spec.Spec.spec;
import static org.codingmatters.value.objects.spec.ValueSpec.valueSpec;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 11/19/16.
 */
public class SetPropertySpecGenerationTest {

    @Rule
    public TemporaryFolder dir = new TemporaryFolder();

    private final Spec spec  = spec()
            .addValue(
                    valueSpec().name("val")
                            .addProperty(property().name("setProp").type(type()
                                    .typeRef(String.class.getName())
                                    .typeKind(TypeKind.JAVA_TYPE)
                                    .cardinality(PropertyCardinality.SET)))
            )
            .build();
    private CompiledCode compiled;

    @Before
    public void setUp() throws Exception {
        new SpecCodeGenerator(this.spec, "org.generated", dir.getRoot()).generate();
        this.compiled = CompiledCode.builder().source(this.dir.getRoot()).compile();
    }

    @Test
    public void valueSetHarness() throws Exception {
        assertThat(this.compiled.getClass("org.generated.ValueSet"), is(aPublic().interface_()));
        assertThat(this.compiled.getClass("org.generated.ValueSetImpl"), is(aPackagePrivate().class_()));
    }


    @Test
    public void setPropValueMethod() throws Exception {
        assertThat(this.compiled.getClass("org.generated.Val"), is(anInterface()
                .with(aPublic().method().named("setProp").returning(
                        genericType().baseClass(this.compiled.getClass("org.generated.ValueSet"))
                                .withParameters(typeParameter().named(String.class.getName()))
                ))
        ));
    }

    @Test
    public void propBuilderMethod() throws Exception {
        assertThat(this.compiled.getClass("org.generated.Val$Builder"), is(aStatic().class_()
                .with(aPublic().method()
                        .named("setProp")
                        .withoutParameters()
                        .returning(this.compiled.getClass("org.generated.Val$Builder"))
                )
                .with(aPublic().method()
                        .named("setProp")
                        .withParameters(String[].class)
                        .returning(this.compiled.getClass("org.generated.Val$Builder"))
                )
                .with(aPublic().method()
                        .named("setProp")
                        .withParameters(genericType().baseClass(this.compiled.getClass("org.generated.ValueSet"))
                                .withParameters(typeParameter().named(String.class.getName())))
                        .returning(this.compiled.getClass("org.generated.Val$Builder"))
                )
        ));
    }

    @Test
    public void builderWithValueArray() throws Exception {
        Object builder = this.compiled.onClass("org.generated.Val").invoke("builder");
        this.compiled.on(builder).invoke("setProp", String[].class).with(new Object [] {new String [] {"a", "b", "c"}});
        Object value = this.compiled.on(builder).invoke("build");
        Object list = this.compiled.on(value).castedTo("org.generated.Val").invoke("setProp");

        assertThat(this.compiled.on(list).invoke("toArray"), is(new String [] {"a", "b", "c"}));
    }

    @Test
    public void builderWithValueSet() throws Exception {
        Object builder1 = this.compiled.onClass("org.generated.Val").invoke("builder");
        this.compiled.on(builder1).invoke("setProp", String[].class).with(new Object [] {new String [] {"a", "b", "c"}});
        Object value1 = this.compiled.on(builder1).invoke("build");
        Object list1 = this.compiled.on(value1).castedTo("org.generated.Val").invoke("setProp");

        Object builder2 = this.compiled.onClass("org.generated.Val").invoke("builder");
        this.compiled.on(builder2).invoke("setProp", this.compiled.getClass("org.generated.ValueSet")).with(list1);
        Object value2 = this.compiled.on(builder2).invoke("build");
        Object list2 = this.compiled.on(value2).castedTo("org.generated.Val").invoke("setProp");

        assertThat(this.compiled.on(list2).invoke("toArray"), is(new String [] {"a", "b", "c"}));
    }

    @Test
    public void equalsWithSet() throws Exception {
        Object builder1 = this.compiled.onClass("org.generated.Val").invoke("builder");
        this.compiled.on(builder1).invoke("setProp", String[].class).with(new Object [] {new String [] {"a", "b", "c"}});
        Object value1 = this.compiled.on(builder1).invoke("build");

        Object builder2 = this.compiled.onClass("org.generated.Val").invoke("builder");
        this.compiled.on(builder2).invoke("setProp", String[].class).with(new Object [] {new String [] {"a", "b", "c"}});
        Object value2 = this.compiled.on(builder2).invoke("build");

        assertThat(value2, is(value1));
    }

}
