package org.codingmatters.value.objects.values.vals;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

class ObjectValImpl implements Val.ObjectVal {

    private final HashMap<String, Val> props;

    ObjectValImpl(Map<String, Val> props) {
        this.props = new HashMap<>(props);
    }

    @Override
    public Val property(String name) {
        return props.get(name);
    }

    @Override
    public VType type() {
        return VType.OBJECT;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("{");
        this.props.entrySet().forEach(entry -> result.append(String.format("%s=%s", entry.getKey(), entry.getValue())));
        return result.append("}").toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjectValImpl objectVal = (ObjectValImpl) o;
        return Objects.equals(props, objectVal.props);
    }

    @Override
    public int hashCode() {
        return Objects.hash(props);
    }
}
