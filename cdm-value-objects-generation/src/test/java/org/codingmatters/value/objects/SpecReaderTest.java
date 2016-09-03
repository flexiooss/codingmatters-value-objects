package org.codingmatters.value.objects;

import org.codingmatters.value.objects.spec.PropertyType;
import org.junit.Test;

import java.io.InputStream;

import static org.codingmatters.value.objects.spec.PropertySpec.property;
import static org.codingmatters.value.objects.spec.Spec.spec;
import static org.codingmatters.value.objects.spec.ValueSpec.valueSpec;
import static org.codingmatters.value.objects.utils.Utils.streamFor;
import static org.codingmatters.value.objects.utils.Utils.string;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 9/3/16.
 */
public class SpecReaderTest {
    private SpecReader reader = new SpecReader();

    @Test
    public void manySimpleValueSpec() throws Exception {
        try(InputStream in = streamFor(string()
                .line("val1:")
                .line("val2:")
                .build())) {
            assertThat(
                    reader.read(in),
                    is(
                            spec()
                                    .addValue(valueSpec().name("val1"))
                                    .addValue(valueSpec().name("val2"))
                            .build()
                    )
            );
        }
    }

    @Test
    public void valueWithStringProperties() throws Exception {
        try(InputStream in = streamFor(string()
                .line("val:")
                .line("  p1: string")
                .line("  p2: string")
                .build())) {
            assertThat(
                    reader.read(in),
                    is(
                            spec()
                                    .addValue(valueSpec().name("val")
                                            .addProperty(property().name("p1").type(PropertyType.STRING))
                                            .addProperty(property().name("p2").type(PropertyType.STRING))
                                    )
                                    .build()
                    )
            );
        }
    }
}
