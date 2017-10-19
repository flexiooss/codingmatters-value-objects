package org.codingmatters.value.objects.generation;

import com.squareup.javapoet.TypeSpec;
import org.codingmatters.tests.compile.CompiledCode;
import org.codingmatters.tests.compile.FileHelper;
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
    private CompiledCode compiled;
    @Rule
    public FileHelper fileHelper = new FileHelper();

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
        this.compiled = CompiledCode.builder().source(this.dir.getRoot()).compile();
    }

    @Test
    public void enumWithRefProperties() throws Exception {
        assertThat(compiled.getClass("org.generated.ValWithRef"), is(anInstance().public_().interface_()
                .with(anInstance().method().named("single").returning(this.compiled.getClass("org.outside.MyEnum")))
        ));
        assertThat(compiled.getClass("org.generated.ValWithRefImpl"), is(aPackagePrivate().class_()
                .with(anInstance().method().named("single").returning(this.compiled.getClass("org.outside.MyEnum")))
        ));
        assertThat(compiled.getClass("org.generated.ValWithRef$Builder"), is(aStatic().public_().class_()
                .with(anInstance().method().named("single").withParameters(this.compiled.getClass("org.outside.MyEnum")))
        ));


        assertThat(compiled.getClass("org.generated.ValWithRef"), is(anInstance().public_().interface_().with(anInstance().method().named("list"))));
        assertThat(compiled.getClass("org.generated.ValWithRefImpl"), is(aPackagePrivate().class_().with(anInstance().method().named("list"))));
        assertThat(compiled.getClass("org.generated.ValWithRef$Builder"), is(aStatic().public_().class_().with(anInstance().method().named("list"))));
    }

    @Test
    public void enumWithValues() throws Exception {
        assertThat(this.compiled.getClass("org.generated.ValWithRaw$Single").isEnum(), is(true));

        assertThat(compiled.getClass("org.generated.ValWithRaw"), is(anInstance().public_().interface_()
                .with(anInstance().method().named("single")
                        .returning(this.compiled.getClass("org.generated.ValWithRaw$Single")))
        ));
        assertThat(compiled.getClass("org.generated.ValWithRawImpl"), is(aPackagePrivate().class_()
                .with(anInstance().method().named("single").returning(this.compiled.getClass("org.generated.ValWithRaw$Single")))
        ));
        assertThat(compiled.getClass("org.generated.ValWithRaw$Builder"), is(aStatic().public_().class_()
                .with(anInstance().method().named("single").withParameters(this.compiled.getClass("org.generated.ValWithRaw$Single")))
        ));

        assertThat(compiled.getClass("org.generated.ValWithRaw"), is(anInstance().public_().interface_().with(anInstance().method().named("list"))));
        assertThat(compiled.getClass("org.generated.ValWithRawImpl"), is(aPackagePrivate().class_().with(anInstance().method().named("list"))));
        assertThat(compiled.getClass("org.generated.ValWithRaw$Builder"), is(aStatic().public_().class_().with(anInstance().method().named("list"))));
    }
}
