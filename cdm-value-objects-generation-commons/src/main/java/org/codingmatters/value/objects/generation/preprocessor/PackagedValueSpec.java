package org.codingmatters.value.objects.generation.preprocessor;

import org.codingmatters.value.objects.spec.ValueSpec;

/**
 * Created by nelt on 11/23/16.
 */
public class PackagedValueSpec {
    private final String packagename;
    private final ValueSpec valueSpec;

    public PackagedValueSpec(String packagename, ValueSpec valueSpec) {
        this.packagename = packagename;
        this.valueSpec = valueSpec;
    }

    public String packagename() {
        return packagename;
    }

    public ValueSpec valueSpec() {
        return valueSpec;
    }
}
