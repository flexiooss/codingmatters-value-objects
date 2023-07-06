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

    private YamlSpecParser parser = new YamlSpecParser( "org.generated" );

    @Test
    public void testEmptyObject() throws SyntaxError {
        InputStream resource = loadSpec( "01_emptyObject.yaml" );
        ParsedYAMLSpec spec = parser.parse( resource );
        assertThat( spec.valueObjects().size(), is( 1 ) );
        assertThat( spec.valueObjects().get( 0 ).name(), is( "EmptyObject" ) );
        assertThat( ((ParsedValueObject) spec.valueObjects().get( 0 )).properties().size(), is( 0 ) );
    }

    @Test
    public void testPrimitiveProperties() throws SyntaxError {
        InputStream resource = loadSpec( "02_simpleObjectWithPrimitiveProperties.yaml" );
        ParsedYAMLSpec spec = parser.parse( resource );
        assertThat( spec.valueObjects().size(), is( 1 ) );
        ParsedValueObject value = (ParsedValueObject) spec.valueObjects().get( 0 );
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
        ParsedValueObject value = (ParsedValueObject) spec.valueObjects().get( 0 );

        assertThat( value.properties().get( 0 ).name(), is( "stringList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 0 ).type()).name(), is( "ArraySimplePropsStringListList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 0 ).type()).packageName(), is( "org.generated.arraysimpleprops" ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ValueObjectTypeList) value.properties().get( 0 ).type()).type()).type(), is( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.STRING ) );

        assertThat( value.properties().get( 1 ).name(), is( "integerList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 1 ).type()).name(), is( "ArraySimplePropsIntegerListList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 1 ).type()).packageName(), is( "org.generated.arraysimpleprops" ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ValueObjectTypeList) value.properties().get( 1 ).type()).type()).type(), is( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.INT ) );

        assertThat( value.properties().get( 2 ).name(), is( "longList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 2 ).type()).name(), is( "ArraySimplePropsLongListList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 2 ).type()).packageName(), is( "org.generated.arraysimpleprops" ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ValueObjectTypeList) value.properties().get( 2 ).type()).type()).type(), is( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.LONG ) );

        assertThat( value.properties().get( 3 ).name(), is( "floatList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 3 ).type()).name(), is( "ArraySimplePropsFloatListList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 3 ).type()).packageName(), is( "org.generated.arraysimpleprops" ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ValueObjectTypeList) value.properties().get( 3 ).type()).type()).type(), is( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.FLOAT ) );

        assertThat( value.properties().get( 4 ).name(), is( "doubleList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 4 ).type()).name(), is( "ArraySimplePropsDoubleListList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 4 ).type()).packageName(), is( "org.generated.arraysimpleprops" ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ValueObjectTypeList) value.properties().get( 4 ).type()).type()).type(), is( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.DOUBLE ) );

        assertThat( value.properties().get( 5 ).name(), is( "booleanList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 5 ).type()).name(), is( "ArraySimplePropsBooleanListList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 5 ).type()).packageName(), is( "org.generated.arraysimpleprops" ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ValueObjectTypeList) value.properties().get( 5 ).type()).type()).type(), is( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.BOOL ) );

        assertThat( value.properties().get( 6 ).name(), is( "dateList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 6 ).type()).name(), is( "ArraySimplePropsDateListList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 6 ).type()).packageName(), is( "org.generated.arraysimpleprops" ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ValueObjectTypeList) value.properties().get( 6 ).type()).type()).type(), is( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.DATE ) );

        assertThat( value.properties().get( 7 ).name(), is( "timeList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 7 ).type()).name(), is( "ArraySimplePropsTimeListList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 7 ).type()).packageName(), is( "org.generated.arraysimpleprops" ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ValueObjectTypeList) value.properties().get( 7 ).type()).type()).type(), is( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.TIME ) );

        assertThat( value.properties().get( 8 ).name(), is( "dateTimeList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 8 ).type()).name(), is( "ArraySimplePropsDateTimeListList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 8 ).type()).packageName(), is( "org.generated.arraysimpleprops" ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ValueObjectTypeList) value.properties().get( 8 ).type()).type()).type(), is( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.DATE_TIME ) );

        assertThat( value.properties().get( 9 ).name(), is( "tzDateTimeList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 9 ).type()).name(), is( "ArraySimplePropsTzDateTimeListList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 9 ).type()).packageName(), is( "org.generated.arraysimpleprops" ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ValueObjectTypeList) value.properties().get( 9 ).type()).type()).type(), is( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.TZ_DATE_TIME ) );


        assertThat( value.properties().get( 10 ).name(), is( "stringSet" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 10 ).type()).name(), is( "ArraySimplePropsStringSetList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 10 ).type()).packageName(), is( "org.generated.arraysimpleprops" ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ValueObjectTypeList) value.properties().get( 10 ).type()).type()).type(), is( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.STRING ) );

        assertThat( value.properties().get( 11 ).name(), is( "integerSet" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 11 ).type()).name(), is( "ArraySimplePropsIntegerSetList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 11 ).type()).packageName(), is( "org.generated.arraysimpleprops" ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ValueObjectTypeList) value.properties().get( 11 ).type()).type()).type(), is( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.INT ) );

        assertThat( value.properties().get( 12 ).name(), is( "longSet" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 12 ).type()).name(), is( "ArraySimplePropsLongSetList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 12 ).type()).packageName(), is( "org.generated.arraysimpleprops" ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ValueObjectTypeList) value.properties().get( 12 ).type()).type()).type(), is( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.LONG ) );

        assertThat( value.properties().get( 13 ).name(), is( "floatSet" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 13 ).type()).name(), is( "ArraySimplePropsFloatSetList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 13 ).type()).packageName(), is( "org.generated.arraysimpleprops" ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ValueObjectTypeList) value.properties().get( 13 ).type()).type()).type(), is( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.FLOAT ) );

        assertThat( value.properties().get( 14 ).name(), is( "doubleSet" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 14 ).type()).name(), is( "ArraySimplePropsDoubleSetList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 14 ).type()).packageName(), is( "org.generated.arraysimpleprops" ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ValueObjectTypeList) value.properties().get( 14 ).type()).type()).type(), is( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.DOUBLE ) );

        assertThat( value.properties().get( 15 ).name(), is( "booleanSet" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 15 ).type()).name(), is( "ArraySimplePropsBooleanSetList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 15 ).type()).packageName(), is( "org.generated.arraysimpleprops" ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ValueObjectTypeList) value.properties().get( 15 ).type()).type()).type(), is( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.BOOL ) );

        assertThat( value.properties().get( 16 ).name(), is( "dateSet" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 16 ).type()).name(), is( "ArraySimplePropsDateSetList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 16 ).type()).packageName(), is( "org.generated.arraysimpleprops" ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ValueObjectTypeList) value.properties().get( 16 ).type()).type()).type(), is( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.DATE ) );

        assertThat( value.properties().get( 17 ).name(), is( "timeSet" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 17 ).type()).name(), is( "ArraySimplePropsTimeSetList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 17 ).type()).packageName(), is( "org.generated.arraysimpleprops" ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ValueObjectTypeList) value.properties().get( 17 ).type()).type()).type(), is( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.TIME ) );

        assertThat( value.properties().get( 18 ).name(), is( "dateTimeSet" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 18 ).type()).name(), is( "ArraySimplePropsDateTimeSetList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 18 ).type()).packageName(), is( "org.generated.arraysimpleprops" ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ValueObjectTypeList) value.properties().get( 18 ).type()).type()).type(), is( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.DATE_TIME ) );

        assertThat( value.properties().get( 19 ).name(), is( "tzDateTimeSet" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 19 ).type()).name(), is( "ArraySimplePropsTzDateTimeSetList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 19 ).type()).packageName(), is( "org.generated.arraysimpleprops" ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ValueObjectTypeList) value.properties().get( 19 ).type()).type()).type(), is( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.TZ_DATE_TIME ) );
    }

    @Test
    public void testNestedObjectValue() throws Exception, SyntaxError {
        InputStream resource = loadSpec( "04_objectWithEmbeddedValueSpec.yaml" );
        ParsedYAMLSpec spec = parser.parse( resource );
        assertThat( spec.valueObjects().size(), is( 1 ) );
        ParsedValueObject value = (ParsedValueObject) spec.valueObjects().get( 0 );

        assertThat( value.properties().get( 0 ).name(), is( "complex-props" ) );
        assertThat( ((ObjectTypeNested) value.properties().get( 0 ).type()).namespace(), is( "complextype" ) );
        assertThat( ((ObjectTypeNested) value.properties().get( 0 ).type()).nestValueObject().name(), is( "ComplexProps" ) );
        assertThat( ((ObjectTypeNested) value.properties().get( 0 ).type()).nestValueObject().properties().size(), is( 3 ) );
        assertThat( ((ObjectTypeNested) value.properties().get( 0 ).type()).nestValueObject().properties().get( 0 ).name(), is( "string-prop" ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ObjectTypeNested) value.properties().get( 0 ).type()).nestValueObject().properties().get( 0 ).type()).type(), is( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.STRING ) );

        assertThat( ((ObjectTypeNested) value.properties().get( 0 ).type()).nestValueObject().properties().get( 1 ).name(), is( "int-list" ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ValueObjectTypeList) ((ObjectTypeNested) value.properties().get( 0 ).type()).nestValueObject().properties().get( 1 ).type()).type()).type(), is( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.INT ) );
        assertThat( ((ValueObjectTypeList) ((ObjectTypeNested) value.properties().get( 0 ).type()).nestValueObject().properties().get( 1 ).type()).name(), is( "ComplexPropsIntListList" ) );
        assertThat( ((ValueObjectTypeList) ((ObjectTypeNested) value.properties().get( 0 ).type()).nestValueObject().properties().get( 1 ).type()).packageName(), is( "org.generated.complextype.complexprops" ) );

        assertThat( ((ObjectTypeNested) value.properties().get( 0 ).type()).nestValueObject().properties().get( 2 ).name(), is( "sub-complex-prop" ) );
        assertThat( ((ObjectTypeNested)((ObjectTypeNested) value.properties().get( 0 ).type()).nestValueObject().properties().get( 2 ).type()).nestValueObject().name(), is( "SubComplexProp" ) );
        assertThat( ((ObjectTypeNested)((ObjectTypeNested) value.properties().get( 0 ).type()).nestValueObject().properties().get( 2 ).type()).nestValueObject().properties().size(), is( 1 ) );
        assertThat( ((ObjectTypeNested)((ObjectTypeNested) value.properties().get( 0 ).type()).nestValueObject().properties().get( 2 ).type()).nestValueObject().properties().get( 0 ).name(), is( "string-prop" ) );
        assertThat( ((ValueObjectTypePrimitiveType)((ObjectTypeNested)((ObjectTypeNested) value.properties().get( 0 ).type()).nestValueObject().properties().get( 2 ).type()).nestValueObject().properties().get( 0 ).type()).type(), is( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.STRING ) );


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
        ParsedValueObject value = (ParsedValueObject) spec.valueObjects().get( 0 );

        assertThat( value.properties().get( 0 ).name(), is( "single" ) );
        assertThat( ((YamlEnumInSpecEnum) value.properties().get( 0 ).type()).name(), is( "Single" ) );
        assertThat( ((YamlEnumInSpecEnum) value.properties().get( 0 ).type()).namespace(), is( "inspecenumproperties" ) );
        assertThat( ((YamlEnumInSpecEnum) value.properties().get( 0 ).type()).values().toArray(), is( new String[]{ "SA", "SB", "SC" } ) );

        assertThat( value.properties().get( 1 ).name(), is( "multiple" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 1 ).type()).name(), is( "InSpecEnumPropertiesMultipleList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 1 ).type()).packageName(), is( "org.generated.inspecenumproperties" ) );
        assertThat( ((YamlEnumInSpecEnum) ((ValueObjectTypeList) value.properties().get( 1 ).type()).type()).name(), is( "Multiple" ) );
        assertThat( ((YamlEnumInSpecEnum) ((ValueObjectTypeList) value.properties().get( 1 ).type()).type()).namespace(), is( "inspecenumproperties" ) );
        assertThat( ((YamlEnumInSpecEnum) ((ValueObjectTypeList) value.properties().get( 1 ).type()).type()).values().toArray(), is( new String[]{ "MA", "MB", "MC" } ) );
    }

    @Test
    public void testReferencedTypes() throws Exception, SyntaxError {
        InputStream resource = loadSpec( "06_enumWithExternalType.yaml" );
        ParsedYAMLSpec spec = parser.parse( resource );
        assertThat( spec.valueObjects().size(), is( 2 ) );
        ParsedValueObject value = (ParsedValueObject) spec.valueObjects().get( 0 );

        assertThat( value.properties().get( 0 ).name(), is( "single" ) );
        assertThat( ((YamlEnumExternalEnum) value.properties().get( 0 ).type()).enumReference(), is( "java.time.DayOfWeek" ) );

        assertThat( value.properties().get( 1 ).name(), is( "multiple" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 1 ).type()).name(), is( "EnumPropertiesMultipleList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 1 ).type()).packageName(), is( "org.generated.enumproperties" ) );
        assertThat( ((YamlEnumExternalEnum) ((ValueObjectTypeList) value.properties().get( 1 ).type()).type()).enumReference(), is( "java.time.DayOfWeek" ) );

        assertThat( value.properties().get( 2 ).name(), is( "reference" ) );
        assertThat( ((ObjectTypeExternalValue) value.properties().get( 2 ).type()).objectReference(), is( "org.generated.ref.ExtReferenced" ) );

        assertThat( value.properties().get( 3 ).name(), is( "referenceList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 3 ).type()).name(), is( "EnumPropertiesReferenceListList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 3 ).type()).packageName(), is( "org.generated.enumproperties" ) );
        assertThat( ((ObjectTypeExternalValue) ((ValueObjectTypeList) value.properties().get( 3 ).type()).type()).objectReference(), is( "org.generated.ref.ExtReferenced" ) );

        assertThat( value.properties().get( 4 ).name(), is( "internalReference" ) );
        assertThat( ((ObjectTypeInSpecValueObject) value.properties().get( 4 ).type()).inSpecValueObjectName(), is( "internal" ) );

        assertThat( value.properties().get( 5 ).name(), is( "internalReferenceList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 5 ).type()).name(), is( "EnumPropertiesInternalReferenceListList" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 5 ).type()).packageName(), is( "org.generated.enumproperties" ) );
        assertThat( ((ObjectTypeInSpecValueObject) ((ValueObjectTypeList) value.properties().get( 5 ).type()).type()).inSpecValueObjectName(), is( "internal" ) );
    }

    @Test
    public void testEnumListInNestedType() throws Exception, SyntaxError {
        InputStream resource = loadSpec( "07_enumListInEmbeddedSpec.yaml" );
        ParsedYAMLSpec spec = parser.parse( resource );
        assertThat( spec.valueObjects().size(), is( 1 ) );
        ParsedValueObject value = (ParsedValueObject) spec.valueObjects().get( 0 );
        Stack<String> context = new Stack<>();
        context.push( "enumListInEmbedded" );
        context.push( "complexProperty" );

        assertThat( value.properties().get( 0 ).name(), is( "dontCare" ) );
        assertThat( ((ValueObjectTypePrimitiveType) value.properties().get( 0 ).type()).type(), is( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.STRING ) );

        assertThat( value.properties().get( 1 ).name(), is( "complexProperty" ) );
        assertThat( ((ObjectTypeNested) value.properties().get( 1 ).type()).namespace(), is( "enumlistinembedded" ) );
        assertThat( ((ObjectTypeNested) value.properties().get( 1 ).type()).nestValueObject().name(), is( "ComplexProperty" ) );
        assertThat( ((ObjectTypeNested) value.properties().get( 1 ).type()).nestValueObject().properties().size(), is( 2 ) );

        assertThat( ((ObjectTypeNested) value.properties().get( 1 ).type()).nestValueObject().properties().get( 0 ).name(), is( "stringProp" ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ObjectTypeNested) value.properties().get( 1 ).type()).nestValueObject().properties().get( 0 ).type()).type(), is( ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.STRING ) );

        assertThat( ((ObjectTypeNested) value.properties().get( 1 ).type()).nestValueObject().properties().get( 1 ).name(), is( "enums" ) );
        assertThat( ((ValueObjectTypeList) ((ObjectTypeNested) value.properties().get( 1 ).type()).nestValueObject().properties().get( 1 ).type()).name(), is( "ComplexPropertyEnumsList" ) );
        assertThat( ((ValueObjectTypeList) ((ObjectTypeNested) value.properties().get( 1 ).type()).nestValueObject().properties().get( 1 ).type()).packageName(), is( "org.generated.enumlistinembedded.complexproperty" ) );
        assertThat( ((YamlEnumInSpecEnum) ((ValueObjectTypeList) ((ObjectTypeNested) value.properties().get( 1 ).type()).nestValueObject().properties().get( 1 ).type()).type()).name(), is( "Enums" ) );
        assertThat( ((YamlEnumInSpecEnum) ((ValueObjectTypeList) ((ObjectTypeNested) value.properties().get( 1 ).type()).nestValueObject().properties().get( 1 ).type()).type()).values().toArray(), is( new String[]{ "A", "B", "C" } ) );
    }

    @Test
    public void testExternalType() throws Exception {
        InputStream resource = loadSpec( "08_propertiesWithExternalType.yaml" );
        ParsedYAMLSpec spec = parser.parse( resource );
        assertThat( spec.valueObjects().size(), is( 1 ) );
        ParsedValueObject value = (ParsedValueObject) spec.valueObjects().get( 0 );

        assertThat( value.properties().get( 0 ).name(), is( "prop" ) );
        assertThat( ((ValueObjectTypeExternalType) value.properties().get( 0 ).type()).typeReference(), is( "org.generated.PrimitiveProps" ) );

        assertThat( value.properties().get( 1 ).name(), is( "propList" ) );
        assertThat( ((ValueObjectTypeExternalType) ((ValueObjectTypeList) value.properties().get( 1 ).type()).type()).typeReference(), is( "org.generated.PrimitiveProps" ) );
        assertThat( ((ValueObjectTypeList) value.properties().get( 1 ).type()).packageName(), is( "org.generated.valueobjectprops" ) );
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
