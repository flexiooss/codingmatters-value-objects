package org.codingmatters.value.objects.spec;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Created by nelt on 9/3/16.
 */
public class ValueSpec {

    static public Builder valueSpec() {
        return new Builder();
    }

    static public class Builder {
        private String name;
        private List<PropertySpec> propertySpecs = new LinkedList<>();

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder addProperty(PropertySpec spec) {
            this.propertySpecs.add(spec);
            return this;
        }

        public Builder addProperty(PropertySpec.Builder spec) {
            return this.addProperty(spec.build());
        }

        public ValueSpec build() {
            return new ValueSpec(this.name, new ArrayList<>(this.propertySpecs));
        }
    }

    private final String name;
    private final List<PropertySpec> propertySpecs;

    private ValueSpec(String name, List<PropertySpec> propertySpecs) {
        this.name = name;
        this.propertySpecs = propertySpecs;
    }

    public String name() {
        return name;
    }

    public List<PropertySpec> propertySpecs() {
        return propertySpecs;
    }

    @Override
    public String toString() {
        return "ValueSpec{" +
                "name='" + name + '\'' +
                ", propertySpecs=" + propertySpecs +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValueSpec valueSpec = (ValueSpec) o;
        return Objects.equals(name, valueSpec.name) &&
                Objects.equals(propertySpecs, valueSpec.propertySpecs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, propertySpecs);
    }
}
