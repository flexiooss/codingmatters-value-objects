package org.codingmatters.value.objects.values.json;

import com.fasterxml.jackson.core.JsonFactory;
import org.codingmatters.value.objects.values.ObjectValue;
import org.codingmatters.value.objects.values.PropertyValue;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.is;

public class ObjectValueReaderTest {

    private JsonFactory jsonFactory = new JsonFactory();

    @Test
    public void singleNull() throws Exception {
        assertThat(
                new ObjectValueReader().read(jsonFactory.createParser("{\"prop\": null}")),
                is(ObjectValue.builder().property("prop", builder -> builder.stringValue(null)).build())
        );
    }

    @Test
    public void singleString() throws Exception {
        String json = "{\"prop\": \"str\"}";

        ObjectValue value = new ObjectValueReader().read(jsonFactory.createParser(json));
        assertThat(value, is(ObjectValue.builder().property("prop", builder -> builder.stringValue("str")).build()));
    }

    @Test
    public void singleStringWithNull() throws Exception {
        assertThat(
                new ObjectValueReader().read(jsonFactory.createParser("{\"prop\": \"null\"}")),
                is(ObjectValue.builder().property("prop", builder -> builder.stringValue("null")).build())
        );
    }

    @Test
    public void singleBoolean() throws Exception {
        assertThat(
                new ObjectValueReader().read(jsonFactory.createParser("{\"prop\": true}")),
                is(ObjectValue.builder().property("prop", builder -> builder.booleanValue(true)).build())
        );
        assertThat(
                new ObjectValueReader().read(jsonFactory.createParser("{\"prop\": false}")),
                is(ObjectValue.builder().property("prop", builder -> builder.booleanValue(false)).build())
        );
    }

    @Test
    public void singleLong() throws Exception {
        assertThat(
                new ObjectValueReader().read(jsonFactory.createParser("{\"prop\": 12}")),
                is(ObjectValue.builder().property("prop", builder -> builder.doubleValue(12d)).build())
        );
    }

    @Test
    public void longIsRetrievableAsDouble() throws Exception {
        ObjectValue read = new ObjectValueReader().read( jsonFactory.createParser( "{\"prop\": 12}" ) );
        assertThat(read.property( "prop" ).single().longValue(), is( 12L ));
        assertThat( read.property( "prop" ).single().doubleValue(), is( 12.0 ) );
    }

    @Test
    public void singleDouble() throws Exception {
        assertThat(
                new ObjectValueReader().read(jsonFactory.createParser("{\"prop\": 12.0}")),
                is(ObjectValue.builder().property("prop", builder -> builder.doubleValue(12.0)).build())
        );
    }

    @Test
    public void singleDoubleWithExponentialLiteral() throws Exception {
        assertThat(
                new ObjectValueReader().read(jsonFactory.createParser("{\"prop\": 8e+22}")),
                is(ObjectValue.builder().property("prop", builder -> builder.doubleValue(8e+22d)).build())
        );
    }

    @Test
    public void doubleIsRetrievableAsLong() throws Exception {
        ObjectValue read = new ObjectValueReader().read( jsonFactory.createParser( "{\"prop\": 12.4}" ) );
        assertThat(read.property( "prop" ).single().doubleValue(), is( 12.4 ));
        assertThat( read.property( "prop" ).single().longValue(), is( 12L ) );
    }

    @Test
    public void multipleString() throws Exception {
        String json = "{\"prop\": [\"str1\", \"str2\"]}";

        ObjectValue value = new ObjectValueReader().read(jsonFactory.createParser(json));
        assertThat(
                value,
                is(ObjectValue.builder()
                        .property("prop", PropertyValue.multiple(PropertyValue.Type.STRING,
                                builder -> builder.stringValue("str1"),
                                builder -> builder.stringValue("str2"))
                        )
                        .build()
                )
        );
    }

    @Test
    public void object() throws Exception {
        String json = "{\"prop\": {\"p\": \"v\"}, \"deep\": {\"prop\": {\"p\": \"v\"}}}";

        ObjectValue value = new ObjectValueReader().read(jsonFactory.createParser(json));
        assertThat(
                value,
                is(ObjectValue.builder()
                        .property("prop", PropertyValue.builder().objectValue(ObjectValue.builder().property("p", PropertyValue.builder().stringValue("v").build()).build()))
                        .property("deep", PropertyValue.builder().objectValue(
                                ObjectValue.builder().property("prop", PropertyValue.builder().objectValue(ObjectValue.builder().property("p", PropertyValue.builder().stringValue("v").build()).build())).build()
                        ).build())
                        .build()
                )
        );
    }


    @Test
    public void readArray() throws Exception {
        String json = "[{\"prop\": \"str\"}]";

        ObjectValue[] value = new ObjectValueReader().readArray(jsonFactory.createParser(json));
        assertThat(
                value,
                is(arrayContaining(ObjectValue.builder().property("prop", builder -> builder.stringValue("str")).build()))
        );
    }

    @Test
    public void readArrayWithNullElement() throws IOException {
        String json = "{\"objectList\":[{\"prop\": \"str\"}, null]}";
        ObjectValue value = new ObjectValueReader().read(jsonFactory.createParser(json));
        assertThat(
                value,
                is(ObjectValue.builder()
                        .property("objectList", PropertyValue.multiple(PropertyValue.Type.OBJECT,
                                val -> val.objectValue(ObjectValue.builder().property("prop", prop->prop.stringValue("str")).build()),
                                val -> val.objectValue((ObjectValue) null)
                        )).build()
                )
        );
    }

}