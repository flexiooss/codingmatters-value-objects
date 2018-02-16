package org.codingmatters.value.objects.values;

import org.codingmatters.value.objects.values.optional.OptionalObjectValue;

import java.util.HashMap;
import java.util.Map;

class ObjectValueImpl implements ObjectValue {

    private final Map<String, PropertyValue> properties;

    public ObjectValueImpl(Map<String, PropertyValue> properties) {
        this.properties = new HashMap<>(properties);
    }

    public String[] properties() {
        return this.properties.keySet().toArray(new String[this.properties.size()]);
    }

    @Override
    public OptionalObjectValue opt() {
        return OptionalObjectValue.of(this);
    }

    @Override
    public boolean has(String property) {
        return this.properties.containsKey(property);
    }

    @Override
    public PropertyValue property(String property) {
        return this.properties.get(property);
    }

    @Override
    public String[] propertyNames() {
        return this.properties.keySet().toArray(new String[this.properties.size()]);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ObjectValueImpl that = (ObjectValueImpl) o;

        return properties != null ? properties.equals(that.properties) : that.properties == null;
    }

    @Override
    public int hashCode() {
        return properties != null ? properties.hashCode() : 0;
    }

    @Override
    public String toString() {
        return properties.toString();
    }
}
