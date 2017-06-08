package org.codingmatters.value.objects.reader;

import org.codingmatters.value.objects.spec.PropertyCardinality;
import org.codingmatters.value.objects.spec.TypeKind;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.InputStream;
import java.time.DayOfWeek;

import static org.codingmatters.value.objects.spec.PropertySpec.property;
import static org.codingmatters.value.objects.spec.PropertyTypeSpec.type;
import static org.codingmatters.value.objects.spec.Spec.spec;
import static org.codingmatters.value.objects.spec.ValueSpec.valueSpec;
import static org.codingmatters.value.objects.utils.Utils.streamFor;
import static org.codingmatters.value.objects.utils.Utils.string;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 6/8/17.
 */
public class SpecReaderEnumPropertyTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private SpecReader reader = new SpecReader();

    @Test
    public void singleEnumPropertyWithValue() throws Exception {
        try(InputStream in = streamFor(string()
                .line("val:")
                .line("  p:")
                .line("    $enum: a, b, c")
                .build())) {
            assertThat(
                    reader.read(in),
                    is(
                            spec()
                                    .addValue(valueSpec().name("val")
                                            .addProperty(property().name("p").type(
                                                    type()
                                                            .typeKind(TypeKind.ENUM)
                                                            .cardinality(PropertyCardinality.SINGLE)
                                                            .enumValues("a", "b", "c")
                                                    )
                                            )
                                    )
                                    .build()
                    )
            );
        }
    }

    @Test
    public void singleEnumPropertyWithTypeRef() throws Exception {
        try(InputStream in = streamFor(string()
                .line("val:")
                .line("  p:")
                .line("    $enum:")
                .line("      $type: " + DayOfWeek.class.getName())
                .build())) {
            assertThat(
                    reader.read(in),
                    is(
                            spec()
                                    .addValue(valueSpec().name("val")
                                            .addProperty(property().name("p").type(
                                                    type()
                                                            .typeKind(TypeKind.ENUM)
                                                            .cardinality(PropertyCardinality.SINGLE)
                                                            .typeRef(DayOfWeek.class.getName())
                                                    )
                                            )
                                    )
                                    .build()
                    )
            );
        }
    }
}
