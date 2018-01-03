package org.codingmatters.value.objects.generation;

import org.codingmatters.tests.compile.CompiledCode;
import org.codingmatters.tests.compile.helpers.ClassLoaderHelper;
import org.codingmatters.tests.compile.helpers.helpers.ObjectHelper;
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
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 9/14/16.
 */
public class JavaTypePropertySpecGenerationTest {

    @Rule
    public TemporaryFolder dir = new TemporaryFolder();

    private final Spec spec  = spec()
            .addValue(
                    valueSpec().name("val")
                    .addProperty(property().name("prop").type(type().typeKind(TypeKind.JAVA_TYPE).typeRef(String.class.getName())))
                    .addProperty(property().name("prop2").type(type().typeKind(TypeKind.JAVA_TYPE).typeRef(String.class.getName())))
                    .addProperty(property().name("binary").type(type().typeKind(TypeKind.JAVA_TYPE).typeRef(byte[].class.getName())))
            )
            .build();
    private ClassLoaderHelper classes;

    @Before
    public void setUp() throws Exception {
        new SpecCodeGenerator(this.spec, "org.generated", dir.getRoot()).generate();
        this.classes = CompiledCode.builder().source(this.dir.getRoot()).compile().classLoader();
    }

    @Test
    public void multipleProperty_multipleMethods() throws Exception {
        assertThat(classes.get("org.generated.Val").get(), is(anInstance().public_().interface_().with(anInstance().method().named("prop"))));
        assertThat(classes.get("org.generated.ValImpl").get(), is(aPackagePrivate().class_().with(anInstance().method().named("prop"))));
        assertThat(classes.get("org.generated.Val$Builder").get(), is(aStatic().public_().class_().with(anInstance().method().named("prop"))));

        assertThat(classes.get("org.generated.Val").get(), is(anInstance().public_().interface_().with(anInstance().method().named("prop2"))));
        assertThat(classes.get("org.generated.ValImpl").get(), is(aPackagePrivate().class_().with(anInstance().method().named("prop2"))));
        assertThat(classes.get("org.generated.Val$Builder").get(), is(aStatic().public_().class_().with(anInstance().method().named("prop2"))));

        assertThat(classes.get("org.generated.Val").get(), is(anInstance().public_().interface_().with(anInstance().method().named("binary"))));
        assertThat(classes.get("org.generated.ValImpl").get(), is(aPackagePrivate().class_().with(anInstance().method().named("binary"))));
        assertThat(classes.get("org.generated.Val$Builder").get(), is(aStatic().public_().class_().with(anInstance().method().named("binary"))));
    }

    @Test
    public void propertyInterfaceGetterSignature() throws Exception {
        assertThat(classes.get("org.generated.Val").get(),
                is(anInterface()
                        .with(anInstance().method().named("prop").withoutParameters().returning(String.class))
                        .with(anInstance().method().named("prop2").withoutParameters().returning(String.class))
                        .with(anInstance().method().named("binary").withoutParameters().returning(byte[].class))
                )
        );
    }

    @Test
    public void propertyBuilderSetterSignature() throws Exception {
        assertThat(classes.get("org.generated.Val$Builder").get(),
                is(aStatic().class_()
                        .with(aPublic().method().named("prop")
                                .withParameters(String.class).returning(classes.get("org.generated.Val$Builder").get())
                        )
                        .with(aPublic().method().named("prop2")
                                .withParameters(String.class).returning(classes.get("org.generated.Val$Builder").get())
                        )
                        .with(aPublic().method().named("binary")
                                .withParameters(byte[].class).returning(classes.get("org.generated.Val$Builder").get())
                        )
                )
        );
    }

    @Test
    public void propertyBuilderField() throws Exception {
        assertThat(classes.get("org.generated.Val$Builder").get(),
                is(aStatic().class_()
                        .with(aPrivate().field().named("prop"))
                        .with(aPrivate().field().named("prop2"))
                        .with(aPrivate().field().named("binary"))
                )
        );
    }

    @Test
    public void propertyValueGetterSignature() throws Exception {
        assertThat(classes.get("org.generated.ValImpl").get(),
                is(aPackagePrivate().class_()
                        .with(aPublic().method().named("prop")
                                .withoutParameters().returning(String.class)
                        )
                        .with(aPublic().method().named("prop2")
                                .withoutParameters().returning(String.class)
                        )
                        .with(aPublic().method().named("binary")
                                .withoutParameters().returning(byte[].class)
                        )
                )
        );
    }

    @Test
    public void propertyValueField() throws Exception {
        assertThat(classes.get("org.generated.ValImpl").get(),
                is(aPackagePrivate().class_()
                        .with(aPrivate().field().named("prop").final_())
                        .with(aPrivate().field().named("prop2").final_())
                        .with(aPrivate().field().named("binary").final_())
                )
        );
    }

    @Test
    public void valueBuilding() throws Exception {
        ObjectHelper value = classes.get("org.generated.Val").call("builder")
                .call("prop", String.class).with("prop value")
                .call("prop2", String.class).with("prop2 value")
                .call("binary", byte[].class).with("binary value".getBytes())
                .call("build");

        assertThat(value.get(), is(notNullValue(classes.get("org.generated.ValImpl").get())));
        assertThat(value.as("org.generated.Val").call("prop").get(), is("prop value"));
        assertThat(value.as("org.generated.Val").call("prop2").get(), is("prop2 value"));
        assertThat(value.as("org.generated.Val").call("binary").get(), is("binary value".getBytes()));
    }
}
