package org.codingmatters.value.objects.values.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.codingmatters.value.objects.values.ObjectValue;
import org.codingmatters.value.objects.values.PropertyValue;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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

    @Test
    public void testAllTypesAndAllTypesMultipleWithNullValues() throws IOException {
        ObjectValue.Builder builder = ObjectValue.builder();
        builder
                .property("str-simple", PropertyValue.builder().stringValue("plok"))
                .property("str-simple-null-1", PropertyValue.builder().stringValue(null))
                .property("str-simple-null-2", PropertyValue.builder().build())
                .property("str-simple-null-3", (PropertyValue) null)
                .property("str-multiple", PropertyValue.multiple(PropertyValue.Type.STRING,
                        PropertyValue.builder().stringValue("plok").buildValue(),
                        PropertyValue.builder().stringValue(null).buildValue(),
                        (PropertyValue.Value) null
                ))
                .property("str-multiple-null-1", PropertyValue.builder().build())
                .property("str-multiple-null-2", (PropertyValue) null);

        builder
                .property("long-simple", PropertyValue.builder().longValue(1L))
                .property("long-simple-null-1", PropertyValue.builder().longValue(null))
                .property("long-simple-null-2", PropertyValue.builder().build())
                .property("long-simple-null-3", (PropertyValue) null)
                .property("long-multiple", PropertyValue.multiple(PropertyValue.Type.LONG,
                        PropertyValue.builder().longValue(1L).buildValue(),
                        PropertyValue.builder().longValue(null).buildValue(),
                        (PropertyValue.Value) null
                ))
                .property("long-multiple-null-1", PropertyValue.builder().build())
                .property("long-multiple-null-2", (PropertyValue) null);

        builder
                .property("double-simple", PropertyValue.builder().doubleValue(1.1))
                .property("double-simple-null-1", PropertyValue.builder().doubleValue(null))
                .property("double-simple-null-2", PropertyValue.builder().build())
                .property("double-simple-null-3", (PropertyValue) null)
                .property("double-multiple", PropertyValue.multiple(PropertyValue.Type.DOUBLE,
                        PropertyValue.builder().doubleValue(1.1).buildValue(),
                        PropertyValue.builder().doubleValue(null).buildValue(),
                        (PropertyValue.Value) null
                ))
                .property("double-multiple-null-1", PropertyValue.builder().build())
                .property("double-multiple-null-2", (PropertyValue) null);

        LocalDate now = LocalDate.parse("2024-12-17");
        builder
                .property("date-simple", PropertyValue.builder().dateValue(now))
                .property("date-simple-null-1", PropertyValue.builder().dateValue(null))
                .property("date-simple-null-2", PropertyValue.builder().build())
                .property("date-simple-null-3", (PropertyValue) null)
                .property("date-multiple", PropertyValue.multiple(PropertyValue.Type.DATE,
                        PropertyValue.builder().dateValue(now).buildValue(),
                        PropertyValue.builder().dateValue(null).buildValue(),
                        (PropertyValue.Value) null
                ))
                .property("date-multiple-null-1", PropertyValue.builder().build())
                .property("date-multiple-null-2", (PropertyValue) null);

        LocalDateTime nowDT = LocalDateTime.parse("2024-12-17T09:31:17.831817565");
        builder
                .property("datetime-simple", PropertyValue.builder().datetimeValue(nowDT))
                .property("datetime-simple-null-1", PropertyValue.builder().datetimeValue(null))
                .property("datetime-simple-null-2", PropertyValue.builder().build())
                .property("datetime-simple-null-3", (PropertyValue) null)
                .property("datetime-multiple", PropertyValue.multiple(PropertyValue.Type.DATETIME,
                        PropertyValue.builder().datetimeValue(nowDT).buildValue(),
                        PropertyValue.builder().datetimeValue(null).buildValue(),
                        (PropertyValue.Value) null
                ))
                .property("datetime-multiple-null-1", PropertyValue.builder().build())
                .property("datetime-multiple-null-2", (PropertyValue) null);

        LocalTime nowT = LocalTime.parse("09:31:17.831890062");
        builder
                .property("time-simple", PropertyValue.builder().timeValue(nowT))
                .property("time-simple-null-1", PropertyValue.builder().timeValue(null))
                .property("time-simple-null-2", PropertyValue.builder().build())
                .property("time-simple-null-3", (PropertyValue) null)
                .property("time-multiple", PropertyValue.multiple(PropertyValue.Type.TIME,
                        PropertyValue.builder().timeValue(nowT).buildValue(),
                        PropertyValue.builder().timeValue(null).buildValue(),
                        (PropertyValue.Value) null
                ))
                .property("time-multiple-null-1", PropertyValue.builder().build())
                .property("time-multiple-null-2", (PropertyValue) null);

        builder
                .property("bool-simple", PropertyValue.builder().booleanValue(true))
                .property("bool-simple-null-1", PropertyValue.builder().booleanValue(null))
                .property("bool-simple-null-2", PropertyValue.builder().build())
                .property("bool-simple-null-3", (PropertyValue) null)
                .property("bool-multiple", PropertyValue.multiple(PropertyValue.Type.BOOLEAN,
                        PropertyValue.builder().booleanValue(true).buildValue(),
                        PropertyValue.builder().booleanValue(null).buildValue(),
                        (PropertyValue.Value) null
                ))
                .property("bool-multiple-null-1", PropertyValue.builder().build())
                .property("bool-multiple-null-2", (PropertyValue) null);

        ObjectValue obj = ObjectValue.builder()
                .property("p1", p -> p.stringValue("plok"))
                .build();
        builder
                .property("obj-simple", PropertyValue.builder().objectValue(obj))
                .property("obj-simple-null-1", PropertyValue.builder().objectValue((ObjectValue) null))
                .property("obj-simple-null-2", PropertyValue.builder().build())
                .property("obj-simple-null-3", (PropertyValue) null)
                .property("obj-multiple", PropertyValue.multiple(PropertyValue.Type.OBJECT,
                        PropertyValue.builder().objectValue(obj).buildValue(),
                        PropertyValue.builder().objectValue((ObjectValue) null).buildValue(),
                        (PropertyValue.Value) null
                ))
                .property("obj-multiple-null-1", PropertyValue.builder().build())
                .property("obj-multiple-null-2", (PropertyValue) null);

        ObjectValue write = builder.build();
        String json = this.write(write);
        assertThat(json, is("{\"datetime-multiple\":[\"2024-12-17T09:31:17.831817565\",null,null],\"bool-multiple-null-2\":null,\"date-simple\":\"2024-12-17\",\"bool-multiple-null-1\":null,\"obj-simple-null-2\":null,\"time-multiple\":[\"09:31:17.831890062\",null,null],\"obj-simple-null-1\":null,\"obj-simple-null-3\":null,\"str-multiple\":[\"plok\",null,null],\"double-simple\":1.1,\"str-simple-null-2\":null,\"str-simple-null-1\":null,\"str-simple-null-3\":null,\"long-multiple-null-1\":null,\"date-multiple-null-2\":null,\"long-simple\":1,\"date-multiple-null-1\":null,\"long-multiple-null-2\":null,\"datetime-multiple-null-2\":null,\"datetime-multiple-null-1\":null,\"long-simple-null-1\":null,\"long-simple-null-3\":null,\"long-simple-null-2\":null,\"double-multiple\":[1.1,null,null],\"double-multiple-null-1\":null,\"double-multiple-null-2\":null,\"bool-simple\":true,\"str-simple\":\"plok\",\"obj-simple\":{\"p1\":\"plok\"},\"obj-multiple-null-2\":null,\"obj-multiple-null-1\":null,\"date-simple-null-1\":null,\"bool-multiple\":[true,null,null],\"double-simple-null-2\":null,\"date-simple-null-2\":null,\"time-multiple-null-2\":null,\"bool-simple-null-3\":null,\"double-simple-null-1\":null,\"date-simple-null-3\":null,\"time-multiple-null-1\":null,\"bool-simple-null-2\":null,\"bool-simple-null-1\":null,\"double-simple-null-3\":null,\"time-simple-null-1\":null,\"date-multiple\":[\"2024-12-17\",null,null],\"time-simple-null-2\":null,\"time-simple\":\"09:31:17.831890062\",\"time-simple-null-3\":null,\"obj-multiple\":[{\"p1\":\"plok\"},null,null],\"datetime-simple\":\"2024-12-17T09:31:17.831817565\",\"str-multiple-null-2\":null,\"str-multiple-null-1\":null,\"long-multiple\":[1,null,null],\"datetime-simple-null-3\":null,\"datetime-simple-null-2\":null,\"datetime-simple-null-1\":null}"));
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
