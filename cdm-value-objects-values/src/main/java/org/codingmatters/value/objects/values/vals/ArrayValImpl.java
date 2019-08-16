package org.codingmatters.value.objects.values.vals;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class ArrayValImpl implements Val.ArrayVal {

    private final ArrayList<Val> vals;

    protected ArrayValImpl(List<Val> vals) {
        this.vals = new ArrayList<>(vals);
    }

    @Override
    public Val[] values() {
        return this.vals.toArray(new Val[0]);
    }

    @Override
    public VType type() {
        return VType.ARRAY;
    }

    @Override
    public <T> T accept(ValVisitor<T> visitor) {
        return visitor.visitArray(this);
    }

    @Override
    public String toString() {
        return this.vals.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArrayValImpl arrayVal = (ArrayValImpl) o;
        return Objects.deepEquals(vals, arrayVal.vals);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vals);
    }
}
