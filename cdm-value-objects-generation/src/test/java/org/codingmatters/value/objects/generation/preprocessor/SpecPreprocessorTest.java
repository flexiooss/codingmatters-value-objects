package org.codingmatters.value.objects.generation.preprocessor;

import org.codingmatters.value.objects.spec.Spec;
import org.codingmatters.value.objects.spec.TypeKind;
import org.junit.Test;

import java.util.List;

import static org.codingmatters.value.objects.spec.AnonymousValueSpec.anonymousValueSpec;
import static org.codingmatters.value.objects.spec.PropertySpec.property;
import static org.codingmatters.value.objects.spec.PropertyTypeSpec.type;
import static org.codingmatters.value.objects.spec.Spec.spec;
import static org.codingmatters.value.objects.spec.ValueSpec.valueSpec;
import static org.hamcrest.Matchers.*;
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

    @Test
    public void embeddedType_introduceValueTypeToPropertyPackage() throws Exception {
        Spec spec  = spec()
                .addValue(valueSpec().name("val")
                        .addProperty(property().name("p").type(type().typeKind(TypeKind.EMBEDDED)
                                .embeddedValueSpec(anonymousValueSpec()
                                        .addProperty(property().name("p1").type(type().typeKind(TypeKind.JAVA_TYPE).typeRef(String.class.getName())))
                                        .build()
                                )
                        ))
                )
                .build();

        List<PackagedValueSpec> actual = new SpecPreprocessor(spec, "org.generated").packagedValueSpec();

        assertThat(actual, hasSize(2));

        PackagedValueSpec val = null;
        PackagedValueSpec p = null;
        for (PackagedValueSpec packagedValueSpec : actual) {
            if(packagedValueSpec.valueSpec().name().equals("val")) {
                val = packagedValueSpec;
            }
            if(packagedValueSpec.valueSpec().name().equals("p")) {
                p = packagedValueSpec;
            }
        }

        assertThat(val, is(notNullValue()));
        assertThat(val.packagename(), is("org.generated"));
        assertThat(val.valueSpec(), is(
                valueSpec().name("val")
                        .addProperty(property().name("p").type(type().typeKind(TypeKind.EXTERNAL_VALUE_OBJECT).typeRef("org.generated.val.P")))
                        .build()
        ));

        assertThat(p, is(notNullValue()));
        assertThat(p.packagename(), is("org.generated.val"));
        assertThat(p.valueSpec(), is(
                valueSpec().name("p")
                        .addProperty(property().name("p1").type(type().typeKind(TypeKind.JAVA_TYPE).typeRef(String.class.getName())))
                        .build()
        ));
    }

    @Test
    public void preprocessingIsRecursive() throws Exception {
        Spec spec  = spec()
                .addValue(valueSpec().name("val")
                        .addProperty(property().name("p").type(type().typeKind(TypeKind.EMBEDDED)
                                .embeddedValueSpec(anonymousValueSpec()
                                        .addProperty(property().name("p2").type(type().typeKind(TypeKind.EMBEDDED)
                                                .embeddedValueSpec(anonymousValueSpec()
                                                        .addProperty(property().name("p3").type(type().typeKind(TypeKind.JAVA_TYPE).typeRef(String.class.getName())).build())
                                                        .build()
                                                )
                                        ))
                                        .build()
                                )
                        ))
                )
                .build();

        List<PackagedValueSpec> actual = new SpecPreprocessor(spec, "org.generated").packagedValueSpec();

        assertThat(actual, hasSize(3));
    }
}