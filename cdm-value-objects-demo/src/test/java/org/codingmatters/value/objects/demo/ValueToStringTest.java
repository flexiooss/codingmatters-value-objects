package org.codingmatters.value.objects.demo;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 10/6/16.
 */
public class ValueToStringTest {
    @Test
    public void withNull() throws Exception {
        Value value = Value.builder()
                .stringProperty("toto")
                .booleanProperty(true)
                .listProperty()
                .setProperty()
                .build();

        assertThat(value.toString(), is("Value{stringProperty=toto, booleanProperty=true, listProperty=null, setProperty=null}"));
    }

    @Test
    public void withEmpty() throws Exception {
        Value value = Value.builder()
                .stringProperty("toto")
                .booleanProperty(true)
                .listProperty(new String [0])
                .setProperty(new String [0])
                .build();

        assertThat(value.toString(), is("Value{stringProperty=toto, booleanProperty=true, listProperty=[], setProperty=[]}"));
    }
}
