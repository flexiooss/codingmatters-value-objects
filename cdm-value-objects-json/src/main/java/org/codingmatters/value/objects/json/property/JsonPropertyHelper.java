package org.codingmatters.value.objects.json.property;

import org.codingmatters.value.objects.spec.PropertySpec;

public class JsonPropertyHelper {

    public static boolean isTransient(PropertySpec propertySpec) {
        for (String hint : propertySpec.hints()) {
            if(hint.equals("json:transient")) return true;
        }
        return false;
    }
}
