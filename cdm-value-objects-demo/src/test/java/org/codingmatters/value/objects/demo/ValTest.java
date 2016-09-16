package org.codingmatters.value.objects.demo;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 9/16/16.n
 */
public class ValTest {

    @Test
    public void nominal() throws Exception {
        Value value = Value.Builder.builder()
                .stringProperty("toto")
                .booleanProperty(true)
                .build();

        assertThat(value.stringProperty(), is("toto"));
        assertThat(value.booleanProperty(), is(true));
    }
}
