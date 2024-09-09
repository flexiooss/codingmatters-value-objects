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
        private HashSet<String> builderProtocols = new HashSet<>();
        private HashSet<String> parametrizedBuilderProtocols = new HashSet<>();

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
        public AnonymousValueSpec.Builder addBuilderConformsTo(String ... protocols) {
            if(protocols != null && protocols.length != 0) {
                this.builderProtocols.addAll(Arrays.asList(protocols));
            }
            return this;
        }

        public AnonymousValueSpec.Builder addBuilderConformsToParametrized(String ... protocols) {
            if(protocols != null && protocols.length != 0) {
                this.parametrizedBuilderProtocols.addAll(Arrays.asList(protocols));
            }
            return this;
        }

        public AnonymousValueSpec build() {
            return new AnonymousValueSpec(new ArrayList<>(this.propertySpecs), new LinkedList<>(this.protocols), new LinkedList<>(this.builderProtocols),
                    new ArrayList<>(this.parametrizedBuilderProtocols));
        }
    }

    private final List<PropertySpec> propertySpecs;
    private final List<String> protocols;
    private final List<String> builderProtocols;
    private final List<String> parametrizedBuilderProtocols;

    private AnonymousValueSpec(List<PropertySpec> propertySpecs, List<String> protocols, List<String> builderProtocols, List<String> parametrizedBuilderProtocols) {
        this.propertySpecs = propertySpecs;
        this.protocols = protocols;
        this.builderProtocols = builderProtocols;
        this.parametrizedBuilderProtocols = parametrizedBuilderProtocols;
    }

    public List<PropertySpec> propertySpecs() {
        return propertySpecs;
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
    public PropertySpec propertySpec(String name) {
        return this.propertySpecs.stream().filter(propertySpec -> name.equals(propertySpec.name())).findFirst().orElse(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnonymousValueSpec that = (AnonymousValueSpec) o;
        return Objects.equals(propertySpecs, that.propertySpecs) && Objects.equals(protocols, that.protocols) && Objects.equals(builderProtocols, that.builderProtocols) && Objects.equals(parametrizedBuilderProtocols, that.parametrizedBuilderProtocols);
    }

    @Override
    public int hashCode() {
        return Objects.hash(propertySpecs, protocols, builderProtocols, parametrizedBuilderProtocols);
    }

    @Override
    public String toString() {
        return "AnonymousValueSpec{" +
                "propertySpecs=" + propertySpecs +
                ", protocols=" + protocols +
                ", builderProtocols=" + builderProtocols +
                ", parametrizedBuilderProtocols=" + parametrizedBuilderProtocols +
                '}';
    }
}
