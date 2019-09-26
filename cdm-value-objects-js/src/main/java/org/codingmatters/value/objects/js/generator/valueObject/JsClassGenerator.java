package org.codingmatters.value.objects.js.generator.valueObject;

import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.generator.JsFileWriter;
import org.codingmatters.value.objects.js.generator.NamingUtility;
import org.codingmatters.value.objects.js.generator.visitor.JsTypeAssertionProcessor;
import org.codingmatters.value.objects.js.generator.visitor.JsTypeReferenceProcessor;
import org.codingmatters.value.objects.js.generator.visitor.PropertiesDeserializationProcessor;
import org.codingmatters.value.objects.js.generator.visitor.PropertiesSerializationProcessor;
import org.codingmatters.value.objects.js.parser.model.ParsedValueObject;
import org.codingmatters.value.objects.js.parser.model.ValueObjectProperty;
import org.codingmatters.value.objects.js.parser.model.types.ValueObjectType;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.codingmatters.value.objects.js.generator.NamingUtility.attributeName;
import static org.codingmatters.value.objects.js.generator.NamingUtility.propertyName;

public class JsClassGenerator extends JsFileWriter {

    private final JsTypeReferenceProcessor jsTypeDescriptor;
    private final JsTypeAssertionProcessor jsTypeAssertion;
    private final String typesPackage;

    public JsClassGenerator(String filePath, String typesPackage) throws IOException {
        super(filePath);
        this.jsTypeDescriptor = new JsTypeReferenceProcessor(this);
        this.jsTypeAssertion = new JsTypeAssertionProcessor(this, typesPackage);
        this.typesPackage = typesPackage;
    }

    public void valueObjectClass(ParsedValueObject valueObject, String objectName, JsClassGenerator write) throws IOException, ProcessingException {
        String builderName = NamingUtility.builderName(objectName);
        write.line("class " + objectName + " {");
        write.constructor(valueObject.properties());
        write.valueObjectGetters(valueObject.properties());
        write.valueObjectWithMethods(valueObject.properties(), objectName, NamingUtility.builderName(objectName));
        write.valueObjectBuilderMethod(builderName);
        write.valueObjectFromInstanceMethod(objectName, builderName);
        write.valueObjectFromObjectMethod(builderName);
        write.valueObjectFromJSONMethod(builderName);
        write.valueObjectToObjectMethod(valueObject.properties());
        write.valueObjectToJsonMethod();
        write.line("}");
        write.newLine();
        write.line("export { " + objectName + " }");
    }

    public void constructor(List<ValueObjectProperty> properties) throws IOException {
        line("/**");
        properties.forEach(prop -> {
            try {
                indent();
                string(" * @param {");
                prop.type().process(jsTypeDescriptor);
                string("} " + NamingUtility.propertyName(prop.name()));
                newLine();
            } catch (Exception e) {
                System.out.println("Error processing constructor");
            }
        });
        line(" * @private");
        line(" */");
        List<String> names = properties.stream().map(prop -> propertyName(prop.name())).collect(Collectors.toList());
        line("constructor(" + String.join(", ", names) + ") {");

        for (ValueObjectProperty prop : properties){
            line("/**");
            line(" * @private");
            line(" */");
            line("this." + attributeName(prop.name()) + " = " + propertyName(prop.name()));
            newLine();
        }

        line("deepFreezeSeal(this)");
        line("}");
        newLine();
        flush();
    }

    public void valueObjectGetters(List<ValueObjectProperty> properties) throws IOException, ProcessingException {
        for (ValueObjectProperty property : properties) {
            line("/**");
            indent();
            string(" * @returns {");
            property.type().process(jsTypeDescriptor);
            string("}");
            newLine();
            line(" */");
            line(propertyName(property.name()) + "() {");
            line("return this." + attributeName(property.name()));
            line("}");
            newLine();
        }
    }

    public void valueObjectBuilderMethod(String builderName) throws IOException {
        line("/**");
        line(" * @returns {" + builderName + "}");
        line(" */");
        line("static builder() {");
        line("return new " + builderName + "()");
        line("}");
        newLine();
    }

    public void valueObjectFromInstanceMethod(String objectName, String builderName) throws IOException {
        line("/**");
        line(" * @param {" + objectName + "} instance");
        line(" * @returns {" + builderName + "}");
        line(" */");
        line("static from(instance) {");
        line("return " + builderName + ".from(instance)");
        line("}");
        newLine();
    }

    public void valueObjectFromObjectMethod(String builderName) throws IOException {
        line("/**");
        line(" * @param {Object} jsonObject");
        line(" * @returns {" + builderName + "}");
        line(" */");
        line("static fromObject(jsonObject) {");
        line("return " + builderName + ".fromObject(jsonObject)");
        line("}");
        newLine();
    }

    public void valueObjectFromJSONMethod(String builderName) throws IOException {
        line("/**");
        line(" * @param {string} json");
        line(" * @returns {" + builderName + "}");
        line(" */");
        line("static fromJson(json) {");
        line("return " + builderName + ".fromJson(json)");
        line("}");
        newLine();
    }

    public void valueObjectToObjectMethod(List<ValueObjectProperty> properties) throws IOException, ProcessingException {
        line("/**");
        line(" * @returns {Object}");
        line(" */");
        line("toObject() {");
        line("let jsonObject = {}");
        PropertiesSerializationProcessor propertiesSerializationProcessor = new PropertiesSerializationProcessor(this);
        for (ValueObjectProperty property : properties) {
            propertiesSerializationProcessor.process(property);
        }
        line("return jsonObject");
        line("}");
        newLine();
    }

    public void valueObjectToJsonMethod() throws IOException {
        line("/**");
        line(" * @returns {Object}");
        line(" */");
        line("toJSON() {");
        line("return this.toObject()");
        line("}");
    }

    public void builderClass(ParsedValueObject valueObject, String objectName, JsClassGenerator write) throws IOException, ProcessingException {
        String builderName = NamingUtility.builderName(objectName);
        write.line("class " + builderName + " {");
        write.line("/**");
        write.line(" * @constructor");
        write.line(" */");
        write.line("constructor() {");
        write.line(String.join("\n    ", valueObject.properties().stream().map(prop -> "this." + NamingUtility.attributeName(prop.name()) + " = null").collect(Collectors.toList())));
        write.line("}");
        write.line("");
        write.builderSetters(valueObject.properties(), builderName);
        write.builderBuildMethod(objectName, valueObject.properties());
        write.builderFromObjectMethod(builderName, valueObject.properties());
        write.builderFromJsonMethod(builderName);
        write.builderFromInstanceMethod(objectName, builderName, valueObject.properties());
        write.line("}");
        write.newLine();
        write.line("export { " + builderName + " }");
    }

    public void builderValidateElement(ValueObjectType type) throws ProcessingException, IOException {
//        line( "/**" );
//        indent();
//        string( "* @param {" );
//        type.process( jsTypeDescriptor );
//        string( "} element" );
//        newLine();
//        line( "* @protected" );
//        line( "* @throws Error" );
//        line( "*/" );
        line("_validate(element) {");
        jsTypeAssertion.currentVariable("element");
        type.process(jsTypeAssertion);
        line("}");
        newLine();
    }

    public void builderSetters(List<ValueObjectProperty> properties, String builderName) throws IOException, ProcessingException {
        for (ValueObjectProperty property : properties) {
            String propertyName = propertyName(property.name());
            line("/**");
            indent();
            string(" * @param {?");
            property.type().process(jsTypeDescriptor);
            string("} " + propertyName);
            newLine();
            line(" * @returns {" + builderName + "}");
            line(" */");
            line(propertyName + "(" + propertyName + ") {");
            property.process(jsTypeAssertion);
            line("this." + attributeName(property.name()) + " = " + propertyName);
            line("return this");
            line("}");
            newLine();
        }
    }

    private void valueObjectWithMethods(List<ValueObjectProperty> properties,String objectName, String builderName) throws IOException, ProcessingException {
        for (ValueObjectProperty property : properties) {
            String propertyName = propertyName(property.name());
            line("/**");
            indent();
            string(" * @param {");
            property.type().process(jsTypeDescriptor);
            string("} " + propertyName);
            newLine();
            line(" * @returns {" + objectName + "}");
            line(" */");
            line("with" + NamingUtility.firstLetterUpperCase(propertyName) + "(" + propertyName + ") {");
            line("let builder = " + builderName + ".from(this);");
            line("builder." + propertyName + "(" + propertyName + ")");
            line("return builder.build()");
            line("}");
            newLine();
        }
    }

    public void builderBuildMethod(String objectName, List<ValueObjectProperty> properties) throws IOException {
        line("/**");
        line(" * @returns {" + objectName + "}");
        line(" */");
        line("build() {");
        line("return new " + objectName + "(" +
                String.join(", ", properties.stream().map(prop -> "this." + attributeName(prop.name())).collect(Collectors.toList())) +
                ")");
        line("}");
        newLine();
    }

    public void builderFromObjectMethod(String builderName, List<ValueObjectProperty> properties) throws IOException, ProcessingException {
        line("/**");
        line(" * @param {Object} jsonObject");
        line(" * @returns {" + builderName + "}");
        line(" */");
        line("static fromObject(jsonObject) {");
        line("assertType(isObject(jsonObject), 'input should be an object')");
        line("let builder = new " + builderName + "()");
        PropertiesDeserializationProcessor propertiesDeserializationProcessor = new PropertiesDeserializationProcessor(this, typesPackage);
        for (ValueObjectProperty property : properties) {
            line("if (jsonObject['" + property.name() + "'] !== undefined) {");
            propertiesDeserializationProcessor.process(property);
            line("}");
        }
        line("return builder");
        line("}");
        newLine();
    }

    public void builderFromJsonMethod(String objectName) throws IOException {
        line("/**");
        line(" * @param {string} json");
        line(" * @returns {" + objectName + "}");
        line(" */");
        line("static fromJson(json) {");
        line("assertType(isString(json), 'input should be a string')");
        line("let jsonObject = JSON.parse(json)");
        line("return this.fromObject(jsonObject)");
        line("}");
        newLine();
    }

    private void builderFromInstanceMethod(String objectName, String builderName, List<ValueObjectProperty> properties) throws IOException {
        line("/**");
        line(" * @param {" + objectName + "} instance");
        line(" * @returns {" + builderName + "}");
        line(" */");
        line("static from(instance) {");
        line("assertType(instance instanceof "+objectName+", 'input should be an instance of "+objectName+"')");
        line("let builder = new " + builderName + "()");
        for (ValueObjectProperty property : properties) {
            String accessor = NamingUtility.propertyName(property.name());
            line("builder." + accessor + "(instance." + accessor + "())");
        }
        line("return builder");
        line("}");
    }

    public void elementAccessor(ValueObjectType type) throws IOException, ProcessingException {
        line("/**");
        line(" * @param {number} index");
        indent();
        string("* @returns {");
        type.process(jsTypeDescriptor);
        string("}");
        newLine();
        line(" */");
        line("get(index) {");
        line("return this[index]");
        line("}");
        newLine();
    }

    public void extendGenericTypeJsDoc(ValueObjectType type) throws IOException, ProcessingException {
        line("/**");
        indent();
        string(" * @extends {FlexArray<?");
        type.process(jsTypeDescriptor);
        string(">}");
        newLine();
        line(" */");
    }

    public void listConstructor() throws IOException {
        line("constructor(...args) {");
        line("super(...args)");
        line("}");
        newLine();
    }
}
