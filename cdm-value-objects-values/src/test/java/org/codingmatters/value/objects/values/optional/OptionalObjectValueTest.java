package org.codingmatters.value.objects.values.optional;

import org.codingmatters.value.objects.values.ObjectValue;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;
import static org.hamcrest.Matchers.*;

public class OptionalObjectValueTest {

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