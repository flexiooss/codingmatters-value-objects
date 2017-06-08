package org.codingmatters.value.objects.json.property;

import com.fasterxml.jackson.core.JsonToken;
import com.squareup.javapoet.MethodSpec;
import org.codingmatters.value.objects.json.property.statement.PropertyStatement;
import org.codingmatters.value.objects.spec.PropertySpec;

import java.util.Set;

/**
 * Created by nelt on 6/8/17.
 */
public class SimplePropertyReaderProducer {

    private final Set<JsonToken> expectedToken;
    private final String parserMethod;
    private final PropertyStatement propertyStatement;

    public SimplePropertyReaderProducer(
            Set<JsonToken> expectedToken,
            String parserMethod,
            PropertyStatement propertyStatement) {
        this.expectedToken = expectedToken;
        this.parserMethod = parserMethod;
        this.propertyStatement = propertyStatement;
    }

    public Set<JsonToken> expectedTokens() {
        return expectedToken;
    }

    public String parserMethod() {
        return parserMethod;
    }

    public void addSingleStatement(MethodSpec.Builder method, PropertySpec propertySpec) {
        this.propertyStatement.addSingleStatement(method, propertySpec, this);
    }

    public void addMultipleStatement(MethodSpec.Builder method, PropertySpec propertySpec) {
        this.propertyStatement.addMultipleStatement(method, propertySpec, this);
    }
}
