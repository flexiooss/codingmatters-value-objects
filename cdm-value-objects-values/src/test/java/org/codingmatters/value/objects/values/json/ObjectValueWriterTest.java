package org.codingmatters.value.objects.values.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.codingmatters.value.objects.values.ObjectValue;
import org.codingmatters.value.objects.values.PropertyValue;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ObjectValueWriterTest {

    private JsonFactory jsonFactory = new JsonFactory();

    @Test
    public void onlyNull() throws Exception {
        assertThat(
                this.write(ObjectValue.builder().property("prop", builder -> builder.stringValue(null))),
                is("{\"prop\":null}")
        );
    }

    @Test
    public void singleNull() throws Exception {
        assertThat(
                this.write(ObjectValue.builder().property("prop", (PropertyValue) null)),
                is("{\"prop\":null}")
        );
    }

    @Test
    public void singleString() throws Exception {
        assertThat(
                this.write(ObjectValue.builder().property("prop", builder -> builder.stringValue("str"))),
                is("{\"prop\":\"str\"}")
        );
    }

    @Test
    public void singleStringWithNull() throws Exception {
        assertThat(
                this.write(ObjectValue.builder().property("prop", builder -> builder.stringValue("null"))),
                is("{\"prop\":\"null\"}")
        );
    }

    @Test
    public void singleBoolean() throws Exception {
        assertThat(
                this.write(ObjectValue.builder().property("prop", builder -> builder.booleanValue(true))),
                is("{\"prop\":true}")
        );
        assertThat(
                this.write(ObjectValue.builder().property("prop", builder -> builder.booleanValue(false))),
                is("{\"prop\":false}")
        );
    }

    @Test
    public void singleLong() throws Exception {
        assertThat(
                this.write(ObjectValue.builder().property("prop", builder -> builder.longValue(12L))),
                is("{\"prop\":12}")
        );
    }

    @Test
    public void singleDouble() throws Exception {
        assertThat(
                this.write(ObjectValue.builder().property("prop", builder -> builder.doubleValue(12.0))),
                is("{\"prop\":12.0}")
        );
    }


    @Test
    public void multipleString() throws Exception {
        assertThat(
                this.write(
                        ObjectValue.builder()
                                .property("prop", PropertyValue.multiple(PropertyValue.Type.STRING,
                                        builder -> builder.stringValue("str1"),
                                        builder -> builder.stringValue("str2"))
                                )
                ),
                is("{\"prop\":[\"str1\",\"str2\"]}")
        );
    }

    @Test
    public void object() throws Exception {
        assertThat(
                this.write(ObjectValue.builder()
                        .property("prop", PropertyValue.builder().objectValue(ObjectValue.builder().property("p", PropertyValue.builder().stringValue("v").build()).build()))
                        .property("deep", PropertyValue.builder().objectValue(
                                ObjectValue.builder().property("prop", PropertyValue.builder().objectValue(ObjectValue.builder().property("p", PropertyValue.builder().stringValue("v").build()).build())).build()
                        ).build())),
                is("{\"deep\":{\"prop\":{\"p\":\"v\"}},\"prop\":{\"p\":\"v\"}}")
        );
    }

    @Test
    public void nullObject() throws Exception {
        assertThat(this.write((ObjectValue) null), is("null"));
    }

    @Test
    public void nullObjectInList() throws Exception {
        assertThat(this.write(
                        ObjectValue.builder()
                                .property("list", PropertyValue.multiple(PropertyValue.Type.OBJECT, val -> val.objectValue((ObjectValue) null)))
                                .build()),
                is("{\"list\":[null]}")
        );

        assertThat(this.write(
                        ObjectValue.builder()
                                .property("list", PropertyValue.multiple(PropertyValue.Type.OBJECT,
                                        val -> val.objectValue((ObjectValue) null),
                                        val -> val.objectValue(ObjectValue.builder().property("a", a -> a.longValue(12L)))
                                )).build()),
                is("{\"list\":[null,{\"a\":12}]}")
        );
    }

    private String write(ObjectValue.Builder builder) throws IOException {
        return this.write(builder.build());
    }

    private String write(ObjectValue value) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator generator = this.jsonFactory.createGenerator(out)) {
            new ObjectValueWriter().write(generator,
                    value
            );
        }
        return out.toString();
    }
}
