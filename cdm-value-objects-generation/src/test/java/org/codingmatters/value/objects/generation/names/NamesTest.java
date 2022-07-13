package org.codingmatters.value.objects.generation.names;

import org.codingmatters.tests.compile.CompiledCode;
import org.codingmatters.tests.compile.FileHelper;
import org.codingmatters.tests.compile.helpers.ClassLoaderHelper;
import org.codingmatters.value.objects.generation.SpecCodeGenerator;
import org.codingmatters.value.objects.spec.PropertyCardinality;
import org.codingmatters.value.objects.spec.Spec;
import org.codingmatters.value.objects.spec.TypeKind;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.codingmatters.tests.reflect.ReflectMatchers.aPublic;
import static org.codingmatters.tests.reflect.ReflectMatchers.aStatic;
import static org.codingmatters.value.objects.spec.PropertySpec.property;
import static org.codingmatters.value.objects.spec.PropertyTypeSpec.type;
import static org.codingmatters.value.objects.spec.Spec.spec;
import static org.codingmatters.value.objects.spec.ValueSpec.valueSpec;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class NamesTest {

    @Rule
    public TemporaryFolder dir = new TemporaryFolder();

    @Rule
    public FileHelper fileHelper = new FileHelper();

    private final Spec spec = spec()
            .addValue(valueSpec().name("val")
                    .addProperty(property().name("prop1").type(type().typeRef(String.class.getName()).typeKind(TypeKind.JAVA_TYPE)))
                    .addProperty(property().name("prop2").hints(set("property:raw(a-long-name)")).type(type().typeRef(String.class.getName()).typeKind(TypeKind.JAVA_TYPE)))
            )
            .addValue(valueSpec().name("referencingVal")
                    .addProperty(property().name("single").type(type().typeRef("val").typeKind(TypeKind.IN_SPEC_VALUE_OBJECT)))
                    .addProperty(property().name("multiple").type(type().cardinality(PropertyCardinality.LIST).typeRef("val").typeKind(TypeKind.IN_SPEC_VALUE_OBJECT)))
            )
            .build();
    private ClassLoaderHelper classes;

    @Before
    public void setUp() throws Exception {
        new SpecCodeGenerator(this.spec, "org.generated", dir.getRoot()).generate();
        this.classes = CompiledCode.builder().source(this.dir.getRoot()).compile().classLoader();
    }

    @Test
    public void whenValue__thenNamesInterfaceHasInstanceFiels() throws Exception {
        assertThat(classes.get("org.generated.names.ValNames").get(),
                is(aPublic().interface_()
                        .with(aStatic().public_().field().named("INSTANCE"))
                )
        );
    }

    @Test
    public void whenSimpleValue__thenNamesInterfaceHasFieldMethods() throws Exception {
        assertThat(classes.get("org.generated.names.ValNames").get(),
                is(aPublic().interface_())
        );
        assertThat(classes.get("org.generated.names.ValNames").get(),
                is(aPublic().interface_()
                        .with(aPublic().method()
                                .named("prop1")
                                .withoutParameters()
                                .returning(String.class)
                        )
                        .with(aPublic().method()
                                .named("prop2")
                                .withoutParameters()
                                .returning(String.class)
                        )
                )
        );
    }

    @Test
    public void whenReferencingValue__thenNamesInterfaceHasFieldMethodsPointingToReferenceNames() throws Exception {
        assertThat(classes.get("org.generated.names.ReferencingValNames").get(),
                is(aPublic().interface_())
        );
        assertThat(classes.get("org.generated.names.ReferencingValNames").get(),
                is(aPublic().interface_()
                        .with(aPublic().method()
                                .named("single")
                                .withoutParameters()
                                .returning(String.class)
                        )
                        .with(aPublic().method()
                                .named("singleNames")
                                .withoutParameters()
                                .returning(classes.get("org.generated.names.ValNames").get())
                        )
                        .with(aPublic().method()
                                .named("multiple")
                                .withoutParameters()
                                .returning(String.class)
                        )
                        .with(aPublic().method()
                                .named("multipleNames")
                                .withoutParameters()
                                .returning(classes.get("org.generated.names.ValNames").get())
                        )
                )
        );
    }

    @Test
    public void whenSimpleValue__thenValueHasNamesStaticMethod_andReturnsValueNamesInterface() throws Exception {
        assertThat(classes.get("org.generated.Val").get(),
                is(aPublic().interface_()
                        .with(aStatic().public_().method()
                                .named("names")
                                .withoutParameters()
                                .returning(classes.get("org.generated.names.ValNames").get())
                        )
                )
        );
    }

    @Test
    public void givenCallingNameMethod__whenPropertyHasIdentifierName__thenNameReturned() throws Exception {
        assertThat(
                classes.get("org.generated.Val").call("names").call("prop1").get(),
                is("prop1")
        );
    }

    @Test
    public void givenCallingNameMethod__whenPropertyHasRawHint__thenRawNameReturned() throws Exception {
        assertThat(
                classes.get("org.generated.Val").call("names").call("prop2").get(),
                is("a-long-name")
        );
    }

    @Test
    public void givenComplexField__whenCallingNameMethod__thenFieldName() throws Exception {
        assertThat(
                classes.get("org.generated.ReferencingVal").call("names").call("single").get(),
                is("single")
        );
    }

    @Test
    public void givenComplexField__whenCallingNamesMethod__thenReferencedNamesAreAccessible() throws Exception {
        assertThat(
                classes.get("org.generated.ReferencingVal").call("names").call("singleNames").call("prop2").get(),
                is("a-long-name")
        );
    }

    private Set<String> set(String ... values) {
        return values != null ? new HashSet<String>(Arrays.asList(values)) : new HashSet<>();
    }
}
