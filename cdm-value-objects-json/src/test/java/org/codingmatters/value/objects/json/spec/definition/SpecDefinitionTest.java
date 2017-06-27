package org.codingmatters.value.objects.json.spec.definition;

import org.codingmatters.value.objects.json.value.explore.ExampleValueReader;
import org.codingmatters.value.objects.json.value.explore.ExampleValueWriter;
import org.generated.ExampleValue;
import org.generated.examplevalue.Complex;
import org.generated.examplevalue.ComplexList;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 3/25/17.
 */
public class SpecDefinitionTest {

    @Test
    public void writeSimpleProperties() throws Exception {
        assertThat(
                new ExampleValueWriter().write(ExampleValue.builder()
                        .prop("a value")
                        .listProp("a", "b", "c")
                        .build()),
                is("{" +
                        "\"prop\":\"a value\"," +
                        "\"listProp\":[\"a\",\"b\",\"c\"]," +
                        "\"complex\":null," +
                        "\"complexList\":null" +
                        "}")
        );
    }

    @Test
    public void writeNullSimpleProperties() throws Exception {
        assertThat(
                new ExampleValueWriter().write(ExampleValue.builder()
                        .prop(null)
                        .listProp()
                        .build()),
                is("{" +
                        "\"prop\":null," +
                        "\"listProp\":null," +
                        "\"complex\":null," +
                        "\"complexList\":null" +
                        "}")
        );
    }

    @Test
    public void writeComplexProperties() throws Exception {
        assertThat(
                new ExampleValueWriter().write(ExampleValue.builder()
                        .prop(null)
                        .listProp()
                        .complex(Complex.builder()
                                .sub("value")
                                .build())
                        .complexList(
                                ComplexList.builder()
                                        .sub("value1")
                                        .build(),
                                ComplexList.builder()
                                        .sub("value2")
                                        .build()
                        )
                        .build()),
                is("{" +
                        "\"prop\":null," +
                        "\"listProp\":null," +
                        "\"complex\":{\"sub\":\"value\"}," +
                        "\"complexList\":[{\"sub\":\"value1\"},{\"sub\":\"value2\"}]" +
                        "}")
        );
    }

    @Test
    public void readSimpleProperties() throws Exception {
        assertThat(
                new ExampleValueReader().read("{" +
                        "\"prop\":\"a value\"," +
                        "\"listProp\":[\"a\",\"b\",\"c\"]," +
                        "\"complex\":null," +
                        "\"complexList\":null" +
                        "}"),
                is(ExampleValue.builder()
                        .prop("a value")
                        .listProp("a", "b", "c")
                        .build())
        );
    }

    @Test
    public void readNullSimpleProperties() throws Exception {
        assertThat(
                new ExampleValueReader().read("{" +
                        "\"prop\":null," +
                        "\"listProp\":null," +
                        "\"complex\":null," +
                        "\"complexList\":null" +
                        "}"),
                is(ExampleValue.builder()
                        .prop(null)
                        .listProp()
                        .build())
        );
    }

    @Test
    public void readComplexProperties() throws Exception {
        assertThat(
                new ExampleValueReader().read("{" +
                        "\"prop\":null," +
                        "\"listProp\":null," +
                        "\"complex\":{\"sub\":\"value\"}," +
                        "\"complexList\":[{\"sub\":\"value1\"},{\"sub\":\"value2\"}]" +
                        "}"),
                is(ExampleValue.builder()
                        .prop(null)
                        .listProp()
                        .complex(Complex.builder()
                                .sub("value")
                                .build())
                        .complexList(
                                ComplexList.builder()
                                        .sub("value1")
                                        .build(),
                                ComplexList.builder()
                                        .sub("value2")
                                        .build()
                        )
                        .build())
        );
    }


}
