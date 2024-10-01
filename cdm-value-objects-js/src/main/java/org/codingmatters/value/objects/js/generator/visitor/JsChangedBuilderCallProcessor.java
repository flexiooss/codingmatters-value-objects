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

public class JsChangedBuilderCallProcessor implements ParsedYamlProcessor {

    private final JsFileWriter jsClassGenerator;
    private String propertyName;
    private final String objectName;
    private final String builderName;

    public JsChangedBuilderCallProcessor(JsFileWriter jsClassGenerator, String objectName, String builderName) {
        this.jsClassGenerator = jsClassGenerator;
        this.objectName = objectName;
        this.builderName = builderName;
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
        String classFullName = NamingUtility.classFullName(externalValueObject.objectReference());
        String builderCall = "builder." + propertyName + "(" + propertyName + ".call(null, isNull(this." + propertyName + "()) ? " + classFullName + ".builder() : " + classFullName + ".from(this.mySubProp())))";
        baseMethod(builderCall);
    }

    @Override
    public void process(ObjectTypeInSpecValueObject inSpecValueObject) throws ProcessingException {
        String classFullName = NamingUtility.classFullName(inSpecValueObject.packageName() + "." + NamingUtility.className(inSpecValueObject.inSpecValueObjectName()));
        String builderCall = "builder." + propertyName + "(" + propertyName + ".call(null, isNull(this." + propertyName + "()) ? " + classFullName + ".builder() : " + classFullName + ".from(this.mySubProp())))";
        baseMethod(builderCall);
    }

    @Override
    public void process(ObjectTypeNested nestedValueObject) throws ProcessingException {
        String classFullName = NamingUtility.classFullName(nestedValueObject.nestValueObject().packageName() + "." + NamingUtility.className(nestedValueObject.nestValueObject().name()));
        String builderCall = "builder." + propertyName + "(" + propertyName + ".call(null, isNull(this." + propertyName + "()) ? " + classFullName + ".builder() : " + classFullName + ".from(this.mySubProp())))";
        baseMethod(builderCall);
    }

    @Override
    public void process(ValueObjectTypeList list) throws ProcessingException {
        String classFullName = NamingUtility.classFullName(list.packageName() + "." + NamingUtility.className(list.name()));
        String builderCall = "builder." + propertyName + "(" + propertyName + ".call(null, isNull(this." + propertyName + "()) ? " + classFullName + ".builder() : " + classFullName + ".from(this.mySubProp())))";
        baseMethod(builderCall);
    }

    @Override
    public void process(ValueObjectTypePrimitiveType primitiveType) throws ProcessingException {
        if (primitiveType.type() == ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.OBJECT) {
            String builderCall = "builder." + propertyName + "(" + propertyName + ".call(null, isNull(this." + propertyName + "()) ? globalFlexioImport.io.flexio.flex_types.ObjectValue.builder() : globalFlexioImport.io.flexio.flex_types.ObjectValue.from(this." + propertyName + "())))";
            baseMethod(builderCall);
        }
    }

    @Override
    public void process(YamlEnumExternalEnum externalEnum) throws ProcessingException {

    }

    @Override
    public void process(YamlEnumInSpecEnum inSpecEnum) throws ProcessingException {

    }

    @Override
    public void process(ValueObjectTypeExternalType externalType) throws ProcessingException {

    }

    @Override
    public void process(ParsedEnum parsedEnum) throws ProcessingException {

    }

    public void baseMethod(String builderCall) throws ProcessingException {
        try {
            jsClassGenerator.line("/**");
            jsClassGenerator.line(" * @param {function(builder:ObjectValueBuilder):?ObjectValue} " + propertyName);
            jsClassGenerator.line(" * @returns {" + objectName + "}");
            jsClassGenerator.line(" */");
            jsClassGenerator.line("withChanged" + NamingUtility.firstLetterUpperCase(propertyName) + "(" + propertyName + ") {");
            jsClassGenerator.line("TypeCheck.assertIsFunction(" + propertyName + ")");
            jsClassGenerator.line("let builder = " + builderName + ".from(this);");
            jsClassGenerator.line(builderCall);
            jsClassGenerator.line("return builder.build()");
            jsClassGenerator.line("}");
            jsClassGenerator.newLine();
        } catch (Exception e) {
            throw new ProcessingException("Error writing", e);
        }
    }



}

