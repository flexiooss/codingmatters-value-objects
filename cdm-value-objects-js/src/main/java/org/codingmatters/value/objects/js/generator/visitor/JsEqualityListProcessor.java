package org.codingmatters.value.objects.js.generator.visitor;

import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.generator.JsFileWriter;
import org.codingmatters.value.objects.js.parser.model.ParsedEnum;
import org.codingmatters.value.objects.js.parser.model.ParsedValueObject;
import org.codingmatters.value.objects.js.parser.model.ParsedYAMLSpec;
import org.codingmatters.value.objects.js.parser.model.ValueObjectProperty;
import org.codingmatters.value.objects.js.parser.model.types.ObjectTypeExternalValue;
import org.codingmatters.value.objects.js.parser.model.types.ObjectTypeInSpecValueObject;
import org.codingmatters.value.objects.js.parser.model.types.ObjectTypeNested;
import org.codingmatters.value.objects.js.parser.model.types.ValueObjectTypeExternalType;
import org.codingmatters.value.objects.js.parser.model.types.ValueObjectTypeList;
import org.codingmatters.value.objects.js.parser.model.types.ValueObjectTypePrimitiveType;
import org.codingmatters.value.objects.js.parser.model.types.YamlEnumExternalEnum;
import org.codingmatters.value.objects.js.parser.model.types.YamlEnumInSpecEnum;
import org.codingmatters.value.objects.js.parser.processing.ParsedYamlProcessor;

public class JsEqualityListProcessor implements ParsedYamlProcessor {

    private final JsFileWriter jsFileWriter;

    public JsEqualityListProcessor(JsFileWriter jsFileWriter) {
        this.jsFileWriter = jsFileWriter;
    }

    @Override
    public void process(ParsedYAMLSpec spec) throws ProcessingException {

    }

    @Override
    public void process(ParsedValueObject valueObject) throws ProcessingException {

    }

    @Override
    public void process(ValueObjectProperty property) throws ProcessingException {
        compareAsObject();
    }

    @Override
    public void process(ObjectTypeExternalValue externalValueObject) throws ProcessingException {
        compareAsObject();
    }

    private void compareAsObject() throws ProcessingException {
        try {
            jsFileWriter.line("return FlexArray.compareArraysAsObjectWithEquals(this, to)");
        } catch (Exception e) {
            throw new ProcessingException("error writing ", e);
        }
    }


    private void compareAsPrimitive() throws ProcessingException {
        try {
            jsFileWriter.line("return FlexArray.compareArraysAsPrimitives(this, to)");
        } catch (Exception e) {
            throw new ProcessingException("error writing ", e);
        }
    }

    @Override
    public void process(ObjectTypeInSpecValueObject inSpecValueObject) throws ProcessingException {
        compareAsObject();
    }

    @Override
    public void process(ObjectTypeNested nestedValueObject) throws ProcessingException {
        compareAsObject();
    }

    @Override
    public void process(ValueObjectTypeList list) throws ProcessingException {
        compareAsObject();
    }

    @Override
    public void process(ValueObjectTypePrimitiveType primitiveType) throws ProcessingException {
        compareAsPrimitive();
    }

    @Override
    public void process(YamlEnumExternalEnum externalEnum) throws ProcessingException {
        compareAsPrimitive();
    }

    @Override
    public void process(YamlEnumInSpecEnum inSpecEnum) throws ProcessingException {
        compareAsPrimitive();
    }

    @Override
    public void process(ValueObjectTypeExternalType externalType) throws ProcessingException {
        compareAsObject();
    }

    @Override
    public void process(ParsedEnum parsedEnum) throws ProcessingException {
        compareAsPrimitive();
    }
}
