package org.codingmatters.value.objects.js.parser;


import org.codingmatters.value.objects.js.error.SyntaxError;
import org.codingmatters.value.objects.js.parser.model.ParsedValueObject;
import org.codingmatters.value.objects.js.parser.model.ParsedYAMLSpec;
import org.codingmatters.value.objects.js.parser.model.types.*;
import org.junit.Test;

import java.io.InputStream;
import java.util.Stack;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class YamlSpecParserTest {

    private YamlSpecParser parser = new YamlSpecParser();

    @Test
    public void testEmptyObject() throws SyntaxError {
        InputStream resource = loadSpec( "01_emptyObject.yaml" );
        ParsedYAMLSpec spec = parser.parse( resource );
        assertThat( spec.valueObjects().size(), is( 1 ) );
        assertThat( spec.valueObjects().get( 0 ).name(), is( "EmptyObject" ) );
        assertThat( spec.valueObjects().get( 0 ).properties().size(), is( 0 ) );
    }

    @Test
    public void testPrimitiveProperties() throws SyntaxError {
        InputStream resource = loadSpec( "02_simpleObjectWithPrimitiveProperties.yaml" );
        ParsedYAMLSpec spec = parser.parse( resource );
        assertThat( spec.valueObjects().size(), is( 1 ) );
        ParsedValueObject value = spec.valueObjects().get( 0 );
        assertThat( value.name(), is( "PrimitiveProps" ) );

        assertThat( value.properties().get( 0 ).name(), is( "stringProp" ) );
        assertThat( ((ValueObjectTypePrimitiveType) value.properties().get( 0 ).type()).type(), is( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.STRING ) );

        assertThat( value.properties().get( 1 ).name(), is( "bytesProp" ) );
        assertThat( ((ValueObjectTypePrimitiveType) value.properties().get( 1 ).type()).type(), is( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.BYTES ) );

        assertThat( value.properties().get( 2 ).name(), is( "integerProp" ) );
        assertThat( ((ValueObjectTypePrimitiveType) value.properties().get( 2 ).type()).type(), is( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.INT ) );

        assertThat( value.properties().get( 3 ).name(), is( "longProp" ) );
        assertThat( ((ValueObjectTypePrimitiveType) value.properties().get( 3 ).type()).type(), is( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.LONG ) );

        assertThat( value.properties().get( 4 ).name(), is( "floatProp" ) );
        assertThat( ((ValueObjectTypePrimitiveType) value.properties().get( 4 ).type()).type(), is( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.FLOAT ) );

        assertThat( value.properties().get( 5 ).name(), is( "doubleProp" ) );
        assertThat( ((ValueObjectTypePrimitiveType) value.properties().get( 5 ).type()).type(), is( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.DOUBLE ) );

        assertThat( value.properties().get( 6 ).name(), is( "booleanProp" ) );
        assertThat( ((ValueObjectTypePrimitiveType) value.properties().get( 6 ).type()).type(), is( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.BOOL ) );

        assertThat( value.properties().get( 7 ).name(), is( "date-prop" ) );
        assertThat( ((ValueObjectTypePrimitiveType) value.properties().get( 7 ).type()).type(), is( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.DATE ) );

        assertThat( value.properties().get( 8 ).name(), is( "timeProp" ) );
        assertThat( ((ValueObjectTypePrimitiveType) value.properties().get( 8 ).type()).type(), is( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.TIME ) );

        assertThat( value.properties().get( 9 ).name(), is( "dateTimeProp" ) );
        assertThat( ((ValueObjectTypePrimitiveType) value.properties().get( 9 ).type()).type(), is( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.DATE_TIME ) );

        assertThat( value.properties().get( 10 ).name(), is( "tzDateTimeProp" ) );
        assertThat( ((ValueObjectTypePrimitiveType) value.properties().get( 10 ).type()).type(), is( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.TZ_DATE_TIME ) );
    }

    @Test
    public void testPrimitiveList() throws SyntaxError {
        InputStream resource = loadSpec( "03_simpleObjectWithPrimitiveList.yaml" );
        ParsedYAMLSpec spec = parser.parse( resource );
        assertThat( spec.valueObjects().size(), is( 1 ) );
        ParsedValueObject value = spec.valueObjects().get( 0 );

        assertThat( value.properties().get( 0 ).name(), is( "stringList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 0 ).type()).name(), is( "ArraySimplePropsStringListList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 0 ).type()).namespace(), is( "arraySimpleProps" ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ValueObjectTypeList) value.properties().get( 0 ).type()).type()).type(), is( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.STRING ) );

        assertThat( value.properties().get( 1 ).name(), is( "integerList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 1 ).type()).name(), is( "ArraySimplePropsIntegerListList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 1 ).type()).namespace(), is( "arraySimpleProps" ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ValueObjectTypeList) value.properties().get( 1 ).type()).type()).type(), is( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.INT ) );

        assertThat( value.properties().get( 2 ).name(), is( "longList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 2 ).type()).name(), is( "ArraySimplePropsLongListList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 2 ).type()).namespace(), is( "arraySimpleProps" ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ValueObjectTypeList) value.properties().get( 2 ).type()).type()).type(), is( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.LONG ) );

        assertThat( value.properties().get( 3 ).name(), is( "floatList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 3 ).type()).name(), is( "ArraySimplePropsFloatListList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 3 ).type()).namespace(), is( "arraySimpleProps" ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ValueObjectTypeList) value.properties().get( 3 ).type()).type()).type(), is( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.FLOAT ) );

        assertThat( value.properties().get( 4 ).name(), is( "doubleList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 4 ).type()).name(), is( "ArraySimplePropsDoubleListList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 4 ).type()).namespace(), is( "arraySimpleProps" ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ValueObjectTypeList) value.properties().get( 4 ).type()).type()).type(), is( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.DOUBLE ) );

        assertThat( value.properties().get( 5 ).name(), is( "booleanList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 5 ).type()).name(), is( "ArraySimplePropsBooleanListList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 5 ).type()).namespace(), is( "arraySimpleProps" ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ValueObjectTypeList) value.properties().get( 5 ).type()).type()).type(), is( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.BOOL ) );

        assertThat( value.properties().get( 6 ).name(), is( "dateList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 6 ).type()).name(), is( "ArraySimplePropsDateListList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 6 ).type()).namespace(), is( "arraySimpleProps" ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ValueObjectTypeList) value.properties().get( 6 ).type()).type()).type(), is( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.DATE ) );

        assertThat( value.properties().get( 7 ).name(), is( "timeList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 7 ).type()).name(), is( "ArraySimplePropsTimeListList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 7 ).type()).namespace(), is( "arraySimpleProps" ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ValueObjectTypeList) value.properties().get( 7 ).type()).type()).type(), is( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.TIME ) );

        assertThat( value.properties().get( 8 ).name(), is( "dateTimeList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 8 ).type()).name(), is( "ArraySimplePropsDateTimeListList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 8 ).type()).namespace(), is( "arraySimpleProps" ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ValueObjectTypeList) value.properties().get( 8 ).type()).type()).type(), is( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.DATE_TIME ) );

        assertThat( value.properties().get( 9 ).name(), is( "tzDateTimeList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 9 ).type()).name(), is( "ArraySimplePropsTzDateTimeListList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 9 ).type()).namespace(), is( "arraySimpleProps" ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ValueObjectTypeList) value.properties().get( 9 ).type()).type()).type(), is( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.TZ_DATE_TIME ) );

    }

    @Test
    public void testNestedObjectValue() throws Exception, SyntaxError {
        InputStream resource = loadSpec( "04_objectWithEmbeddedValueSpec.yaml" );
        ParsedYAMLSpec spec = parser.parse( resource );
        assertThat( spec.valueObjects().size(), is( 1 ) );
        ParsedValueObject value = spec.valueObjects().get( 0 );

        assertThat( value.properties().get( 0 ).name(), is( "complexProps" ) );
        assertThat( ((ObjectTypeNested) value.properties().get( 0 ).type()).namespace(), is( "complexType" ) );
        assertThat( ((ObjectTypeNested) value.properties().get( 0 ).type()).nestValueObject().name(), is( "ComplexProps" ) );
        assertThat( ((ObjectTypeNested) value.properties().get( 0 ).type()).nestValueObject().properties().size(), is( 2 ) );
        assertThat( ((ObjectTypeNested) value.properties().get( 0 ).type()).nestValueObject().properties().get( 0 ).name(), is( "string-prop" ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ObjectTypeNested) value.properties().get( 0 ).type()).nestValueObject().properties().get( 0 ).type()).type(), is( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.STRING ) );

        assertThat( ((ObjectTypeNested) value.properties().get( 0 ).type()).nestValueObject().properties().get( 1 ).name(), is( "intList" ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ValueObjectTypeList) ((ObjectTypeNested) value.properties().get( 0 ).type()).nestValueObject().properties().get( 1 ).type()).type()).type(), is( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.INT ) );
        assertThat( ((ValueObjectTypeList) ((ObjectTypeNested) value.properties().get( 0 ).type()).nestValueObject().properties().get( 1 ).type()).name(), is( "ComplexPropsIntListList" ) );
        assertThat( ((ValueObjectTypeList) ((ObjectTypeNested) value.properties().get( 0 ).type()).nestValueObject().properties().get( 1 ).type()).namespace(), is( "complexType.complexProps" ) );

        assertThat( value.properties().get( 1 ).name(), is( "test-is-ok" ) );
        assertThat( ((ValueObjectTypePrimitiveType) value.properties().get( 1 ).type()).type(), is( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.BOOL ) );

        assertThat( value.properties().get( 2 ).name(), is( "foo" ) );
        assertThat( ((ValueObjectTypePrimitiveType) value.properties().get( 2 ).type()).type(), is( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.FLOAT ) );
    }

    @Test
    public void testEnum() throws Exception, SyntaxError {
        InputStream resource = loadSpec( "05_objectWithInSpecEnum.yaml" );
        ParsedYAMLSpec spec = parser.parse( resource );
        assertThat( spec.valueObjects().size(), is( 1 ) );
        ParsedValueObject value = spec.valueObjects().get( 0 );

        assertThat( value.properties().get( 0 ).name(), is( "single" ) );
        assertThat( ((YamlEnumInSpecEnum) value.properties().get( 0 ).type()).name(), is( "InSpecEnumPropertiesSingle" ) );
        assertThat( ((YamlEnumInSpecEnum) value.properties().get( 0 ).type()).namespace(), is( "inSpecEnumProperties" ) );
        assertThat( ((YamlEnumInSpecEnum) value.properties().get( 0 ).type()).values().toArray(), is( new String[]{ "SA", "SB", "SC" } ) );

        assertThat( value.properties().get( 1 ).name(), is( "multiple" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 1 ).type()).name(), is( "InSpecEnumPropertiesMultipleList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 1 ).type()).namespace(), is( "inSpecEnumProperties" ) );
        assertThat( ((YamlEnumInSpecEnum) ((ValueObjectTypeList) value.properties().get( 1 ).type()).type()).name(), is( "InSpecEnumPropertiesMultiple" ) );
        assertThat( ((YamlEnumInSpecEnum) ((ValueObjectTypeList) value.properties().get( 1 ).type()).type()).namespace(), is( "inSpecEnumProperties" ) );
        assertThat( ((YamlEnumInSpecEnum) ((ValueObjectTypeList) value.properties().get( 1 ).type()).type()).values().toArray(), is( new String[]{ "MA", "MB", "MC" } ) );
    }

    @Test
    public void testReferencedTypes() throws Exception, SyntaxError {
        InputStream resource = loadSpec( "06_enumWithExternalType.yaml" );
        ParsedYAMLSpec spec = parser.parse( resource );
        assertThat( spec.valueObjects().size(), is( 2 ) );
        ParsedValueObject value = spec.valueObjects().get( 0 );

        assertThat( value.properties().get( 0 ).name(), is( "single" ) );
        assertThat( ((YamlEnumExternalEnum) value.properties().get( 0 ).type()).enumReference(), is( "java.time.DayOfWeek" ) );

        assertThat( value.properties().get( 1 ).name(), is( "multiple" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 1 ).type()).name(), is( "EnumPropertiesMultipleList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 1 ).type()).namespace(), is( "enumProperties" ) );
        assertThat( ((YamlEnumExternalEnum) ((ValueObjectTypeList) value.properties().get( 1 ).type()).type()).enumReference(), is( "java.time.DayOfWeek" ) );

        assertThat( value.properties().get( 2 ).name(), is( "reference" ) );
        assertThat( ((ObjectTypeExternalValue) value.properties().get( 2 ).type()).objectReference(), is( "org.generated.ref.ExtReferenced" ) );

        assertThat( value.properties().get( 3 ).name(), is( "referenceList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 3 ).type()).name(), is( "EnumPropertiesReferenceListList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 3 ).type()).namespace(), is( "enumProperties" ) );
        assertThat( ((ObjectTypeExternalValue) ((ValueObjectTypeList) value.properties().get( 3 ).type()).type()).objectReference(), is( "org.generated.ref.ExtReferenced" ) );

        assertThat( value.properties().get( 4 ).name(), is( "internalReference" ) );
        assertThat( ((ObjectTypeInSpecValueObject) value.properties().get( 4 ).type()).inSpecValueObjectName(), is( "internal" ) );
    }

    @Test
    public void testEnumListInNestedType() throws Exception, SyntaxError {
        InputStream resource = loadSpec( "07_enumListInEmbeddedSpec.yaml" );
        ParsedYAMLSpec spec = parser.parse( resource );
        assertThat( spec.valueObjects().size(), is( 1 ) );
        ParsedValueObject value = spec.valueObjects().get( 0 );
        Stack<String> context = new Stack<>();
        context.push( "enumListInEmbedded" );
        context.push( "complexProperty" );

        assertThat( value.properties().get( 0 ).name(), is( "dontCare" ) );
        assertThat( ((ValueObjectTypePrimitiveType) value.properties().get( 0 ).type()).type(), is( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.STRING ) );

        assertThat( value.properties().get( 1 ).name(), is( "complexProperty" ) );
        assertThat( ((ObjectTypeNested) value.properties().get( 1 ).type()).namespace(), is( "enumListInEmbedded" ) );
        assertThat( ((ObjectTypeNested) value.properties().get( 1 ).type()).nestValueObject().name(), is( "ComplexProperty" ) );
        assertThat( ((ObjectTypeNested) value.properties().get( 1 ).type()).nestValueObject().properties().size(), is( 2 ) );

        assertThat( ((ObjectTypeNested) value.properties().get( 1 ).type()).nestValueObject().properties().get( 0 ).name(), is( "stringProp" ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ObjectTypeNested) value.properties().get( 1 ).type()).nestValueObject().properties().get( 0 ).type()).type(), is( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.STRING ) );

        assertThat( ((ObjectTypeNested) value.properties().get( 1 ).type()).nestValueObject().properties().get( 1 ).name(), is( "enums" ) );
        assertThat( ((ValueObjectTypeList) ((ObjectTypeNested) value.properties().get( 1 ).type()).nestValueObject().properties().get( 1 ).type()).name(), is( "ComplexPropertyEnumsList" ) );
        assertThat( ((ValueObjectTypeList) ((ObjectTypeNested) value.properties().get( 1 ).type()).nestValueObject().properties().get( 1 ).type()).namespace(), is( "enumListInEmbedded.complexProperty" ) );
        assertThat( ((YamlEnumInSpecEnum) ((ValueObjectTypeList) ((ObjectTypeNested) value.properties().get( 1 ).type()).nestValueObject().properties().get( 1 ).type()).type()).name(), is( "ComplexPropertyEnums" ) );
        assertThat( ((YamlEnumInSpecEnum) ((ValueObjectTypeList) ((ObjectTypeNested) value.properties().get( 1 ).type()).nestValueObject().properties().get( 1 ).type()).type()).values().toArray(), is( new String[]{ "A", "B", "C" } ) );
    }

    private InputStream loadSpec( String name ) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream( name );
    }

    @Test
    public void testPrimitiveTypeRecognition() throws Exception {
        assertThat( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.from( "toto" ), is( nullValue() ) );
        assertThat( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.from( "date" ), is( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.DATE ) );
    }

}
