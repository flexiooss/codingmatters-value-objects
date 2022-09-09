package org.codingmatters.value.objects.values.optional;

import org.codingmatters.value.objects.values.PropertyValue;
import org.junit.Test;

import java.util.NoSuchElementException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;
import static org.hamcrest.Matchers.*;

public class OptionalPropertyValueTest {
    @Test
    public void whenNullValue__thenIsNotPresent_andIsEmpty_andGetFails() throws Exception {
        assertThat(new OptionalPropertyValue(null).isPresent(), is(false));
        assertThat(new OptionalPropertyValue(null).isEmpty(), is(true));
        assertThrows(NoSuchElementException.class, () -> new OptionalPropertyValue(null).get());
    }

    @Test
    public void whenEmptyValue__thenIsPresent_andIsNotEmpty_andGetReturnsEmpty() throws Exception {
        assertThat(new OptionalPropertyValue(PropertyValue.builder().build()).isPresent(), is(true));
        assertThat(new OptionalPropertyValue(PropertyValue.builder().build()).isEmpty(), is(false));
        assertThat(new OptionalPropertyValue(PropertyValue.builder().build()).get(), is(PropertyValue.builder().build()));
    }

    @Test
    public void whenNullValue__thenTypeIsNotPresent_andIsEmpty_andGetFails() throws Exception {
        assertThat(new OptionalPropertyValue(null).type().isPresent(), is(false));
        assertThat(new OptionalPropertyValue(null).type().isEmpty(), is(true));
        assertThrows(NoSuchElementException.class, () -> new OptionalPropertyValue(null).type().get());
    }

    @Test
    public void whenEmpty__thenTypeIsObject() throws Exception {
        assertThat(new OptionalPropertyValue(PropertyValue.builder().build()).type().isEmpty(), is(false));
        assertThat(new OptionalPropertyValue(PropertyValue.builder().build()).type().isPresent(), is(true));
        assertThat(new OptionalPropertyValue(PropertyValue.builder().build()).type().get(), is(PropertyValue.Type.OBJECT));
    }

    @Test
    public void whenTypedValue__thenTypeIsTypedValue() throws Exception {
        assertThat(new OptionalPropertyValue(PropertyValue.builder().stringValue("yop").build()).type().isEmpty(), is(false));
        assertThat(new OptionalPropertyValue(PropertyValue.builder().stringValue("yop").build()).type().isPresent(), is(true));
        assertThat(new OptionalPropertyValue(PropertyValue.builder().stringValue("yop").build()).type().get(), is(PropertyValue.Type.STRING));
    }

    @Test
    public void whenNullValue__thenCardinalityIsNotPresent_andIsEmpty_andGetFails() throws Exception {
        assertThat(new OptionalPropertyValue(null).cardinality().isPresent(), is(false));
        assertThat(new OptionalPropertyValue(null).cardinality().isEmpty(), is(true));
        assertThrows(NoSuchElementException.class, () -> new OptionalPropertyValue(null).cardinality().get());
    }

    @Test
    public void whenEmpty__thenCardinalityIsSingle() throws Exception {
        assertThat(new OptionalPropertyValue(PropertyValue.builder().build()).type().isEmpty(), is(false));
        assertThat(new OptionalPropertyValue(PropertyValue.builder().build()).type().isPresent(), is(true));
        assertThat(new OptionalPropertyValue(PropertyValue.builder().build()).type().get(), is(PropertyValue.Type.OBJECT));
    }

    @Test
    public void whenSingleValue__thenCardinalityIsSingle() throws Exception {
        assertThat(new OptionalPropertyValue(PropertyValue.builder().stringValue("yop").build()).cardinality().isEmpty(), is(false));
        assertThat(new OptionalPropertyValue(PropertyValue.builder().stringValue("yop").build()).cardinality().isPresent(), is(true));
        assertThat(new OptionalPropertyValue(PropertyValue.builder().stringValue("yop").build()).cardinality().get(), is(PropertyValue.Cardinality.SINGLE));
    }

    @Test
    public void whenSingleValue__thenSingleIsPresent_andOptionalValueIsSet() throws Exception {
        assertThat(new OptionalPropertyValue(PropertyValue.builder().stringValue("yop").build()).single().isPresent(), is(true));
        assertThat(new OptionalPropertyValue(PropertyValue.builder().stringValue("yop").build()).single().isEmpty(), is(false));
        assertThat(new OptionalPropertyValue(PropertyValue.builder().stringValue("yop").build()).single().get(),
                is(PropertyValue.builder().stringValue("yop").build().single())
        );
    }

    @Test
    public void whenSingleValue__thenMultipleIsEmpty_andOptionalValueIsNotSet() throws Exception {
        assertThat(new OptionalPropertyValue(PropertyValue.builder().stringValue("yop").build()).multiple().isPresent(), is(false));
        assertThat(new OptionalPropertyValue(PropertyValue.builder().stringValue("yop").build()).multiple().isEmpty(), is(true));
        assertThrows(NoSuchElementException.class, () -> new OptionalPropertyValue(PropertyValue.builder().stringValue("yop").build()).multiple().get());
    }

    @Test
    public void whenMultipleValue__thenCardinalityIsSingle() throws Exception {
        assertThat(new OptionalPropertyValue(PropertyValue.multiple(PropertyValue.Type.STRING, v -> v.stringValue("yop"))).cardinality().isEmpty(), is(false));
        assertThat(new OptionalPropertyValue(PropertyValue.multiple(PropertyValue.Type.STRING, v -> v.stringValue("yop"))).cardinality().isPresent(), is(true));
        assertThat(new OptionalPropertyValue(PropertyValue.multiple(PropertyValue.Type.STRING, v -> v.stringValue("yop"))).cardinality().get(), is(PropertyValue.Cardinality.MULTIPLE));
    }

    @Test
    public void whenMultipleValue__thenMultipleIsPresent_andMultipleOptionalValueIsSet() throws Exception {
        assertThat(new OptionalPropertyValue(PropertyValue.multiple(PropertyValue.Type.STRING, v -> v.stringValue("yop"))).multiple().isEmpty(), is(false));
        assertThat(new OptionalPropertyValue(PropertyValue.multiple(PropertyValue.Type.STRING, v -> v.stringValue("yop"))).multiple().isPresent(), is(true));
        assertThat(new OptionalPropertyValue(PropertyValue.multiple(PropertyValue.Type.STRING, v -> v.stringValue("yop"))).multiple().get(),
                is(PropertyValue.multiple(PropertyValue.Type.STRING, v -> v.stringValue("yop")).multiple())
        );
    }

    @Test
    public void whenMultipleValue__thenSingleIsEmpty_andOptionalValueIsNotSet() throws Exception {
        assertThat(new OptionalPropertyValue(PropertyValue.multiple(PropertyValue.Type.STRING, v -> v.stringValue("yop"))).single().isEmpty(), is(true));
        assertThat(new OptionalPropertyValue(PropertyValue.multiple(PropertyValue.Type.STRING, v -> v.stringValue("yop"))).single().isPresent(), is(false));
        assertThrows(NoSuchElementException.class, () -> new OptionalPropertyValue(PropertyValue.multiple(PropertyValue.Type.STRING, v -> v.stringValue("yop"))).single().get());
    }
}