package org.codingmatters.value.objects.values.vals;

import java.util.Objects;

class BaseTypeValImpl<T> implements Val.BaseTypeVal<T> {
    private final VType type;
    private final T value;

    public BaseTypeValImpl(VType type, T value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public VType type() {
        return this.type;
    }

    @Override
    public T value() {
        return this.value;
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", this.value, this.type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseTypeValImpl<?> that = (BaseTypeValImpl<?>) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
