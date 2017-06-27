package org.codingmatters.value.objects.demo;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 10/8/16.
 */
public class ValuleChangerTest {
    @Test
    public void simple() throws Exception {
        Value value = Value.builder()
                .stringProperty("unchanged")
                .booleanProperty(true)
                .build();

        Value changedValue = value.changed(builder -> builder.stringProperty("changed"));

        assertThat(changedValue.stringProperty(), is("changed"));
        assertThat(changedValue.booleanProperty(), is(true));
    }
}
