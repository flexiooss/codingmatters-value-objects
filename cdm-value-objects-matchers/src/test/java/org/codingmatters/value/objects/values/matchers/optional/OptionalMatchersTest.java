package org.codingmatters.value.objects.values.matchers.optional;

import org.junit.Test;

import java.util.Optional;

import static org.codingmatters.value.objects.values.matchers.optional.Matchers.isEmpty;
import static org.codingmatters.value.objects.values.matchers.optional.Matchers.isPresent;
import static org.hamcrest.MatcherAssert.assertThat;

public class OptionalMatchersTest {
    @Test
    public void present_StandardOptional() {
        final Optional<String> opt = Optional.of("optional");
        assertThat(opt, isPresent());
    }

    @Test
    public void absent_StandardOptional() {
        final Optional<String> opt = Optional.empty();
        assertThat(opt, isEmpty());
    }

    @Test
    public void present_CustomOptional() {
        final CustomOptional opt = new CustomOptional(true);
        assertThat(opt, isPresent());
    }

    @Test
    public void absent_CustomOptional() {
        final CustomOptional opt = new CustomOptional(false);
        assertThat(opt, isEmpty());
    }

    @Test(expected = AssertionError.class)
    public void checkIsPresentFailure__WhenNotAValidOptional() {
        final Object whatever = new Object();
        assertThat(whatever, isPresent());
    }

    @Test(expected = AssertionError.class)
    public void checkIsEmptyFailure__WhenNotAValidOptional() {
        final Object whatever = new Object();
        assertThat(whatever, isEmpty());
    }
}