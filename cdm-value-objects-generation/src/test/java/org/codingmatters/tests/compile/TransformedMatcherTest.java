package org.codingmatters.tests.compile;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 9/7/16.
 */
public class TransformedMatcherTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void matches() throws Exception {
        assertThat("abcd", new TransformedMatcher<String>(o -> o.substring(2), is("cd")));
    }

    @Test
    public void doesntMatch() throws Exception {
        exception.expect(AssertionError.class);
        exception.expectMessage(
                "Expected: transformed is \"ab\"\n" +
                "     but: \"abcd\" transformed was \"cd\""
        );

        assertThat("abcd", new TransformedMatcher<String>(o -> o.substring(2), is("ab")));
    }
}
