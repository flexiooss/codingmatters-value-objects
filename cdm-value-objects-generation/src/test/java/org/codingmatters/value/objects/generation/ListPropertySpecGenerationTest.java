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
import static org.hamcrest.Matchers.*;
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
        new SpecCodeGenerator(this.spec, "org.generated", dir.getRoot()).generate();
        this.compiled = CompiledCode.builder().source(this.dir.getRoot()).compile();
    }

    @Test
    public void valueListHarness() throws Exception {
        assertThat(this.compiled.getClass("org.generated.ValueList"), is(aPublic().interface_()));
        assertThat(this.compiled.getClass("org.generated.ValueListImpl"), is(aPackagePrivate().class_()));
        assertThat(this.compiled.getClass("org.generated.ValueList$Builder"), is(aPublic().static_().class_()));
    }

    @Test
    public void valueListBuilder() throws Exception {
        assertThat(this.compiled.getClass("org.generated.ValueList$Builder"), is(aPublic().static_().class_()));
        assertThat(
                this.compiled.getClass("org.generated.ValueList$Builder"),
                is(aPublic().static_().class_()
                        .with(
                                aPublic().method().named("build")
                                .withoutParameters()
                                .returning(
                                        genericType().baseClass(this.compiled.getClass("org.generated.ValueList")).withParameters(typeParameter().named("E"))
                                )
                        )
                )
        );

        assertThat(
                this.compiled.getClass("org.generated.ValueList$Builder"),
                is(aPublic().static_().class_()
                        .with(
                                aPublic().method().named("with")
                                    .withParameters(typeArray(variableType().named("E")))
                                    .returning(this.compiled.getClass("org.generated.ValueList$Builder"))
                        )
                )
        );
    }

    @Test
    public void listPropValueMethod() throws Exception {
        assertThat(this.compiled.getClass("org.generated.Val"), is(anInterface()
                .with(aPublic().method().named("listProp").returning(
                        genericType().baseClass(this.compiled.getClass("org.generated.ValueList")).withParameters(typeParameter().named(String.class.getName()))
                ))
        ));
    }

    @Test
    public void listPropBuilderMethod() throws Exception {
        assertThat(this.compiled.getClass("org.generated.Val$Builder"), is(aStatic().class_()
                .with(aPublic().method()
                        .named("listProp")
                        .withoutParameters()
                        .returning(this.compiled.getClass("org.generated.Val$Builder"))
                )
                .with(aPublic().method()
                        .named("listProp")
                        .withParameters(String[].class)
                        .returning(this.compiled.getClass("org.generated.Val$Builder"))
                )
                .with(aPublic().method()
                        .named("listProp")
                        .withParameters(genericType().baseClass(this.compiled.getClass("org.generated.ValueList")).withParameters(typeParameter().named(String.class.getName())))
                        .returning(this.compiled.getClass("org.generated.Val$Builder"))
                )
        ));
    }

    @Test
    public void builderWithValueArray() throws Exception {
        Object builder = this.compiled.onClass("org.generated.Val").invoke("builder");
        this.compiled.on(builder).invoke("listProp", String[].class).with(new Object [] {new String [] {"a", "b", "c"}});
        Object value = this.compiled.on(builder).invoke("build");
        Object list = this.compiled.on(value).castedTo("org.generated.Val").invoke("listProp");

        assertThat(this.compiled.on(list).invoke("toArray"), is(new String [] {"a", "b", "c"}));
    }

    @Test
    public void builderWithValueList() throws Exception {
        Object builder1 = this.compiled.onClass("org.generated.Val").invoke("builder");
        this.compiled.on(builder1).invoke("listProp", String[].class).with(new Object [] {new String [] {"a", "b", "c"}});
        Object value1 = this.compiled.on(builder1).invoke("build");
        Object list1 = this.compiled.on(value1).castedTo("org.generated.Val").invoke("listProp");

        Object builder2 = this.compiled.onClass("org.generated.Val").invoke("builder");
        this.compiled.on(builder2).invoke("listProp", this.compiled.getClass("org.generated.ValueList")).with(list1);
        Object value2 = this.compiled.on(builder2).invoke("build");
        Object list2 = this.compiled.on(value2).castedTo("org.generated.Val").invoke("listProp");

        assertThat(this.compiled.on(list2).invoke("toArray"), is(new String [] {"a", "b", "c"}));
    }

    @Test
    public void builderWithNullValueList() throws Exception {
        Object builder = this.compiled.onClass("org.generated.Val").invoke("builder");
        this.compiled.on(builder)
                .invoke("listProp", this.compiled.getClass("org.generated.ValueList"))
                .with(new String [] {null});
        Object value = this.compiled.on(builder).invoke("build");
        Object list = this.compiled.on(value).castedTo("org.generated.Val").invoke("listProp");

        assertThat(list, is(nullValue()));
    }

    @Test
    public void equalsWithList() throws Exception {
        Object builder1 = this.compiled.onClass("org.generated.Val").invoke("builder");
        this.compiled.on(builder1).invoke("listProp", String[].class).with(new Object [] {new String [] {"a", "b", "c"}});
        Object value1 = this.compiled.on(builder1).invoke("build");

        Object builder2 = this.compiled.onClass("org.generated.Val").invoke("builder");
        this.compiled.on(builder2).invoke("listProp", String[].class).with(new Object [] {new String [] {"a", "b", "c"}});
        Object value2 = this.compiled.on(builder2).invoke("build");

        assertThat(value2, is(value1));
    }

    @Test
    public void buildValueListInstance() throws Exception {
        Object listBuilder = this.compiled.getClass("org.generated.ValueList$Builder").newInstance();
        this.compiled.on(listBuilder)
                .invoke("with", Object[].class)
                .with(new Object [] {new Object [] {"a", "b", "c"}});
        Object list = this.compiled.on(listBuilder).invoke("build");

        assertThat(list, isA(this.compiled.getClass("org.generated.ValueList")));
    }
}
