package org.codingmatters.tests.compile;

import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 9/8/16.
 */
public class MethodMatcherTest {

    @Test
    public void aMethod() throws Exception {
        assertThat(String.class.getMethod("toUpperCase", Locale.class), MethodMatcher.isAMethod());
    }

    @Test
    public void aMethodWithName() throws Exception {
        assertThat(String.class.getMethod("toUpperCase", Locale.class), MethodMatcher.isAMethod().withName("toUpperCase"));
    }
}