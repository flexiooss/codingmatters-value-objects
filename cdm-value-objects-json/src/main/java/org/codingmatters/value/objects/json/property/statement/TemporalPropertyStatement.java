package org.codingmatters.value.objects.json.property.statement;

import com.squareup.javapoet.MethodSpec;
import org.codingmatters.value.objects.json.property.SimplePropertyReaderProducer;
import org.codingmatters.value.objects.spec.PropertySpec;

/**
 * Created by nelt on 6/8/17.
 */
public class TemporalPropertyStatement implements PropertyStatement {
    private final Class temporalClass;

    public TemporalPropertyStatement(Class temporalClass) {
        this.temporalClass = temporalClass;
    }

    @Override
    public void addSingleStatement(MethodSpec.Builder method, PropertySpec propertySpec, SimplePropertyReaderProducer propertyReaderProducer) {
        method.addStatement("builder.$L(this.readValue(parser, jsonParser -> $T.parse(jsonParser.$L()), $S, expectedTokens))",
                propertySpec.name(), this.temporalClass, propertyReaderProducer.parserMethod(), propertySpec.name()
        );
    }

    @Override
    public void addMultipleStatement(MethodSpec.Builder method, PropertySpec propertySpec, SimplePropertyReaderProducer propertyReaderProducer) {
        method.addStatement("builder.$L(this.readListValue(parser, jsonParser -> $T.parse(jsonParser.$L()), $S))",
                propertySpec.name(), this.temporalClass, propertyReaderProducer.parserMethod(), propertySpec.name()
        );
    }
}
