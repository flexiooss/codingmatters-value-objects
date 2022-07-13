package org.codingmatters.value.objects.demo;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ConformsTest {
    @Test
    public void value() throws Exception {
        assertThat(
                ConformingValue.builder().stringProperty("value").build().decoratedValue("before ", " after"),
                is("before value after")
        );
    }

    @Test
    public void builder() throws Exception {
        assertThat(
                ConformingValue.builder().initialzeStringPropWithPlok().build().stringProperty(),
                is("plok")
        );
    }
}
