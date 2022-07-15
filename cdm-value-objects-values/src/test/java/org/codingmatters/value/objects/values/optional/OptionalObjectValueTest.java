package org.codingmatters.value.objects.values.optional;

import org.codingmatters.value.objects.values.ObjectValue;
import org.codingmatters.value.objects.values.PropertyValue;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;
import static org.hamcrest.Matchers.*;

public class OptionalObjectValueTest {

    @Test
    public void whenEmptyValue__thenIsPresent_andIsNotEmpty() throws Exception {
        assertThat(new OptionalObjectValue(ObjectValue.builder().build()).isPresent(), is(true));
        assertThat(new OptionalObjectValue(ObjectValue.builder().build()).isEmpty(), is(false));
    }
    @Test
    public void whenNullValue__thenIsNotPresent_andIsEmpty() throws Exception {
        assertThat(new OptionalObjectValue(null).isPresent(), is(false));
        assertThat(new OptionalObjectValue(null).isEmpty(), is(true));
    }

    @Test
    public void whenUnderlyingObjectIsNull__thenPropertyAreNavigable_andValuesAreAbsent() throws Exception {
        OptionalObjectValue opt = new OptionalObjectValue(null);
        assertThat(opt.property("plok").isPresent(), is(false));
    }

    @Test
    public void givenUnderlyingObjectIsEmpty__thenSinglePropertyIsNavigable_andValuesAreAbsent() throws Exception {
        OptionalObjectValue opt = new OptionalObjectValue(ObjectValue.builder().build());
        assertThat(opt.property("plok").isPresent(), is(false));
    }

    @Test
    public void givenUnderlyingObjectIsEmpty__thenMultiplePropertyIsNavigable_andValuesAreAbsent() throws Exception {
        OptionalObjectValue opt = new OptionalObjectValue(ObjectValue.builder().build());
        assertThat(opt.property("plik").multiple().get(2).objectValue().property("plok").isPresent(), is(false));
    }

    @Test
    public void givenUnderlyingObjectIsEmpty__thenSingleEmbeddedPropertyIsNavigable_andValuesAreAbsent() throws Exception {
        OptionalObjectValue opt = new OptionalObjectValue(ObjectValue.builder().build());
        assertThat(opt.property("plik").single().objectValue().property("plok").isPresent(), is(false));
    }

    @Test
    public void givenUnderlyingObjectIsEmpty__thenEmbeddedMultiplePropertyIsNavigable_andValuesAreAbsent() throws Exception {
        OptionalObjectValue opt = new OptionalObjectValue(ObjectValue.builder().build());
        assertThat(opt.property("plik").single().objectValue().property("plok").multiple().get(3).dateValue().isPresent(), is(false));
    }
}