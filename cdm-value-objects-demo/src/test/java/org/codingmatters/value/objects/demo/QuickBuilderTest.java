package org.codingmatters.value.objects.demo;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 9/28/16.
 */
public class QuickBuilderTest {
    @Test
    public void simple() throws Exception {
        Value value = Value.builder()
                .stringProperty("toto")
                .booleanProperty(true)
                .build();

        Value v2 = value.withStringProperty("titi");
        assertThat(v2, is(not(value)));
        assertThat(v2.stringProperty(), is("titi"));
        assertThat(v2.booleanProperty(), is(true));

        Value v3 = value.withStringProperty("titi");
        assertThat(v3, is(v2));
    }

    @Test
    public void complex() throws Exception {
        ComplexValue value = ComplexValue.builder()
                .recursiveProperty(ComplexValue.builder().build())
                .inSpecProperty(Value.builder().stringProperty("toto").build())
                .build();

        ComplexValue v2 = value.withInSpecProperty(Value.builder().stringProperty("titi").build());

        assertThat(v2, is(not(value)));
        assertThat(v2.inSpecProperty().stringProperty(), is("titi"));
    }
}
