package org.codingmatters.value.objects.demo;

import org.codingmatters.value.objects.demo.referenced.ReferencedValue;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 9/27/16.
 */
public class BuilderFromValueTest {

    @Test
    public void simpleValue() throws Exception {
        Value value = Value.builder()
                .stringProperty("toto")
                .booleanProperty(true)
                .build();

        assertThat(Value.from(value).build(), is(value));
    }

    @Test
    public void complexValue() throws Exception {
        ComplexValue value = ComplexValue.builder()
                .recursiveProperty(ComplexValue.builder().build())
                .inSpecProperty(Value.builder().stringProperty("toto").build())
                .build();

        assertThat(ComplexValue.from(value).build(), is(value));
    }

    @Test
    public void externalValue() throws Exception {
        ComplexValue value = ComplexValue.builder().outSpecProperty(ReferencedValue.builder().build()).build();

        assertThat(ComplexValue.from(value).build(), is(value));
    }

}
