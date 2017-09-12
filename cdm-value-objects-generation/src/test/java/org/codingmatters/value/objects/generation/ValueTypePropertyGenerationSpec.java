package org.codingmatters.value.objects.generation;

import org.codingmatters.tests.compile.CompiledCode;
import org.codingmatters.value.objects.spec.Spec;
import org.codingmatters.value.objects.spec.TypeKind;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.util.function.Consumer;

import static org.codingmatters.tests.reflect.ReflectMatchers.*;
import static org.codingmatters.value.objects.spec.PropertySpec.property;
import static org.codingmatters.value.objects.spec.PropertyTypeSpec.type;
import static org.codingmatters.value.objects.spec.Spec.spec;
import static org.codingmatters.value.objects.spec.ValueSpec.valueSpec;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 9/22/16.
 */
public class ValueTypePropertyGenerationSpec {

    @Rule
    public TemporaryFolder dir = new TemporaryFolder();

    private final Spec externalSpec = spec().addValue(valueSpec().name("externalValue")).build();

    private final Spec spec = spec()
            .addValue(valueSpec().name("val")
                    .addProperty(property().name("recursiveValue").type(type().typeRef("val").typeKind(TypeKind.IN_SPEC_VALUE_OBJECT)))
                    .addProperty(property().name("inSpecValue").type(type().typeRef("val2").typeKind(TypeKind.IN_SPEC_VALUE_OBJECT)))
                    .addProperty(property().name("outSpecValue").type(type().typeRef("org.external.value.ExternalValue").typeKind(TypeKind.EXTERNAL_VALUE_OBJECT)))
            )
            .addValue(valueSpec().name("val2"))
            .build();
    private CompiledCode compiled;

    @Before
    public void setUp() throws Exception {
        new SpecCodeGenerator(this.externalSpec, "org.external.value", dir.getRoot()).generate();
        new SpecCodeGenerator(this.spec, "org.generated", dir.getRoot()).generate();
        this.compiled = CompiledCode.builder().source(this.dir.getRoot()).compile();
    }

    @Test
    public void selfReferenceValueObject() throws Exception {
        assertThat(compiled.getClass("org.generated.Val"),
                is(anInterface()
                        .with(aMethod()
                                .named("recursiveValue")
                                .withoutParameters().returning(compiled.getClass("org.generated.Val"))
                        )
                        .with(aMethod()
                                .named("inSpecValue")
                                .withoutParameters().returning(compiled.getClass("org.generated.Val2"))
                        )
                ));
        assertThat(compiled.getClass("org.generated.ValImpl"),
                is(aPackagePrivate().class_()
                        .implementing(compiled.getClass("org.generated.Val"))
                ));
    }

    @Test
    public void builder_setterForValueProperty_withAValueArgument() throws Exception {
        assertThat(compiled.getClass("org.generated.Val$Builder"),
                is(aStatic().public_().class_()
                        .with(aMethod()
                                .named("recursiveValue")
                                .withParameters(compiled.getClass("org.generated.Val"))
                                .returning(compiled.getClass("org.generated.Val$Builder"))
                        )
                ));
    }

    @Test
    public void builder_setterForValueProperty_withAValueBuilderConsumerArgument() throws Exception {
        assertThat(compiled.getClass("org.generated.Val$Builder"),
                is(aStatic().public_().class_()
                        .with(aMethod()
                                .named("recursiveValue")
                                .withParameters(genericType().baseClass(Consumer.class).withParameters(classTypeParameter(compiled.getClass("org.generated.Val$Builder"))))
                                .returning(compiled.getClass("org.generated.Val$Builder"))
                        )
                ));
    }

    @Test
    public void externallyDefinedValueObject() throws Exception {
        assertThat(compiled.getClass("org.external.value.ExternalValue"), is(anInterface()));
        assertThat(compiled.getClass("org.generated.Val"),
                is(anInterface()
                        .with(aMethod()
                                .named("outSpecValue")
                                .withoutParameters().returning(compiled.getClass("org.external.value.ExternalValue"))
                        )
                ));
        assertThat(compiled.getClass("org.generated.Val$Builder"),
                is(aStatic().public_().class_()
                        .with(aMethod()
                                .named("outSpecValue")
                                .withParameters(compiled.getClass("org.external.value.ExternalValue"))
                                .returning(compiled.getClass("org.generated.Val$Builder"))
                        )
                ));
    }

    @Test
    public void valueBuilding() throws Exception {
        Object referenced = compiled.on(compiled.onClass("org.generated.Val2").invoke("builder")).invoke("build");

        Object builder = compiled.onClass("org.generated.Val").invoke("builder");
        compiled.on(builder).invoke("inSpecValue", compiled.getClass("org.generated.Val2")).with(referenced);
        Object value = compiled.on(builder).invoke("build");

        assertThat(value, is(notNullValue(compiled.getClass("org.generated.ValImpl"))));
        assertThat(compiled.on(value).castedTo("org.generated.Val").invoke("inSpecValue"), is(notNullValue(compiled.getClass("org.generated.Val2"))));
    }

    @Test
    public void valueBuildingWithNullBuilder() throws Exception {
        Object builder = compiled.onClass("org.generated.Val").invoke("builder");
        Object value = compiled.on(builder).invoke("build");

        assertThat(value, is(notNullValue(compiled.getClass("org.generated.ValImpl"))));
        assertThat(compiled.on(value).castedTo("org.generated.Val").invoke("inSpecValue"), is(nullValue()));
    }

    @Test
    public void valueBuildingWithExternallyDefinedValueObject() throws Exception {
        Object referenced = compiled.on(compiled.onClass("org.external.value.ExternalValue").invoke("builder")).invoke("build");

        Object builder = compiled.onClass("org.generated.Val").invoke("builder");
        compiled.on(builder).invoke("outSpecValue", compiled.getClass("org.external.value.ExternalValue")).with(referenced);
        Object value = compiled.on(builder).invoke("build");

        assertThat(value, is(notNullValue(compiled.getClass("org.generated.ValImpl"))));
        assertThat(compiled.on(value).castedTo("org.generated.Val").invoke("outSpecValue"), is(notNullValue(compiled.getClass("org.external.value.ExternalValue"))));
    }



}
