package org.codingmatters.value.objects.values.optional;

import org.codingmatters.value.objects.values.ObjectValue;
import org.codingmatters.value.objects.values.PropertyValue;
import org.junit.Test;

import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

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

    @Test
    public void whenEmptyObject__thenEmpty() throws Exception {
        OptionalObjectValue opt = ObjectValue.builder().build().opt();
        assertTrue(opt.property("NOTHING_HERE").isEmpty());
        PropertyValue v = opt.property("NOTHING_HERE").orElse(PropertyValue.builder().booleanValue(false).build());

        assertThat(v.single().booleanValue(), is(false));
        assertFalse(opt.property("NOTHING_HERE").orElse(PropertyValue.builder().booleanValue(false).build()).single().booleanValue());
    }

    @Test
    public void givenObjectWithNullProperty__whenExplicitBuild__thenEmpty() throws Exception {
        HashMap value = new HashMap();
        value.put("NOTHING_HERE", null);
        OptionalObjectValue opt = ObjectValue.builder().property("NOTHING_HERE", PropertyValue.builder().build()).build().opt();
        assertTrue(opt.get().property("NOTHING_HERE").isNullValue());
        assertTrue(opt.property("NOTHING_HERE").isEmpty());
        PropertyValue v = opt.property("NOTHING_HERE").orElse(PropertyValue.builder().booleanValue(false).build());
        assertFalse(opt.property("NOTHING_HERE").orElse(PropertyValue.builder().booleanValue(false).build()).single().booleanValue());
    }

    @Test
    public void givenObjectWithNullProperty__whenBuiltFromMap__thenEmpty() throws Exception {
        HashMap value = new HashMap();
        value.put("NOTHING_HERE", null);
        OptionalObjectValue opt = ObjectValue.fromMap(value).build().opt();
        assertThat(opt.get().property("NOTHING_HERE"), is(nullValue()));
        assertTrue(opt.property("NOTHING_HERE").isEmpty());
        assertFalse(opt.property("NOTHING_HERE").orElse(PropertyValue.builder().booleanValue(false).build()).single().booleanValue());
        PropertyValue v = opt.property("NOTHING_HERE").orElse(PropertyValue.builder().booleanValue(false).build());
        assertFalse(opt.property("NOTHING_HERE").orElse(PropertyValue.builder().booleanValue(false).build()).single().booleanValue());
    }
}