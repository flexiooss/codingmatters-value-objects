package org.codingmatters.value.objects.values.optional;

import org.codingmatters.value.objects.values.PropertyValue;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;
import static org.hamcrest.Matchers.*;

public class OptionalPropertyValueTest {

    @Test
    public void whenEmptyValue__thenIsPresent_andIsNotEmpty() throws Exception {
        assertThat(new OptionalPropertyValue(PropertyValue.builder().build()).isPresent(), is(true));
        assertThat(new OptionalPropertyValue(PropertyValue.builder().build()).isEmpty(), is(false));
    }
    @Test
    public void whenNullValue__thenIsNotPresent_andIsEmpty() throws Exception {
        assertThat(new OptionalPropertyValue(null).isPresent(), is(false));
        assertThat(new OptionalPropertyValue(null).isEmpty(), is(true));
    }
}