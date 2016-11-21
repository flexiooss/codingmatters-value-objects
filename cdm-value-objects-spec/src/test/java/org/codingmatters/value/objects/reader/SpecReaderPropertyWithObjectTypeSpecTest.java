package org.codingmatters.value.objects.reader;

import org.codingmatters.value.objects.spec.TypeKind;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.InputStream;

import static org.codingmatters.value.objects.spec.AnonymousValueSpec.anonymousValueSpec;
import static org.codingmatters.value.objects.spec.PropertySpec.property;
import static org.codingmatters.value.objects.spec.PropertyTypeSpec.type;
import static org.codingmatters.value.objects.spec.Spec.spec;
import static org.codingmatters.value.objects.spec.ValueSpec.valueSpec;
import static org.codingmatters.value.objects.utils.Utils.streamFor;
import static org.codingmatters.value.objects.utils.Utils.string;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 9/5/16.
 */
public class SpecReaderPropertyWithObjectTypeSpecTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private SpecReader reader = new SpecReader();

    @Test
    public void typeSpecWithTypeProperty() throws Exception {
        try(InputStream in = streamFor(string()
                .line("val:")
                .line("  p1: {$type: java.lang.String}")
                .line("  p2:")
                .line("    $type: $val")
                .build())) {
            assertThat(
                    reader.read(in),
                    is(
                            spec()
                                    .addValue(valueSpec().name("val")
                                            .addProperty(property().name("p1").type(type().typeRef("java.lang.String").typeKind(TypeKind.JAVA_TYPE)))
                                            .addProperty(property().name("p2").type(type().typeRef("val").typeKind(TypeKind.IN_SPEC_VALUE_OBJECT)))
                                    )
                                    .build()
                    )
            );
        }
    }

    @Test
    public void typeSpecWithValueObjectProperty() throws Exception {
        try(InputStream in = streamFor(string()
                .line("val:")
                .line("  p:")
                .line("    $value-object: org.codingmatters.ValueObject")
                .build())) {
            assertThat(
                    reader.read(in),
                    is(
                            spec()
                                    .addValue(valueSpec().name("val")
                                            .addProperty(property().name("p").type(type().typeRef("org.codingmatters.ValueObject").typeKind(TypeKind.EXTERNAL_VALUE_OBJECT)))
                                    )
                                    .build()
                    )
            );
        }
    }

    @Test
    public void typeSpecWithEmbeddedObjectProperty() throws Exception {
        try(InputStream in = streamFor(string()
                .line("val:")
                .line("  p:")
                .line("    p1: string")
                .line("    p2:")
                .line("      p3: string")
                .build())) {
            assertThat(
                    reader.read(in),
                    is(
                            spec()
                                    .addValue(valueSpec().name("val")
                                            .addProperty(property().name("p").type(type().typeKind(TypeKind.EMBEDDED)
                                                    .embeddedValueSpec(anonymousValueSpec()
                                                            .addProperty(property().name("p1").type(type().typeKind(TypeKind.JAVA_TYPE).typeRef(String.class.getName())))
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
                                    .build()
                    )
            );
        }
    }
}
