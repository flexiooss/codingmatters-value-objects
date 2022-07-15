package org.codingmatters.value.objects.values.optional;

import org.codingmatters.value.objects.values.ObjectValue;
import org.codingmatters.value.objects.values.PropertyValue;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;
import static org.hamcrest.Matchers.*;

public class OptionalMultipleValueTest {

    @Test
    public void whenEmptyValue__thenIsPresent_andIsNotEmpty() throws Exception {
        assertThat(new OptionalMultipleValue(new PropertyValue.Value[0]).isPresent(), is(true));
        assertThat(new OptionalMultipleValue(new PropertyValue.Value[0]).isEmpty(), is(false));
    }
    @Test
    public void whenNullValue__thenIsNotPresent_andIsEmpty() throws Exception {
        assertThat(new OptionalMultipleValue(null).isPresent(), is(false));
        assertThat(new OptionalMultipleValue(null).isEmpty(), is(true));
    }
}