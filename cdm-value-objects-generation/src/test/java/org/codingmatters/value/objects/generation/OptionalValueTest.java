package org.codingmatters.value.objects.generation;

import org.codingmatters.tests.compile.CompiledCode;
import org.codingmatters.tests.compile.FileHelper;
import org.codingmatters.tests.compile.helpers.ClassLoaderHelper;
import org.codingmatters.value.objects.spec.Spec;
import org.codingmatters.value.objects.spec.TypeKind;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.util.Optional;

import static org.codingmatters.tests.reflect.ReflectMatchers.*;
import static org.codingmatters.value.objects.spec.PropertySpec.property;
import static org.codingmatters.value.objects.spec.PropertyTypeSpec.type;
import static org.codingmatters.value.objects.spec.Spec.spec;
import static org.codingmatters.value.objects.spec.ValueSpec.valueSpec;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class OptionalValueTest {

    @Rule
    public TemporaryFolder dir = new TemporaryFolder();
    @Rule
    public FileHelper fileHelper = new FileHelper();

    private final Spec spec  = spec()
            .addValue(
                    valueSpec().name("val")
                            .addProperty(property().name("stringProp").type(type().typeKind(TypeKind.JAVA_TYPE).typeRef(String.class.getName())))
                            .addProperty(property().name("intProp").type(type().typeKind(TypeKind.JAVA_TYPE).typeRef(String.class.getName())))
            )
            .build();
    private ClassLoaderHelper classes;

    @Before
    public void setUp() throws Exception {
        new SpecCodeGenerator(this.spec, "org.generated", dir.getRoot()).generate();

        this.fileHelper.printJavaContent("", this.dir.getRoot());
        this.fileHelper.printFile(this.dir.getRoot(), "OptionalVal.java");

        this.classes = CompiledCode.builder()
                .source(this.dir.getRoot())
                .compile()
                .classLoader();
    }

    @Test
    public void optionalClass() throws Exception {
        assertThat(
                this.classes.get("org.generated.optional.OptionalVal").get(),
                is(aPublic().class_())
        );
    }

    @Test
    public void constructor() throws Exception {
        assertThat(
                this.classes.get("org.generated.optional.OptionalVal").get(),
                is(aPublic().class_()
                        .with(aPrivate().constructor().withParameters(this.classes.get("org.generated.Val").get()))
                )
        );
    }

    @Test
    public void fields() throws Exception {
        assertThat(
                this.classes.get("org.generated.optional.OptionalVal").get(),
                is(aPublic().class_()
                        .with(aPrivate().field().final_().named("optional")
                                .withType(genericType().baseClass(Optional.class)
                                        .withParameters(typeParameter().aClass(this.classes.get("org.generated.Val").get()))
                                )
                        )
                        .with(aPrivate().field().final_().named("stringProp")
                                .withType(genericType().baseClass(Optional.class)
                                        .withParameters(typeParameter().aClass(String.class))
                                )
                        )
                )
        );
    }
}