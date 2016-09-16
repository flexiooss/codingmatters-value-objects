package org.generated;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.Matchers.is;

/**
 * Created by nelt on 9/16/16.n
 */
public class ValTest {

    @Test
    public void nominal() throws Exception {
        Val val = Val.Builder.builder().prop("toto").build();

        Assert.assertThat(val.prop(), is("toto"));
    }
}
