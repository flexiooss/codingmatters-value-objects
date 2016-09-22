package org.codingmatters.value.objects.generation;

import org.codingmatters.tests.compile.CompiledCode;
import org.codingmatters.value.objects.spec.Spec;
import org.codingmatters.value.objects.spec.TypeKind;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.codingmatters.value.objects.spec.PropertySpec.property;
import static org.codingmatters.value.objects.spec.PropertyTypeSpec.type;
import static org.codingmatters.value.objects.spec.Spec.spec;
import static org.codingmatters.value.objects.spec.ValueSpec.valueSpec;

/**
 * Created by nelt on 9/22/16.
 */
public class ValueTypePropertyGenerationSpec {

    @Rule
    public TemporaryFolder dir = new TemporaryFolder();

    private final Spec externalSpec = spec().addValue(valueSpec().name("externalValue")).build();

    private final Spec spec = spec()
            .addValue(valueSpec().name("val")
                    .addProperty(property().name("selfReference").type(type().typeRef("val").typeKind(TypeKind.IN_SPEC_VALUE_OBJECT)))
                    .addProperty(property().name("inSpecfReference").type(type().typeRef("val2").typeKind(TypeKind.IN_SPEC_VALUE_OBJECT)))
                    .addProperty(property().name("inSpecfReference").type(type().typeRef("org.external.value.ExternalValue").typeKind(TypeKind.EXTERNAL_VALUE_OBJECT)))
            )
            .addValue(valueSpec().name("val2"))
            .build();
    private CompiledCode compiled;

    @Before
    public void setUp() throws Exception {
//        new SpecCodeGenerator(this.externalSpec, "org.external.value").generateTo(dir.getRoot());
//        new SpecCodeGenerator(this.spec, "org.generated").generateTo(dir.getRoot());
//        this.compiled = CompiledCode.compile(this.dir.getRoot());
    }

    @Test
    public void selfReferenceValueObject() throws Exception {

    }
}
