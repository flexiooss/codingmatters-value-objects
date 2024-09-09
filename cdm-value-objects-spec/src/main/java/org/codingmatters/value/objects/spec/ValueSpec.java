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
        private HashSet<String> builderProtocols = new HashSet<>();
        private HashSet<String> parametrizedBuilderProtocols = new HashSet<>();

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

        public Builder addBuilderConformsTo(String ... protocols) {
            if(protocols != null && protocols.length != 0) {
                this.builderProtocols.addAll(Arrays.asList(protocols));
            }
            return this;
        }

        public Builder addBuilderConformsToParametrized(String ... protocols) {
            if(protocols != null && protocols.length != 0) {
                this.parametrizedBuilderProtocols.addAll(Arrays.asList(protocols));
            }
            return this;
        }

        public Builder addProperty(PropertySpec.Builder spec) {
            return this.addProperty(spec.build());
        }

        public ValueSpec build() {
            return new ValueSpec(
                    this.name,
                    new ArrayList<>(this.propertySpecs),
                    new ArrayList<>(this.protocols),
                    new ArrayList<>(this.builderProtocols),
                    new ArrayList<>(this.parametrizedBuilderProtocols)
            );
        }
    }

    private final String name;
    private final List<PropertySpec> propertySpecs;
    private final List<String> protocols;
    private final List<String> builderProtocols;
    private final List<String> parametrizedBuilderProtocols;

    private ValueSpec(String name, List<PropertySpec> propertySpecs, List<String> protocols, List<String> builderProtocols, List<String> parametrizedBuilderProtocols) {
        this.name = name;
        this.propertySpecs = propertySpecs;
        this.protocols = protocols;
        this.builderProtocols = builderProtocols;
        this.parametrizedBuilderProtocols = parametrizedBuilderProtocols;
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
    public List<String> builderProtocols() {
        return builderProtocols;
    }
    public List<String> parametrizedBuilderProtocols() {
        return parametrizedBuilderProtocols;
    }

    @Override
    public String toString() {
        return "ValueSpec{" +
                "name='" + name + '\'' +
                ", propertySpecs=" + propertySpecs +
                ", protocols=" + protocols +
                ", builderProtocols=" + builderProtocols +
                ", parametrizedBuilderProtocols=" + parametrizedBuilderProtocols +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValueSpec valueSpec = (ValueSpec) o;
        return Objects.equals(name, valueSpec.name) && Objects.equals(propertySpecs, valueSpec.propertySpecs) && Objects.equals(protocols, valueSpec.protocols) && Objects.equals(builderProtocols, valueSpec.builderProtocols) && Objects.equals(parametrizedBuilderProtocols, valueSpec.parametrizedBuilderProtocols);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, propertySpecs, protocols, builderProtocols, parametrizedBuilderProtocols);
    }
}
