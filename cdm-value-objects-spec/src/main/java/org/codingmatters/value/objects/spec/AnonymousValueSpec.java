package org.codingmatters.value.objects.spec;

import java.util.*;

/**
 * Created by nelt on 11/21/16.
 */
public class AnonymousValueSpec implements PropertyHolderSpec {


    static public Builder anonymousValueSpec() {
        return new Builder();
    }

    static public class Builder {
        private List<PropertySpec> propertySpecs = new LinkedList<>();
        private HashSet<String> protocols = new HashSet<>();

        public Builder addProperty(PropertySpec spec) {
            this.propertySpecs.add(spec);
            return this;
        }

        public Builder addProperty(PropertySpec.Builder spec) {
            return this.addProperty(spec.build());
        }


        public AnonymousValueSpec.Builder addConformsTo(String ... protocols) {
            if(protocols != null && protocols.length != 0) {
                this.protocols.addAll(Arrays.asList(protocols));
            }
            return this;
        }

        public AnonymousValueSpec build() {
            return new AnonymousValueSpec(new ArrayList<>(this.propertySpecs), new LinkedList<>(this.protocols));
        }
    }

    private final List<PropertySpec> propertySpecs;
    private final List<String> protocols;

    private AnonymousValueSpec(List<PropertySpec> propertySpecs, List<String> protocols) {
        this.propertySpecs = propertySpecs;
        this.protocols = protocols;
    }

    public List<PropertySpec> propertySpecs() {
        return propertySpecs;
    }

    public List<String> protocols() {
        return protocols;
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
