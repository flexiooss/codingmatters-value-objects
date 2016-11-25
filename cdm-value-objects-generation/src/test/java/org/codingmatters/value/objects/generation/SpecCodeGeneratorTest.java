package org.codingmatters.value.objects.generation;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 11/25/16.
 */
public class SpecCodeGeneratorTest {

    @Test
    public void replaceAll() throws Exception {
        assertThat("blabla".replaceAll("\\.", "/"), is("blabla"));
        assertThat(".bla.bla".replaceAll("\\.", "/"), is("/bla/bla"));
    }
}