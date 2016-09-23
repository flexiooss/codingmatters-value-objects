package org.codingmatters.value.objects.demo;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 9/16/16.n
 */
public class ValTest {

    @Test
    public void simpleValue() throws Exception {
        Value value = Value.Builder.builder()
                .stringProperty("toto")
                .booleanProperty(true)
                .build();

        assertThat(value.stringProperty(), is("toto"));
        assertThat(value.booleanProperty(), is(true));
    }

    @Test
    public void complexValue() throws Exception {
        ComplexValue value = ComplexValue.Builder.builder()
                .recursiveProperty(ComplexValue.Builder.builder())
                .inSpecProperty(Value.Builder.builder().stringProperty("toto"))
                .build();

        assertThat(value.inSpecProperty().stringProperty(), is("toto"));
        assertThat(value.recursiveProperty(), is(notNullValue(ComplexValue.class)));
    }
}
