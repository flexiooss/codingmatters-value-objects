package org.codingmatters.value.objects.values.optional;

import org.codingmatters.value.objects.values.PropertyValue;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;
import static org.hamcrest.Matchers.*;

public class OptionalValueTest {

    @Test
    public void whenEmptyValue__thenIsPresent_andIsNotEmpty_ansValueIsEmpty() throws Exception {
        assertThat(new OptionalValue(PropertyValue.builder().buildValue()).isPresent(), is(true));
        assertThat(new OptionalValue(PropertyValue.builder().buildValue()).isEmpty(), is(false));
        assertThat(new OptionalValue(PropertyValue.builder().buildValue()).stringValue().isEmpty(), is(true));
        assertThat(new OptionalValue(PropertyValue.builder().buildValue()).bytesValue().isEmpty(), is(true));
        assertThat(new OptionalValue(PropertyValue.builder().buildValue()).dateValue().isEmpty(), is(true));
        assertThat(new OptionalValue(PropertyValue.builder().buildValue()).datetimeValue().isEmpty(), is(true));
        assertThat(new OptionalValue(PropertyValue.builder().buildValue()).doubleValue().isEmpty(), is(true));
        assertThat(new OptionalValue(PropertyValue.builder().buildValue()).longValue().isEmpty(), is(true));
        assertThat(new OptionalValue(PropertyValue.builder().buildValue()).booleanValue().isEmpty(), is(true));
        assertThat(new OptionalValue(PropertyValue.builder().buildValue()).objectValue().isEmpty(), is(true));
    }

    @Test
    public void whenNullValue__thenIsNotPresent_andIsEmpty() throws Exception {
        assertThat(new OptionalValue(null).isPresent(), is(false));
        assertThat(new OptionalValue(null).isEmpty(), is(true));
    }
}