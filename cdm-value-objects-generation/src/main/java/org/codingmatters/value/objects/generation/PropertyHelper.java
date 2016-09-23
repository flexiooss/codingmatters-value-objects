package org.codingmatters.value.objects.generation;

import com.squareup.javapoet.ClassName;
import org.codingmatters.value.objects.spec.PropertySpec;

import static org.codingmatters.value.objects.generation.SpecCodeGenerator.capitalizedFirst;
import static org.codingmatters.value.objects.spec.TypeKind.IN_SPEC_VALUE_OBJECT;

/**
 * Created by nelt on 9/22/16.
 */
public class PropertyHelper {
    static public ClassName propertyType(PropertySpec propertySpec) {
        if(IN_SPEC_VALUE_OBJECT.equals(propertySpec.typeKind())) {
            return ClassName.bestGuess(capitalizedFirst(propertySpec.type()));
        } else {
            return ClassName.bestGuess(propertySpec.type());
        }
    }

    static public ClassName builderPropertyType(PropertySpec propertySpec) {
        if(propertySpec.typeKind().isValueObject()) {
            return ClassName.bestGuess(capitalizedFirst(propertySpec.type()) + ".Builder");
        } else {
            return propertyType(propertySpec);
        }
    }

}
