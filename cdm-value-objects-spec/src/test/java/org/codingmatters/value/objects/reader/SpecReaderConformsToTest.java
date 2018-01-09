package org.codingmatters.value.objects.reader;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.InputStream;

import static org.codingmatters.value.objects.spec.Spec.spec;
import static org.codingmatters.value.objects.spec.ValueSpec.valueSpec;
import static org.codingmatters.value.objects.utils.Utils.streamFor;
import static org.codingmatters.value.objects.utils.Utils.string;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SpecReaderConformsToTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private SpecReader reader = new SpecReader();

    @Test
    public void emptyConformsTo() throws Exception {
        try(InputStream in = streamFor(string()
                .line("val:")
                .line("  $conforms-to: ")
                .build())) {
            assertThat(
                    reader.read(in),
                    is(
                            spec()
                                    .addValue(valueSpec().name("val"))
                                    .build()
                    )
            );
        }
    }

    @Test
    public void singleConformsTo() throws Exception {
        try(InputStream in = streamFor(string()
                .line("val:")
                .line("  $conforms-to: org.package.Protocol")
                .build())) {
            assertThat(
                    reader.read(in),
                    is(
                            spec()
                                    .addValue(valueSpec().name("val")
                                            .addConformsTo("org.package.Protocol")
                                    )
                                    .build()
                    )
            );
        }
    }

    @Test
    public void multipleConformsTo() throws Exception {
        try(InputStream in = streamFor(string()
                .line("val:")
                .line("  $conforms-to:")
                .line("    - org.package.Protocol1")
                .line("    - org.package.Protocol2")
                .line("    - org.package.Protocol3")
                .build())) {
            assertThat(
                    reader.read(in),
                    is(
                            spec()
                                    .addValue(valueSpec().name("val")
                                            .addConformsTo(
                                                    "org.package.Protocol1",
                                                    "org.package.Protocol2",
                                                    "org.package.Protocol3"
                                            )
                                    )
                                    .build()
                    )
            );
        }
    }

}
