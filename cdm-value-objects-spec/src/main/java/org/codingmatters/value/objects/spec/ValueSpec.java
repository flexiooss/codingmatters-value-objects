package org.codingmatters.value.objects.spec;

import java.util.*;

/**
 * Created by nelt on 9/3/16.
 */
public class ValueSpec implements PropertyHolderSpec {

    static public Builder valueSpec() {
        return new Builder();
    }

    static public class Builder {
        private String name;
        private List<PropertySpec> propertySpecs = new LinkedList<>();
        private HashSet<String> protocols = new HashSet<>();

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder addProperty(PropertySpec spec) {
            this.propertySpecs.add(spec);
            return this;
        }

        public Builder addConformsTo(String ... protocols) {
            if(protocols != null && protocols.length != 0) {
                this.protocols.addAll(Arrays.asList(protocols));
            }
            return this;
        }

        public Builder addProperty(PropertySpec.Builder spec) {
            return this.addProperty(spec.build());
        }

        public ValueSpec build() {
            return new ValueSpec(this.name, new ArrayList<>(this.propertySpecs), new ArrayList<>(this.protocols));
        }

    }

    private final String name;
    private final List<PropertySpec> propertySpecs;
    private final List<String> protocols;

    private ValueSpec(String name, List<PropertySpec> propertySpecs, List<String> protocols) {
        this.name = name;
        this.propertySpecs = propertySpecs;
        this.protocols = protocols;
    }

    public String name() {
        return name;
    }

    @Override
    public List<PropertySpec> propertySpecs() {
        return propertySpecs;
    }

    @Override
    public PropertySpec propertySpec(String name) {
        return this.propertySpecs.stream().filter(propertySpec -> propertySpec.name().equals(name)).findFirst().orElse(null);
    }

    public List<String> protocols() {
        return protocols;
    }

    @Override
    public String toString() {
        return "ValueSpec{" +
                "name='" + name + '\'' +
                ", propertySpecs=" + propertySpecs +
                ", protocols=" + protocols +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ValueSpec valueSpec = (ValueSpec) o;

        if (name != null ? !name.equals(valueSpec.name) : valueSpec.name != null) return false;
        if (propertySpecs != null ? !propertySpecs.equals(valueSpec.propertySpecs) : valueSpec.propertySpecs != null)
            return false;
        return protocols != null ? protocols.equals(valueSpec.protocols) : valueSpec.protocols == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (propertySpecs != null ? propertySpecs.hashCode() : 0);
        result = 31 * result + (protocols != null ? protocols.hashCode() : 0);
        return result;
    }
}
