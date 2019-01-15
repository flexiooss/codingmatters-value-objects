package org.codingmatters.value.objects.js.parser;

import org.codingmatters.value.objects.js.error.SyntaxError;
import org.codingmatters.value.objects.js.parser.model.ParsedValueObject;
import org.codingmatters.value.objects.js.parser.model.ParsedYAMLSpec;
import org.codingmatters.value.objects.js.parser.model.ValueObjectProperty;
import org.codingmatters.value.objects.js.parser.model.types.*;
import org.junit.Test;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

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
        assertThat( spec.valueObjects().get( 0 ).name(), is( "emptyObject" ) );
        assertThat( spec.valueObjects().get( 0 ).properties().size(), is( 0 ) );
    }

    @Test
    public void testPrimitiveProperties() throws SyntaxError {
        InputStream resource = loadSpec( "02_simpleObjectWithPrimitiveProperties.yaml" );
        ParsedYAMLSpec spec = parser.parse( resource );
        assertThat( spec.valueObjects().size(), is( 1 ) );
        ParsedValueObject value = spec.valueObjects().get( 0 );

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

        assertThat( value.properties().get( 1 ).name(), is( "foo" ) );
        assertThat( ((ValueObjectTypePrimitiveType) value.properties().get( 2 ).type()).type(), is( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.FLOAT ) );
    }

    @Test
    public void testEnum() throws Exception, SyntaxError {
        InputStream resource = loadSpec( "05_objectWithInSpecEnum.yaml" );
        ParsedYAMLSpec spec = parser.parse( resource );
        assertThat( spec.valueObjects().size(), is( 1 ) );
        ParsedValueObject value = spec.valueObjects().get( 0 );
        Stack<String> context = new Stack();
        context.push( "inSpecEnumProperties" );
        context.push( "single" );
        assertThat( value.properties().get( 0 ), is( new ValueObjectProperty( "single", new YamlEnumInSpecEnum( "InSpecEnumPropertiesSingle", context, "SA", "SB", "SC" ) ) ) );
        context = new Stack();
        context.push( "inSpecEnumProperties" );
        context.push( "multiple" );
        assertThat( value.properties().get( 1 ), is( new ValueObjectProperty( "multiple", new ValueObjectTypeList( "InSpecEnumPropertiesMultipleList", new YamlEnumInSpecEnum( "InSpecEnumPropertiesMultiple", context, "MA", "MB", "MC" ), context ) ) ) );
    }

    @Test
    public void testReferencedTypes() throws Exception, SyntaxError {
        InputStream resource = loadSpec( "06_enumWithExternalType.yaml" );
        ParsedYAMLSpec spec = parser.parse( resource );
        assertThat( spec.valueObjects().size(), is( 2 ) );
        ParsedValueObject value = spec.valueObjects().get( 0 );
        assertThat( value.properties().get( 0 ), is( new ValueObjectProperty( "single", new YamlEnumExternalEnum( "java.time.DayOfWeek" ) ) ) );
        List<String> context = Arrays.stream( new String[]{ "enumProperties", "multiple" } ).collect( Collectors.toList() );
        assertThat( value.properties().get( 1 ), is( new ValueObjectProperty( "multiple", new ValueObjectTypeList( "EnumPropertiesMultipleList", new YamlEnumExternalEnum( "java.time.DayOfWeek" ), context ) ) ) );
        assertThat( value.properties().get( 2 ), is( new ValueObjectProperty( "reference", new ObjectTypeExternalValue( "org.generated.ref.ExtReferenced" ) ) ) );
        context = Arrays.stream( new String[]{ "enumProperties", "referenceList" } ).collect( Collectors.toList() );
        assertThat( value.properties().get( 3 ), is( new ValueObjectProperty( "referenceList", new ValueObjectTypeList( "EnumPropertiesReferenceListList", new ObjectTypeExternalValue( "org.generated.ref.ExtReferenced" ), context ) ) ) );
        assertThat( value.properties().get( 4 ), is( new ValueObjectProperty( "internalReference", new ObjectTypeInSpecValueObject( "internal" ) ) ) );

        assertThat( spec.valueObjects().get( 1 ).properties().get( 0 ), is( new ValueObjectProperty( "toto", new ValueObjectTypePrimitiveType( "string" ) ) ) );
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
        ParsedValueObject complexProperty = new ParsedValueObject( "" );

        complexProperty.properties().add( new ValueObjectProperty( "stringProp", new ValueObjectTypePrimitiveType( "string" ) ) );
        complexProperty.properties().add( new ValueObjectProperty( "enums", new ValueObjectTypeList( "EnumListInEmbeddedComplexPropertyEnumsList", new YamlEnumInSpecEnum( "EnumListInEmbeddedComplexPropertyEnums", context, "A", "B", "C" ), context ) ) );
        ObjectTypeNested nestedObjectType = new ObjectTypeNested( complexProperty, "" );

        assertThat( value.properties().get( 0 ), is( new ValueObjectProperty( "dontCare", new ValueObjectTypePrimitiveType( "string" ) ) ) );
        assertThat( value.properties().get( 1 ), is( new ValueObjectProperty( "complexProperty", nestedObjectType ) ) );
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
