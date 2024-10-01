package org.codingmatters.value.objects.js.generator.visitor;

import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.generator.JsFileWriter;
import org.codingmatters.value.objects.js.generator.NamingUtility;
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

import java.io.IOException;

public class JsEqualityProcessor implements ParsedYamlProcessor {

    private final JsFileWriter writer;
    private String propertyName;

    public JsEqualityProcessor(JsFileWriter writer) {
        this.writer = writer;
    }

    @Override
    public void process(ParsedYAMLSpec spec) throws ProcessingException {

    }

    @Override
    public void process(ParsedValueObject valueObject) throws ProcessingException {

    }

    @Override
    public void process(ValueObjectProperty property) throws ProcessingException {
        this.propertyName = NamingUtility.propertyName(property.name());
        property.type().process(this);
    }

    @Override
    public void process(ObjectTypeExternalValue externalValueObject) throws ProcessingException {
        checkNullityAndEquals();
    }

    @Override
    public void process(ObjectTypeInSpecValueObject inSpecValueObject) throws ProcessingException {
        checkNullityAndEquals();
    }

    @Override
    public void process(ObjectTypeNested nestedValueObject) throws ProcessingException {
        checkNullityAndEquals();
    }

    @Override
    public void process(ValueObjectTypeList list) throws ProcessingException {
        checkNullityAndEquals();
    }

    private void checkNullityAndEquals() throws ProcessingException {
        try {
            writer.line("if (!isNull(this." + propertyName + "())) {");
            writer.line("if (!this." + propertyName + "().equals(to." + propertyName + "())) return false;");
            writer.unindent();
            writer.line("} else if (!isNull(to." + propertyName + "())) return false;");
        } catch (IOException e) {
            throw new ProcessingException("Error writing", e);
        }
    }

    @Override
    public void process(ValueObjectTypePrimitiveType primitiveType) throws ProcessingException {
        if (primitiveType.type() == ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.DATE) {
            checkNullityAndEquals();
        } else if (primitiveType.type() == ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.DATE_TIME) {
            checkNullityAndEquals();
        } else if (primitiveType.type() == ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.TIME) {
            checkNullityAndEquals();
        } else if (primitiveType.type() == ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.OBJECT) {
            checkNullityAndEquals();
        } else {
            checkDoubleEqual();
        }
    }

    private void checkDoubleEqual() throws ProcessingException {
        try {
            writer.line("if (this." + propertyName + "() !== to." + propertyName + "()) return false;");
        } catch (IOException e) {
            throw new ProcessingException("Error writing", e);
        }
    }

    @Override
    public void process(YamlEnumExternalEnum externalEnum) throws ProcessingException {
        checkDoubleEqual();
    }

    @Override
    public void process(YamlEnumInSpecEnum inSpecEnum) throws ProcessingException {
        checkDoubleEqual();
    }

    @Override
    public void process(ValueObjectTypeExternalType externalType) throws ProcessingException {
        checkNullityAndEquals();
    }

    @Override
    public void process(ParsedEnum parsedEnum) throws ProcessingException {
        checkDoubleEqual();
    }
}
