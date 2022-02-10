package org.codingmatters.value.objects.values.matchers.optional;

public class CustomOptional {
    private final boolean state;

    public CustomOptional(boolean state) {
        this.state = state;
    }

    public boolean isPresent() {
        return this.state;
    }

    public boolean isEmpty() {
        return !isPresent();
    }
}
