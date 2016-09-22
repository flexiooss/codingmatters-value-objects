package org.codingmatters.value.objects.generation;

import com.squareup.javapoet.ClassName;
import org.codingmatters.value.objects.spec.PropertySpec;
import org.codingmatters.value.objects.spec.TypeKind;

import static org.codingmatters.value.objects.generation.SpecCodeGenerator.capitalizedFirst;

/**
 * Created by nelt on 9/22/16.
 */
public class PropertyHelper {
    static public ClassName propertyType(PropertySpec propertySpec) {
        if(propertySpec.typeKind().equals(TypeKind.IN_SPEC_VALUE_OBJECT)) {
            return ClassName.bestGuess(capitalizedFirst(propertySpec.type()) );
        } else {
            return ClassName.bestGuess(propertySpec.type());
        }
    }
}
