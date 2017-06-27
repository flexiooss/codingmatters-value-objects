package org.codingmatters.value.objects.demo;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 9/27/16.
 */
public class ValueEqualityTest {
    @Test
    public void simpleValue() throws Exception {
        Value aValue = Value.builder().stringProperty("toto").booleanProperty(true).build();
        Value theSameValue = Value.builder().stringProperty("toto").booleanProperty(true).build();
        Value anotherValue = Value.builder().stringProperty("titi").booleanProperty(false).build();

        assertThat(aValue, is(aValue));
        assertThat(aValue, is(theSameValue));
        assertThat(aValue, is(not(anotherValue)));
    }

    @Test
    public void complexValue() throws Exception {
        ComplexValue aValue = ComplexValue.builder().inSpecProperty(Value.builder().stringProperty("toto").build()).build();
        ComplexValue theSameValue = ComplexValue.builder().inSpecProperty(Value.builder().stringProperty("toto").build()).build();
        ComplexValue anotherValue = ComplexValue.builder().inSpecProperty(Value.builder().stringProperty("titi").build()).build();

        assertThat(aValue, is(aValue));
        assertThat(aValue, is(theSameValue));
        assertThat(aValue, is(not(anotherValue)));
    }
}