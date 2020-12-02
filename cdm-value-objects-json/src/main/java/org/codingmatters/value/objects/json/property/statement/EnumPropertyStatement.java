package org.codingmatters.value.objects.json.property.statement;

import com.squareup.javapoet.MethodSpec;
import org.codingmatters.value.objects.generation.ValueConfiguration;
import org.codingmatters.value.objects.json.property.SimplePropertyReaderProducer;
import org.codingmatters.value.objects.spec.PropertySpec;

/**
 * Created by nelt on 6/8/17.
 */
public class EnumPropertyStatement implements PropertyStatement {
    private final ValueConfiguration types;

    public EnumPropertyStatement(ValueConfiguration types) {
        this.types = types;
    }

    @Override
    public void addSingleStatement(MethodSpec.Builder method, PropertySpec propertySpec, SimplePropertyReaderProducer propertyReaderProducer) {
        method
                .addStatement(
                        "builder.$L(this.readValue(parser, jsonParser -> { try { return $T.valueOf(jsonParser.$L()); } catch($T e) {return null;} }, $S, expectedTokens))",
                        propertySpec.name(),
                        this.types.propertyClass(propertySpec),
                        propertyReaderProducer.parserMethod(),
                        IllegalArgumentException.class,
                        propertySpec.name()
                )
        ;
    }

    @Override
    public void addMultipleStatement(MethodSpec.Builder method, PropertySpec propertySpec, SimplePropertyReaderProducer propertyReaderProducer) {
        method
                .addStatement(
                        "builder.$L(this.readListValue(parser, jsonParser -> { try { return $T.valueOf(jsonParser.$L()); } catch($T e) { return null; } }, $S))",
                        propertySpec.name(),
                        this.types.propertyClass(propertySpec),
                        propertyReaderProducer.parserMethod(),
                        IllegalArgumentException.class,
                        propertySpec.name()
                )
                ;
    }
}
