package org.codingmatters.value.objects.json.property.statement;

import com.squareup.javapoet.MethodSpec;
import org.codingmatters.value.objects.json.property.SimplePropertyReaderProducer;
import org.codingmatters.value.objects.spec.PropertySpec;

/**
 * Created by nelt on 6/8/17.
 */
public interface PropertyStatement {
    void addSingleStatement(MethodSpec.Builder method, PropertySpec propertySpec, SimplePropertyReaderProducer propertyReaderProducer);

    void addMultipleStatement(MethodSpec.Builder method, PropertySpec propertySpec, SimplePropertyReaderProducer propertyReaderProducer);
}
