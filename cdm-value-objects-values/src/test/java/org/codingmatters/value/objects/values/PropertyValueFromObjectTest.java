package org.codingmatters.value.objects.values;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class PropertyValueFromObjectTest {

    @Test
    public void testString() throws PropertyValue.Type.UnsupportedTypeException {
        PropertyValue actualPropertyValue = PropertyValue.fromObject((Object) "value");
        PropertyValue expectedPropertyValue = PropertyValue.builder().stringValue("value").build();

        MatcherAssert.assertThat(actualPropertyValue, Matchers.is(expectedPropertyValue));
    }

    @Test
    public void testInteger() throws PropertyValue.Type.UnsupportedTypeException {
        PropertyValue actualPropertyValue = PropertyValue.fromObject((Object) 42);
        PropertyValue expectedPropertyValue = PropertyValue.builder().longValue(42L).build();

        MatcherAssert.assertThat(actualPropertyValue, Matchers.is(expectedPropertyValue));
    }

    @Test
    public void testLong() throws PropertyValue.Type.UnsupportedTypeException {
        PropertyValue actualPropertyValue = PropertyValue.fromObject((Object) 42L);
        PropertyValue expectedPropertyValue = PropertyValue.builder().longValue(42L).build();

        MatcherAssert.assertThat(actualPropertyValue, Matchers.is(expectedPropertyValue));
    }

    @Test
    public void testBoolean() throws PropertyValue.Type.UnsupportedTypeException {
        PropertyValue actualPropertyValue = PropertyValue.fromObject((Object) true);
        PropertyValue expectedPropertyValue = PropertyValue.builder().booleanValue(true).build();

        MatcherAssert.assertThat(actualPropertyValue, Matchers.is(expectedPropertyValue));
    }

    @Test
    public void testDouble() throws PropertyValue.Type.UnsupportedTypeException {
        PropertyValue actualPropertyValue = PropertyValue.fromObject((Object) 1.123456789);
        PropertyValue expectedPropertyValue = PropertyValue.builder().doubleValue(1.123456789).build();

        MatcherAssert.assertThat(actualPropertyValue, Matchers.is(expectedPropertyValue));
    }

    @Test
    public void testFloat() throws PropertyValue.Type.UnsupportedTypeException {
        PropertyValue actualPropertyValue = PropertyValue.fromObject((Object) 1.75f);
        PropertyValue expectedPropertyValue = PropertyValue.builder().doubleValue(1.75).build();

        MatcherAssert.assertThat(actualPropertyValue, Matchers.is(expectedPropertyValue));
    }

    @Test
    public void testLocalDateTime() throws PropertyValue.Type.UnsupportedTypeException {
        LocalDateTime now = LocalDateTime.now();
        PropertyValue actualPropertyValue = PropertyValue.fromObject((Object) now);
        PropertyValue expectedPropertyValue = PropertyValue.builder().datetimeValue(now).build();

        MatcherAssert.assertThat(actualPropertyValue, Matchers.is(expectedPropertyValue));
    }

    @Test
    public void testLocalDate() throws PropertyValue.Type.UnsupportedTypeException {
        LocalDate today = LocalDate.now();
        PropertyValue actualPropertyValue = PropertyValue.fromObject((Object) today);
        PropertyValue expectedPropertyValue = PropertyValue.builder().dateValue(today).build();

        MatcherAssert.assertThat(actualPropertyValue, Matchers.is(expectedPropertyValue));
    }

    @Test
    public void testLocalTime() throws PropertyValue.Type.UnsupportedTypeException {
        LocalTime now = LocalTime.now();
        PropertyValue actualPropertyValue = PropertyValue.fromObject((Object) now);
        PropertyValue expectedPropertyValue = PropertyValue.builder().timeValue(now).build();

        MatcherAssert.assertThat(actualPropertyValue, Matchers.is(expectedPropertyValue));
    }

    @Test
    public void testObjectValue() throws PropertyValue.Type.UnsupportedTypeException {
        ObjectValue objectValue = ObjectValue.builder().property("a", v -> v.stringValue("b")).build();
        PropertyValue actualPropertyValue = PropertyValue.fromObject((Object) objectValue);
        PropertyValue expectedPropertyValue = PropertyValue.builder().objectValue(objectValue).build();

        MatcherAssert.assertThat(actualPropertyValue, Matchers.is(expectedPropertyValue));
    }

    @Test
    public void testBytes() throws PropertyValue.Type.UnsupportedTypeException {
        byte[] bytes = "value".getBytes();
        PropertyValue actualPropertyValue = PropertyValue.fromObject((Object) bytes);
        PropertyValue expectedPropertyValue = PropertyValue.builder().bytesValue(bytes).build();

        MatcherAssert.assertThat(actualPropertyValue, Matchers.is(expectedPropertyValue));
    }
}
