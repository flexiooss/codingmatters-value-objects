package org.codingmatters.value.objects.demo;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 10/6/16.
 */
public class ValueToStringTest {
    @Test
    public void simple() throws Exception {
        Value value = Value.Builder.builder()
                .stringProperty("toto")
                .booleanProperty(true)
                .listProperty()
                .build();

        assertThat(value.toString(), is("Value{stringProperty=toto, booleanProperty=true, listProperty=[]}"));
    }
}
