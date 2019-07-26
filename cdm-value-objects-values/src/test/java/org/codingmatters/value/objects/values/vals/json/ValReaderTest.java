package org.codingmatters.value.objects.values.vals.json;

import com.fasterxml.jackson.core.JsonFactory;
import org.codingmatters.value.objects.values.vals.Val;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ValReaderTest {

    private JsonFactory jsonFactory = new JsonFactory();

    @Test
    public void singleNull() throws Exception {
        assertThat(
                new ValReader().read(jsonFactory.createParser("{\"prop\": null}")),
                is(Val.object().property("prop", null).build())
        );
    }

    @Test
    public void singleString() throws Exception {
        String json = "{\"prop\": \"str\"}";

        assertThat(
                new ValReader().read(jsonFactory.createParser(json)),
                is(Val.object().property("prop", Val.stringValue("str")).build())
        );
    }

    @Test
    public void singleStringWithNull() throws Exception {
        assertThat(
                new ValReader().read(jsonFactory.createParser("{\"prop\": \"null\"}")),
                is(Val.object().property("prop", Val.stringValue("null")).build())
        );
    }

    @Test
    public void singleBoolean() throws Exception {
        assertThat(
                new ValReader().read(jsonFactory.createParser("{\"prop\": true}")),
                is(Val.object().property("prop", Val.booleanValue(true)).build())
        );
        assertThat(
                new ValReader().read(jsonFactory.createParser("{\"prop\": false}")),
                is(Val.object().property("prop", Val.booleanValue(false)).build())
        );
    }

    @Test
    public void singleLong() throws Exception {
        assertThat(
                new ValReader().read(jsonFactory.createParser("{\"prop\": 12}")),
                is(Val.object().property("prop", Val.longValue(12L)).build())
        );
    }

    @Test
    public void singleDouble() throws Exception {
        assertThat(
                new ValReader().read(jsonFactory.createParser("{\"prop\": 12.0}")),
                is(Val.object().property("prop", Val.doubleValue(12.0)).build())
        );
    }

    @Test
    public void multipleString() throws Exception {
        String json = "{\"prop\": [\"str1\", \"str2\"]}";

        assertThat(
                new ValReader().read(jsonFactory.createParser(json)),
                is(Val.object()
                        .property("prop", Val.array().values(Val.stringValue("str1"), Val.stringValue("str2")).build())
                        .build())
        );
    }

    @Test
    public void object() throws Exception {
        String json = "{\"prop\": {\"p\": \"v\"}, \"deep\": {\"prop\": {\"p\": \"v\"}}}";

        assertThat(
                new ValReader().read(jsonFactory.createParser(json)),
                is(Val.object()
                        .property("prop", Val.object().property("p", Val.stringValue("v")).build())
                        .property("deep", Val.object()
                                .property("prop", Val.object().property("p", Val.stringValue("v")).build())
                                .build())
                        .build()
                )
        );
    }

    @Test
    public void readArray() throws Exception {
        String json = "[{\"prop\": \"str\"}]";

        assertThat(
                new ValReader().read(jsonFactory.createParser(json)),
                is(Val.array()
                        .with(Val.object().property("prop", Val.stringValue("str")).build())
                        .build()
                )
        );
    }

    @Test
    public void readNestedArray() throws Exception {
        String json = "[[{\"p1\": \"v1\"}],[[{\"p2\": \"v2\"},{\"p3\": \"v3\"}]]]";

        assertThat(
                new ValReader().read(jsonFactory.createParser(json)),
                is(Val.array()
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
                )
        );
    }

    @Test
    public void mixedArray() throws Exception {
        String json = "[\"str\",12,{\"p\":\"v\"},[42.0]]";

        assertThat(
                new ValReader().read(jsonFactory.createParser(json)),
                is(Val.array()
                        .with(Val.stringValue("str"))
                        .with(Val.longValue(12L))
                        .with(Val.object().property("p", Val.stringValue("v")).build())
                        .with(Val.array().with(Val.doubleValue(42.0)).build())
                        .build()
                )
        );
    }
}