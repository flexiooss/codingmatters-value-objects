package org.codingmatters.tests.reflect;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Locale;

import static org.codingmatters.tests.reflect.ReflectMatchers.aMethod;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 9/8/16.
 */
public class MethodMatcherTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void isAMethod() throws Exception {
        assertThat(String.class.getMethod("toUpperCase", Locale.class), is(aMethod()));
    }

    @Test
    public void aMethodWithName() throws Exception {
        assertThat(String.class.getMethod("toUpperCase", Locale.class), is(aMethod().named("toUpperCase")));
    }

    @Test
    public void aMethodWithName_failure() throws Exception {
        exception.expect(AssertionError.class);

        assertThat(String.class.getMethod("toUpperCase", Locale.class), is(aMethod().named("noNamedLikeThat")));
    }
}