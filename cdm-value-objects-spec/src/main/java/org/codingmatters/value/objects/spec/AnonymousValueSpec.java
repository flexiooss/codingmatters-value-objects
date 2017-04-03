package org.codingmatters.value.objects.spec;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Created by nelt on 11/21/16.
 */
public class AnonymousValueSpec implements PropertyHolderSpec {

    static public Builder anonymousValueSpec() {
        return new Builder();
    }

    static public class Builder {
        private List<PropertySpec> propertySpecs = new LinkedList<>();

        public Builder addProperty(PropertySpec spec) {
            this.propertySpecs.add(spec);
            return this;
        }

        public Builder addProperty(PropertySpec.Builder spec) {
            return this.addProperty(spec.build());
        }

        public AnonymousValueSpec build() {
            return new AnonymousValueSpec(new ArrayList<>(this.propertySpecs));
        }
    }

    private final List<PropertySpec> propertySpecs;

    private AnonymousValueSpec(List<PropertySpec> propertySpecs) {
        this.propertySpecs = propertySpecs;
    }

    public List<PropertySpec> propertySpecs() {
        return propertySpecs;
    }

    @Override
    public PropertySpec propertySpec(String name) {
        return this.propertySpecs.stream().filter(propertySpec -> propertySpec.name().equals(name)).findFirst().orElse(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnonymousValueSpec that = (AnonymousValueSpec) o;
        return Objects.equals(propertySpecs, that.propertySpecs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(propertySpecs);
    }

    @Override
    public String toString() {
        return "AnonymousValueSpec{" +
                "propertySpecs=" + propertySpecs +
                '}';
    }
}
