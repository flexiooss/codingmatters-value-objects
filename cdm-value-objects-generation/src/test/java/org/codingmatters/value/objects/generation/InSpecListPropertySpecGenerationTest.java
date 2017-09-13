package org.codingmatters.value.objects.generation;

import org.codingmatters.tests.compile.CompiledCode;
import org.codingmatters.tests.compile.FileHelper;
import org.codingmatters.value.objects.spec.PropertyCardinality;
import org.codingmatters.value.objects.spec.Spec;
import org.codingmatters.value.objects.spec.TypeKind;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.util.Collection;
import java.util.function.Consumer;

import static org.codingmatters.tests.reflect.ReflectMatchers.*;
import static org.codingmatters.value.objects.spec.PropertySpec.property;
import static org.codingmatters.value.objects.spec.PropertyTypeSpec.type;
import static org.codingmatters.value.objects.spec.Spec.spec;
import static org.codingmatters.value.objects.spec.ValueSpec.valueSpec;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 11/23/16.
 */
public class InSpecListPropertySpecGenerationTest {

    @Rule
    public TemporaryFolder dir = new TemporaryFolder();

    @Rule
    public FileHelper fileHelper = new FileHelper();

    private final Spec spec  = spec()
            .addValue(
                    valueSpec().name("ref")
            )
            .addValue(
                    valueSpec().name("val")
                            .addProperty(property().name("listProp").type(type()
                                    .typeRef("ref")
                                    .typeKind(TypeKind.IN_SPEC_VALUE_OBJECT)
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
    public void builderMethods() throws Exception {
        assertThat(this.compiled.getClass("org.generated.Val$Builder"), is(aStatic().class_()
                .with(aPublic().method()
                        .named("listProp")
                        .withParameters(typeArray(classType(this.compiled.getClass("org.generated.Ref"))))
                        .returning(this.compiled.getClass("org.generated.Val$Builder"))
                )
                .with(aPublic().method()
                        .named("listProp")
                        .withParameters(genericType().baseClass(this.compiled.getClass("org.generated.ValueList"))
                                .withParameters(typeParameter().named("org.generated.Ref")))
                        .returning(this.compiled.getClass("org.generated.Val$Builder"))
                )
                .with(aPublic().method()
                        .named("listProp")
                        .withParameters(genericType().baseClass(Collection.class)
                                .withParameters(typeParameter().named("org.generated.Ref")))
                        .returning(this.compiled.getClass("org.generated.Val$Builder"))
                )
        ));
    }

    @Test
    public void builderMethod_withConsumer() throws Exception {
//        this.fileHelper.printJavaContent("", this.dir.getRoot());
//        this.fileHelper.printFile(this.dir.getRoot(), "Val.java");

        assertThat(this.compiled.getClass("org.generated.Val$Builder"), is(aStatic().class_()
                .with(aMethod()
                        .named("listProp")
                        .withParameters(typeArray(genericType().baseClass(Consumer.class).withParameters(classTypeParameter(compiled.getClass("org.generated.Ref$Builder")))))
                        .returning(compiled.getClass("org.generated.Val$Builder"))
                )
        ));
    }
}
