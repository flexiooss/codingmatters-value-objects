package org.codingmatters.value.objects.generation;

import com.squareup.javapoet.TypeSpec;
import org.codingmatters.tests.compile.CompiledCode;
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
            .addValue(valueSpec().name("val")
                    .addProperty(property().name("single").type(type()
                            .typeKind(TypeKind.EXTERNAL_VALUE_OBJECT)
                            .typeRef("org.outside.MyEnum")
                            .cardinality(PropertyCardinality.SINGLE)))
                    .addProperty(property().name("list").type(type()
                            .typeKind(TypeKind.EXTERNAL_VALUE_OBJECT)
                            .typeRef("org.outside.MyEnum")
                            .cardinality(PropertyCardinality.LIST)
                    ))
            )
            .build();
    private CompiledCode compiled;

    @Before
    public void setUp() throws Exception {
        GenerationUtils.writeJavaFile(this.dir.getRoot(), "org.outside", TypeSpec.enumBuilder("MyEnum")
                .addModifiers(Modifier.PUBLIC)
                .addEnumConstant("V1")
                .addEnumConstant("V2")
                .build());
        new SpecCodeGenerator(this.spec, "org.generated", dir.getRoot()).generate();
        this.compiled = CompiledCode.builder().source(this.dir.getRoot()).compile();
    }

    @Test
    public void multipleProperty_multipleMethods() throws Exception {
        assertThat(compiled.getClass("org.generated.Val"), is(anInstance().public_().interface_()
                .with(anInstance().method().named("single").returning(this.compiled.getClass("org.outside.MyEnum")))
        ));
        assertThat(compiled.getClass("org.generated.ValImpl"), is(aPackagePrivate().class_()
                .with(anInstance().method().named("single").returning(this.compiled.getClass("org.outside.MyEnum")))
        ));
        assertThat(compiled.getClass("org.generated.Val$Builder"), is(aStatic().public_().class_()
                .with(anInstance().method().named("single").withParameters(this.compiled.getClass("org.outside.MyEnum")))
        ));


        assertThat(compiled.getClass("org.generated.Val"), is(anInstance().public_().interface_().with(anInstance().method().named("list"))));
        assertThat(compiled.getClass("org.generated.ValImpl"), is(aPackagePrivate().class_().with(anInstance().method().named("list"))));
        assertThat(compiled.getClass("org.generated.Val$Builder"), is(aStatic().public_().class_().with(anInstance().method().named("list"))));
    }
}
