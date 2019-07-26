package org.codingmatters.value.objects.values.vals.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.codingmatters.value.objects.values.ObjectValue;
import org.codingmatters.value.objects.values.PropertyValue;
import org.codingmatters.value.objects.values.json.ObjectValueWriter;
import org.codingmatters.value.objects.values.vals.Val;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class ValWriterTest {

    private JsonFactory jsonFactory = new JsonFactory();

    @Test
    public void singleNull() throws Exception {
        assertThat(
                this.write(Val.object().property("prop", null).build()),
                is("{\"prop\":null}")
        );
    }

    @Test
    public void singleString() throws Exception {
        assertThat(
                this.write(Val.object().property("prop", Val.stringValue("str")).build()),
                is("{\"prop\":\"str\"}")
        );
    }

    @Test
    public void singleStringWithNull() throws Exception {
        assertThat(
                this.write(Val.object().property("prop", Val.stringValue("null")).build()),
                is("{\"prop\":\"null\"}")
        );
    }

    @Test
    public void singleBoolean() throws Exception {
        assertThat(
                this.write(Val.object().property("prop", Val.booleanValue(true)).build()),
                is("{\"prop\":true}")
        );
        assertThat(
                this.write(Val.object().property("prop", Val.booleanValue(false)).build()),
                is("{\"prop\":false}")
        );
    }

    @Test
    public void singleLong() throws Exception {
        assertThat(
                this.write(Val.object().property("prop", Val.longValue(12L)).build()),
                is("{\"prop\":12}")
        );
    }

    @Test
    public void singleDouble() throws Exception {
        assertThat(
                this.write(Val.object().property("prop", Val.doubleValue(12.0)).build()),
                is("{\"prop\":12.0}")
        );
    }



    @Test
    public void multipleString() throws Exception {
        assertThat(
                this.write(Val.object()
                        .property("prop", Val.array().values(Val.stringValue("str1"), Val.stringValue("str2")).build())
                        .build()),
                is("{\"prop\":[\"str1\",\"str2\"]}")
        );
    }

    @Test
    public void object() throws Exception {
        assertThat(
                this.write(Val.object()
                        .property("deep", Val.object()
                                .property("prop", Val.object()
                                        .property("p", Val.stringValue("v"))
                                        .build())
                                .build())
                        .property("prop", Val.object()
                                .property("p", Val.stringValue("v"))
                                .build())
                        .build()),
                is("{\"deep\":{\"prop\":{\"p\":\"v\"}},\"prop\":{\"p\":\"v\"}}")
        );
    }




    @Test
    public void array() throws Exception {

        assertThat(
                this.write(Val.array()
                        .with(Val.object().property("prop", Val.stringValue("str")).build())
                        .build()
                ),
                is("[{\"prop\":\"str\"}]")
        );
    }

    @Test
    public void readNestedArray() throws Exception {
        assertThat(
                this.write(Val.array()
                        .with(Val.array()
                                .with(Val.object().property("p1", Val.stringValue("v1")).build())
                                .build())
                        .with(Val.array()
                                .with(Val.array()
                                        .with(Val.object()
                                                .property("p2", Val.stringValue("v2"))
                                                .build())
                                        .with(Val.object()
                                                .property("p3", Val.stringValue("v3"))
                                                .build())
                                        .build())
                                .build())
                        .build()
                ),
                is("[[{\"p1\":\"v1\"}],[[{\"p2\":\"v2\"},{\"p3\":\"v3\"}]]]")
        );
    }

    @Test
    public void mixedArray() throws Exception {
        assertThat(
                this.write(Val.array()
                        .with(Val.stringValue("str"))
                        .with(Val.longValue(12L))
                        .with(Val.object().property("p", Val.stringValue("v")).build())
                        .with(Val.array().with(Val.doubleValue(42.0)).build())
                        .build()
                ),
                is("[\"str\",12,{\"p\":\"v\"},[42.0]]")
        );
    }




    private String write(Val value) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try(JsonGenerator generator = this.jsonFactory.createGenerator(out)) {
            new ValWriter().write(generator,value);
        }
        return out.toString();
    }
}