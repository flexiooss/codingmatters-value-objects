package org.codingmatters.value.objects.spec;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Created by nelt on 9/3/16.
 */
public class Spec {

    static public Spec.Builder spec() {
        return new Spec.Builder();
    }

    static public class Builder {
        private final List<ValueSpec> values = new LinkedList<>();

        public Builder addValue(ValueSpec spec) {
            this.values.add(spec);
            return this;
        }

        public Builder addValue(ValueSpec.Builder spec) {
            return this.addValue(spec.build());
        }

        public Spec build() {
            return new Spec(new ArrayList<>(this.values));
        }
    }

    private final List<ValueSpec> valueSpecs;

    public Spec(List<ValueSpec> valueSpecs) {
        this.valueSpecs = valueSpecs;
    }

    public List<ValueSpec> valueSpecs() {
        return valueSpecs;
    }

    public ValueSpec valueSpec(String name) {
        return this.valueSpecs.stream().filter(valueSpec -> valueSpec.name().equals(name)).findFirst().orElse(null);
    }
    @Override
    public String toString() {
        return "Spec{" +
                "valueSpecs=" + valueSpecs +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Spec spec = (Spec) o;
        return Objects.equals(valueSpecs, spec.valueSpecs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valueSpecs);
    }
}
