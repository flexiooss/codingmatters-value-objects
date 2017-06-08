package org.codingmatters.value.objects.json.property.statement;

import com.squareup.javapoet.MethodSpec;
import org.codingmatters.value.objects.json.property.SimplePropertyReaderProducer;
import org.codingmatters.value.objects.spec.PropertySpec;

/**
 * Created by nelt on 6/8/17.
 */
public class SimplePropertyStatement implements PropertyStatement {
    @Override
    public void addSingleStatement(MethodSpec.Builder method, PropertySpec propertySpec, SimplePropertyReaderProducer propertyReaderProducer) {
    /*
        builder.prop(this.readValue(parser, jsonParser -> jsonParser.getText(), "prop", expectedTokens));
     */
        method.addStatement("builder.$L(this.readValue(parser, jsonParser -> jsonParser.$L(), $S, expectedTokens))",
                propertySpec.name(), propertyReaderProducer.parserMethod(), propertySpec.name()
        );
    }

    @Override
    public void addMultipleStatement(MethodSpec.Builder method, PropertySpec propertySpec, SimplePropertyReaderProducer propertyReaderProducer) {
    /*
    builder.listProp(this.readListValue(parser, jsonParser -> jsonParser.getText(), "listProp"));
     */
        method.addStatement("builder.$L(this.readListValue(parser, jsonParser -> jsonParser.$L(), $S))",
                propertySpec.name(), propertyReaderProducer.parserMethod(), propertySpec.name()
        );
    }
}
