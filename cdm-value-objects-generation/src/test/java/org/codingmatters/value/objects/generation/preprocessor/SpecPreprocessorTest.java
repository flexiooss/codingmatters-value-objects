package org.codingmatters.value.objects.generation.preprocessor;

import org.codingmatters.value.objects.spec.Spec;
import org.codingmatters.value.objects.spec.TypeKind;
import org.junit.Test;

import java.util.List;

import static org.codingmatters.value.objects.spec.PropertySpec.property;
import static org.codingmatters.value.objects.spec.PropertyTypeSpec.type;
import static org.codingmatters.value.objects.spec.Spec.spec;
import static org.codingmatters.value.objects.spec.ValueSpec.valueSpec;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 11/23/16.
 */
public class SpecPreprocessorTest {

    @Test
    public void noEmbeddedType_noAdditionalValueType() throws Exception {
        Spec spec = spec()
                .addValue(
                        valueSpec().name("val")
                                .addProperty(property().name("prop").type(type().typeKind(TypeKind.JAVA_TYPE).typeRef(String.class.getName())))
                                .addProperty(property().name("prop2").type(type().typeKind(TypeKind.JAVA_TYPE).typeRef(String.class.getName())))
                )
                .build();
        List<PackagedValueSpec> actual = new SpecPreprocessor(spec, "org.generated").packagedValueSpec();

        assertThat(actual, hasSize(1));
        assertThat(actual.get(0).packagename(), is("org.generated"));
        assertThat(actual.get(0).valueSpec(), is(spec.valueSpecs().get(0)));
    }

}