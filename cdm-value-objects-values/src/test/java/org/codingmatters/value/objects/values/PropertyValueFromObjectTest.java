package org.codingmatters.value.objects.values;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class PropertyValueFromObjectTest {

    @Test
    public void testString() throws PropertyValue.Type.UnsupportedTypeException {
        PropertyValue actualPropertyValue = PropertyValue.fromObject((Object) "value");
        PropertyValue expectedPropertyValue = PropertyValue.builder().stringValue("value").build();

        assertThat(actualPropertyValue, is(expectedPropertyValue));
    }

    @Test
    public void testInteger() throws PropertyValue.Type.UnsupportedTypeException {
        PropertyValue actualPropertyValue = PropertyValue.fromObject((Object) 42);
        PropertyValue expectedPropertyValue = PropertyValue.builder().longValue(42L).build();

        assertThat(actualPropertyValue, is(expectedPropertyValue));
    }

    @Test
    public void testLong() throws PropertyValue.Type.UnsupportedTypeException {
        PropertyValue actualPropertyValue = PropertyValue.fromObject((Object) 42L);
        PropertyValue expectedPropertyValue = PropertyValue.builder().longValue(42L).build();

        assertThat(actualPropertyValue, is(expectedPropertyValue));
    }

    @Test
    public void testBoolean() throws PropertyValue.Type.UnsupportedTypeException {
        PropertyValue actualPropertyValue = PropertyValue.fromObject((Object) true);
        PropertyValue expectedPropertyValue = PropertyValue.builder().booleanValue(true).build();

        assertThat(actualPropertyValue, is(expectedPropertyValue));
    }

    @Test
    public void testDouble() throws PropertyValue.Type.UnsupportedTypeException {
        PropertyValue actualPropertyValue = PropertyValue.fromObject((Object) 1.123456789);
        PropertyValue expectedPropertyValue = PropertyValue.builder().doubleValue(1.123456789).build();

        assertThat(actualPropertyValue, is(expectedPropertyValue));
    }

    @Test
    public void testFloat() throws PropertyValue.Type.UnsupportedTypeException {
        PropertyValue actualPropertyValue = PropertyValue.fromObject((Object) 1.75f);
        PropertyValue expectedPropertyValue = PropertyValue.builder().doubleValue(1.75).build();

        assertThat(actualPropertyValue, is(expectedPropertyValue));
    }

    @Test
    public void testLocalDateTime() throws PropertyValue.Type.UnsupportedTypeException {
        LocalDateTime now = LocalDateTime.now();
        PropertyValue actualPropertyValue = PropertyValue.fromObject((Object) now);
        PropertyValue expectedPropertyValue = PropertyValue.builder().datetimeValue(now).build();

        assertThat(actualPropertyValue, is(expectedPropertyValue));
    }

    @Test
    public void testLocalDate() throws PropertyValue.Type.UnsupportedTypeException {
        LocalDate today = LocalDate.now();
        PropertyValue actualPropertyValue = PropertyValue.fromObject((Object) today);
        PropertyValue expectedPropertyValue = PropertyValue.builder().dateValue(today).build();

        assertThat(actualPropertyValue, is(expectedPropertyValue));
    }

    @Test
    public void testLocalTime() throws PropertyValue.Type.UnsupportedTypeException {
        LocalTime now = LocalTime.now();
        PropertyValue actualPropertyValue = PropertyValue.fromObject((Object) now);
        PropertyValue expectedPropertyValue = PropertyValue.builder().timeValue(now).build();

        assertThat(actualPropertyValue, is(expectedPropertyValue));
    }

    @Test
    public void testObjectValue() throws PropertyValue.Type.UnsupportedTypeException {
        ObjectValue objectValue = ObjectValue.builder().property("a", v -> v.stringValue("b")).build();
        PropertyValue actualPropertyValue = PropertyValue.fromObject((Object) objectValue);
        PropertyValue expectedPropertyValue = PropertyValue.builder().objectValue(objectValue).build();

        assertThat(actualPropertyValue, is(expectedPropertyValue));
    }

    @Test
    public void testBytes() throws PropertyValue.Type.UnsupportedTypeException {
        byte[] bytes = "value".getBytes();
        PropertyValue actualPropertyValue = PropertyValue.fromObject((Object) bytes);
        PropertyValue expectedPropertyValue = PropertyValue.builder().bytesValue(bytes).build();

        assertThat(actualPropertyValue, is(expectedPropertyValue));
    }

    @Test
    public void stringList() throws Exception {
        List<String> value = Arrays.asList("one", "two");
        PropertyValue actualPropertyValue = PropertyValue.fromObject(value);
        PropertyValue expectedPropertyValue = PropertyValue.multiple(
                PropertyValue.Type.STRING,
                PropertyValue.builder().stringValue("one"), PropertyValue.builder().stringValue("two")
        );

        assertThat(actualPropertyValue, is(expectedPropertyValue));
    }

    @Test
    public void stringArray() throws Exception {
        String[] value = {"one", "two"};
        PropertyValue actualPropertyValue = PropertyValue.fromObject(value);
        PropertyValue expectedPropertyValue = PropertyValue.multiple(
                PropertyValue.Type.STRING,
                PropertyValue.builder().stringValue("one"), PropertyValue.builder().stringValue("two")
        );

        assertThat(actualPropertyValue, is(expectedPropertyValue));
    }

    @Test
    public void map() throws Exception {
        Map value = new HashMap();
        value.put("p1", "v1");
        value.put("p2", "v2");

        PropertyValue actualPropertyValue = PropertyValue.fromObject(value);
        PropertyValue expectedPropertyValue = PropertyValue.builder()
                .objectValue(ObjectValue.builder()
                        .property("p1", PropertyValue.builder().stringValue("v1").build())
                        .property("p2", PropertyValue.builder().stringValue("v2").build())
                        .build())
                .build();
        assertThat(actualPropertyValue, is(expectedPropertyValue));
    }

    @Test
    public void mapList() throws Exception {
        Map value = new HashMap();
        value.put("p1", "v1");
        value.put("p2", "v2");

        List list = new LinkedList();
        list.add(value);

        PropertyValue actualPropertyValue = PropertyValue.fromObject(list);
        PropertyValue expectedPropertyValue = PropertyValue.multiple(
                PropertyValue.Type.OBJECT,
                PropertyValue.builder().objectValue(ObjectValue.builder()
                        .property("p1", PropertyValue.builder().stringValue("v1").build())
                        .property("p2", PropertyValue.builder().stringValue("v2").build())
                        .build()
                )
        );
        assertThat(actualPropertyValue, is(expectedPropertyValue));
    }

    @Test
    public void mapArray() throws Exception {
        Map value = new HashMap();
        value.put("p1", "v1");
        value.put("p2", "v2");

        PropertyValue actualPropertyValue = PropertyValue.fromObject(new Object[] {value});
        PropertyValue expectedPropertyValue = PropertyValue.multiple(
                PropertyValue.Type.OBJECT,
                PropertyValue.builder().objectValue(ObjectValue.builder()
                        .property("p1", PropertyValue.builder().stringValue("v1").build())
                        .property("p2", PropertyValue.builder().stringValue("v2").build())
                        .build()
                )
        );
        assertThat(actualPropertyValue, is(expectedPropertyValue));
    }
}
