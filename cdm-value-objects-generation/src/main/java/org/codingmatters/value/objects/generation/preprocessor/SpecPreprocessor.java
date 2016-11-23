package org.codingmatters.value.objects.generation.preprocessor;

import org.codingmatters.value.objects.spec.Spec;
import org.codingmatters.value.objects.spec.ValueSpec;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by nelt on 11/23/16.
 */
public class SpecPreprocessor {
    private final Spec spec;
    private final String packageName;

    public SpecPreprocessor(Spec spec, String packageName) {
        this.spec = spec;
        this.packageName = packageName;
    }

    public List<PackagedValueSpec> packagedValueSpec() {
        List<PackagedValueSpec> result = new LinkedList<>();
        for (ValueSpec valueSpec : this.spec.valueSpecs()) {
            result.addAll(this.preprocess(valueSpec));
        }
        return result;
    }

    private List<PackagedValueSpec> preprocess(ValueSpec valueSpec) {
        return new ValueSpecPreprocessor(this.packageName, valueSpec).preprocess();
    }

}
