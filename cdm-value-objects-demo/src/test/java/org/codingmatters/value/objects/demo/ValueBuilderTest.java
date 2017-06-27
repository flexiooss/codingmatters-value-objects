package org.codingmatters.value.objects.demo;

import org.codingmatters.value.objects.demo.referenced.ReferencedValue;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 9/16/16.n
 */
public class ValueBuilderTest {

    @Test
    public void simpleValue() throws Exception {
        Value value = Value.builder()
                .stringProperty("toto")
                .booleanProperty(true)
                .listProperty("a", "b", "c")
                .setProperty("a", "b", "a")
                .build();

        assertThat(value.stringProperty(), is("toto"));
        assertThat(value.booleanProperty(), is(true));
        assertThat(value.listProperty().toArray(), is(new String [] {"a", "b", "c"}));
        assertThat(value.setProperty().toArray(), is(new String [] {"a", "b"}));
    }

    @Test
    public void complexValue() throws Exception {
        ComplexValue value = ComplexValue.builder()
                .recursiveProperty(ComplexValue.builder().build())
                .inSpecProperty(Value.builder().stringProperty("toto").build())
                .build();

        assertThat(value.inSpecProperty().stringProperty(), is("toto"));
        assertThat(value.recursiveProperty(), is(notNullValue(ComplexValue.class)));
    }

    @Test
    public void externalValue() throws Exception {
        ComplexValue value = ComplexValue.builder().outSpecProperty(ReferencedValue.builder().build()).build();
        assertThat(value.outSpecProperty(), is(notNullValue(ReferencedValue.class)));
    }

}
