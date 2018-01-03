package org.codingmatters.value.objects.generation;

import org.codingmatters.tests.compile.CompiledCode;
import org.codingmatters.tests.compile.FileHelper;
import org.codingmatters.tests.compile.helpers.ClassLoaderHelper;
import org.codingmatters.tests.compile.helpers.helpers.ClassHelper;
import org.codingmatters.tests.compile.helpers.helpers.ObjectHelper;
import org.codingmatters.value.objects.spec.PropertyCardinality;
import org.codingmatters.value.objects.spec.Spec;
import org.codingmatters.value.objects.spec.TypeKind;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.codingmatters.tests.reflect.ReflectMatchers.aPackagePrivate;
import static org.codingmatters.tests.reflect.ReflectMatchers.aPublic;
import static org.codingmatters.value.objects.spec.PropertySpec.property;
import static org.codingmatters.value.objects.spec.PropertyTypeSpec.type;
import static org.codingmatters.value.objects.spec.Spec.spec;
import static org.codingmatters.value.objects.spec.ValueSpec.valueSpec;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 9/27/16.
 */
public class ToStringTest {

    @Rule
    public TemporaryFolder dir = new TemporaryFolder();

    @Rule
    public FileHelper fileHelper = new FileHelper();

    private final Spec spec = spec()
            .addValue(valueSpec().name("val")
                    .addProperty(property().name("prop1").type(type().typeRef(String.class.getName()).typeKind(TypeKind.JAVA_TYPE)))
                    .addProperty(property().name("prop2").type(type().typeRef(String.class.getName()).typeKind(TypeKind.JAVA_TYPE)))
                    .addProperty(property().name("prop3").type(type().typeKind(TypeKind.ENUM).enumValues("A", "B", "C")))
                    .addProperty(property().name("prop4").type(type().typeKind(TypeKind.ENUM).enumValues("A", "B", "C")
                            .cardinality(PropertyCardinality.LIST)))
                    .addProperty(property().name("binary").type(type().typeRef(byte[].class.getName()).typeKind(TypeKind.JAVA_TYPE)))
            )
            .addValue(valueSpec().name("noPropertyVal"))
            .addValue(valueSpec().name("complexVal")
                    .addProperty(property().name("prop").type(type().typeRef("val").typeKind(TypeKind.IN_SPEC_VALUE_OBJECT)))
            )
            .build();
    private ClassLoaderHelper classes;


    @Before
    public void setUp() throws Exception {
        new SpecCodeGenerator(this.spec, "org.generated", dir.getRoot()).generate();
        this.classes = CompiledCode.builder().source(this.dir.getRoot()).compile().classLoader();
    }

    @Test
    public void signature() throws Exception {
        assertThat(classes.get("org.generated.ValImpl").get(),
                is(aPackagePrivate().class_()
                        .with(aPublic().method()
                                .named("toString")
                                .withoutParameters()
                                .returning(String.class)
                        )
                )
        );
    }

    @Test
    public void simple() throws Exception {
        this.fileHelper.printFile(this.dir.getRoot(), "ValImpl.java");
        ClassHelper enum3 = classes.get("org.generated.Val$Prop3");
        ClassHelper enum4 = classes.get("org.generated.Val$Prop4");
        ObjectHelper prop4Value = enum4.array().newArray(
                enum4.call("valueOf", String.class).with("A").get(),
                enum4.call("valueOf", String.class).with("B").get()
        );

        Object aValue = classes.get("org.generated.Val").call("builder")
                .call("prop1", String.class).with("v1")
                .call("prop2", String.class).with("v2")
                .call("prop3", enum3.get()).with(enum3.call("valueOf", String.class).with("A").get())
                .call("prop4", enum4.array().get()).with(prop4Value.get())
                .call("binary", byte[].class).with("binary".getBytes())
                .call("build").get();

        assertThat(aValue.toString(), is("Val{prop1=v1, prop2=v2, prop3=A, prop4=[A, B], binary=[98, 105, 110, 97, 114, 121]}"));
    }

    @Test
    public void simpleWithNulls() throws Exception {
        Object aValue = classes.get("org.generated.Val").call("builder")
                .call("prop1", String.class).with("v1")
                .call("build").get();

        assertThat(aValue.toString(), is("Val{prop1=v1, prop2=null, prop3=null, prop4=null, binary=null}"));
    }

    @Test
    public void complex() throws Exception {
        Object builded = classes.get("org.generated.Val").call("builder")
                .call("prop1", String.class).with("v1")
                .call("prop2", String.class).with("v2")
                .call("build").get();

        Object complexValue = classes.get("org.generated.ComplexVal").call("builder")
                .call("prop", classes.get("org.generated.Val").get()).with(builded)
                .call("build").get()
                ;

        assertThat(complexValue.toString(), is("ComplexVal{prop=Val{prop1=v1, prop2=v2, prop3=null, prop4=null, binary=null}}"));
    }

    @Test
    public void noPropertyValue() throws Exception {
        Object aValue = classes.get("org.generated.NoPropertyVal").call("builder").call("build").get();

        assertThat(aValue.toString(), is("NoPropertyVal{}"));
    }
}
