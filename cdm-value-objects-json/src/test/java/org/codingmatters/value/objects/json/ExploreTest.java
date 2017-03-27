package org.codingmatters.value.objects.json;

import org.codingmatters.value.objects.json.explore.ExampleValueReader;
import org.codingmatters.value.objects.json.explore.ExampleValueWriter;
import org.codingmatters.value.objects.json.value.ExampleValue;
import org.codingmatters.value.objects.json.value.examplevalue.Complex;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 3/25/17.
 */
public class ExploreTest {

    @Test
    public void writeSimpleProperties() throws Exception {
        assertThat(
                new ExampleValueWriter().writeValue(ExampleValue.Builder.builder()
                        .prop("a value")
                        .listProp("a", "b", "c")
                        .build()),
                is("{" +
                        "\"prop\":\"a value\"," +
                        "\"listProp\":[\"a\",\"b\",\"c\"]," +
                        "\"complex\":null" +
                        "}")
        );
    }

    @Test
    public void writeNullSimpleProperties() throws Exception {
        assertThat(
                new ExampleValueWriter().writeValue(ExampleValue.Builder.builder()
                        .prop(null)
                        .listProp()
                        .build()),
                is("{" +
                        "\"prop\":null," +
                        "\"listProp\":null," +
                        "\"complex\":null" +
                        "}")
        );
    }

    @Test
    public void writeComplexProperties() throws Exception {
        assertThat(
                new ExampleValueWriter().writeValue(ExampleValue.Builder.builder()
                        .prop(null)
                        .listProp()
                        .complex(Complex.Builder.builder()
                                .sub("value")
                                .build())
                        .build()),
                is("{" +
                        "\"prop\":null," +
                        "\"listProp\":null," +
                        "\"complex\":{\"sub\":\"value\"}" +
                        "}")
        );
    }

    @Test
    public void readSimpleProperties() throws Exception {
        assertThat(
                new ExampleValueReader().readValue("{" +
                        "\"prop\":\"a value\"," +
                        "\"listProp\":[\"a\",\"b\",\"c\"]," +
                        "\"complex\":null" +
                        "}"),
                is(ExampleValue.Builder.builder()
                        .prop("a value")
                        .listProp("a", "b", "c")
                        .build())
        );
    }

    @Test
    public void readNullSimpleProperties() throws Exception {
        assertThat(
                new ExampleValueReader().readValue("{" +
                        "\"prop\":null," +
                        "\"listProp\":null," +
                        "\"complex\":null" +
                        "}"),
                is(ExampleValue.Builder.builder()
                        .prop(null)
                        .listProp()
                        .build())
        );
    }

    @Test
    public void readComplexProperties() throws Exception {
        assertThat(
                new ExampleValueReader().readValue("{" +
                        "\"prop\":null," +
                        "\"listProp\":null," +
                        "\"complex\":{\"sub\":\"value\"}" +
                        "}"),
                is(ExampleValue.Builder.builder()
                        .prop(null)
                        .listProp()
                        .complex(Complex.Builder.builder()
                                .sub("value")
                                .build())
                        .build())
        );
    }


}
