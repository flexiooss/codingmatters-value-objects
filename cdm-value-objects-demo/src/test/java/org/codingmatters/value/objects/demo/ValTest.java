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
        Val val = Val.Builder.builder().prop("toto").build();

        assertThat(val.prop(), is("toto"));
    }
}
