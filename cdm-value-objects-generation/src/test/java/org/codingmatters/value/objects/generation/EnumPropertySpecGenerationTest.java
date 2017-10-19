package org.codingmatters.value.objects.generation;

import com.squareup.javapoet.TypeSpec;
import org.codingmatters.tests.compile.CompiledCode;
import org.codingmatters.tests.compile.FileHelper;
import org.codingmatters.tests.compile.helpers.ClassLoaderHelper;
import org.codingmatters.value.objects.spec.PropertyCardinality;
import org.codingmatters.value.objects.spec.Spec;
import org.codingmatters.value.objects.spec.TypeKind;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import javax.lang.model.element.Modifier;

import static org.codingmatters.tests.reflect.ReflectMatchers.*;
import static org.codingmatters.value.objects.spec.PropertySpec.property;
import static org.codingmatters.value.objects.spec.PropertyTypeSpec.type;
import static org.codingmatters.value.objects.spec.Spec.spec;
import static org.codingmatters.value.objects.spec.ValueSpec.valueSpec;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 6/7/17.
 */
public class EnumPropertySpecGenerationTest {

    @Rule
    public TemporaryFolder dir = new TemporaryFolder();

    private final Spec spec  = spec()
            .addValue(valueSpec().name("valWithRef")
                    .addProperty(property().name("single").type(type()
                            .typeKind(TypeKind.ENUM)
                            .typeRef("org.outside.MyEnum")
                            .cardinality(PropertyCardinality.SINGLE)))
                    .addProperty(property().name("list").type(type()
                            .typeKind(TypeKind.ENUM)
                            .typeRef("org.outside.MyEnum")
                            .cardinality(PropertyCardinality.LIST)
                    ))
            )
            .addValue(valueSpec().name("valWithRaw")
                    .addProperty(property().name("single").type(type()
                            .typeKind(TypeKind.ENUM)
                            .enumValues("a", "b")
                            .cardinality(PropertyCardinality.SINGLE)))
                    .addProperty(property().name("list").type(type()
                            .typeKind(TypeKind.ENUM)
                            .enumValues("a", "b")
                            .cardinality(PropertyCardinality.LIST)
                    ))
            )
            .build();

    @Rule
    public FileHelper fileHelper = new FileHelper();
    private ClassLoaderHelper classes;

    @Before
    public void setUp() throws Exception {
        GenerationUtils.writeJavaFile(this.dir.getRoot(), "org.outside", TypeSpec.enumBuilder("MyEnum")
                .addModifiers(Modifier.PUBLIC)
                .addEnumConstant("V1")
                .addEnumConstant("V2")
                .build());
        new SpecCodeGenerator(this.spec, "org.generated", dir.getRoot()).generate();


        this.fileHelper.printJavaContent("", this.dir.getRoot());
        this.fileHelper.printFile(this.dir.getRoot(), "OptionalVal.java");
        this.classes = CompiledCode.builder().source(this.dir.getRoot()).compile().classLoader();
    }

    @Test
    public void enumWithRefProperties() throws Exception {
        assertThat(classes.get("org.generated.ValWithRef").get(), is(anInstance().public_().interface_()
                .with(anInstance().method().named("single").returning(classes.get("org.outside.MyEnum").get()))
        ));
        assertThat(classes.get("org.generated.ValWithRefImpl").get(), is(aPackagePrivate().class_()
                .with(anInstance().method().named("single").returning(classes.get("org.outside.MyEnum").get()))
        ));
        assertThat(classes.get("org.generated.ValWithRef$Builder").get(), is(aStatic().public_().class_()
                .with(anInstance().method().named("single").withParameters(classes.get("org.outside.MyEnum").get()))
        ));


        assertThat(classes.get("org.generated.ValWithRef").get(), is(anInstance().public_().interface_().with(anInstance().method().named("list"))));
        assertThat(classes.get("org.generated.ValWithRefImpl").get(), is(aPackagePrivate().class_().with(anInstance().method().named("list"))));
        assertThat(classes.get("org.generated.ValWithRef$Builder").get(), is(aStatic().public_().class_().with(anInstance().method().named("list"))));
    }

    @Test
    public void enumWithValues() throws Exception {
        assertThat(classes.get("org.generated.ValWithRaw$Single").get().isEnum(), is(true));

        assertThat(classes.get("org.generated.ValWithRaw").get(), is(anInstance().public_().interface_()
                .with(anInstance().method().named("single")
                        .returning(classes.get("org.generated.ValWithRaw$Single").get()))
        ));
        assertThat(classes.get("org.generated.ValWithRawImpl").get(), is(aPackagePrivate().class_()
                .with(anInstance().method().named("single").returning(classes.get("org.generated.ValWithRaw$Single").get()))
        ));
        assertThat(classes.get("org.generated.ValWithRaw$Builder").get(), is(aStatic().public_().class_()
                .with(anInstance().method().named("single").withParameters(classes.get("org.generated.ValWithRaw$Single").get()))
        ));

        assertThat(classes.get("org.generated.ValWithRaw").get(), is(anInstance().public_().interface_().with(anInstance().method().named("list"))));
        assertThat(classes.get("org.generated.ValWithRawImpl").get(), is(aPackagePrivate().class_().with(anInstance().method().named("list"))));
        assertThat(classes.get("org.generated.ValWithRaw$Builder").get(), is(aStatic().public_().class_().with(anInstance().method().named("list"))));
    }
}
