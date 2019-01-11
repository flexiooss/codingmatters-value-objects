package org.codingmatters.value.objects.js.parser;

import org.codingmatters.value.objects.js.error.SyntaxError;
import org.codingmatters.value.objects.js.parser.model.ParsedValueObject;
import org.codingmatters.value.objects.js.parser.model.ParsedYAMLSpec;
import org.codingmatters.value.objects.js.parser.model.types.*;
import org.junit.Test;

import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class SpecParserJsTest {

    private SpecParserJs parser = new SpecParserJs();

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
        assertThat( value.properties().get( 0 ), is( new ValueObjectProperty( "stringProp", new PrimitiveYamlType( "string" ) ) ) );
        assertThat( value.properties().get( 1 ), is( new ValueObjectProperty( "bytesProp", new PrimitiveYamlType( "bytes" ) ) ) );
        assertThat( value.properties().get( 2 ), is( new ValueObjectProperty( "integerProp", new PrimitiveYamlType( "int" ) ) ) );
        assertThat( value.properties().get( 3 ), is( new ValueObjectProperty( "longProp", new PrimitiveYamlType( "long" ) ) ) );
        assertThat( value.properties().get( 4 ), is( new ValueObjectProperty( "floatProp", new PrimitiveYamlType( "float" ) ) ) );
        assertThat( value.properties().get( 5 ), is( new ValueObjectProperty( "doubleProp", new PrimitiveYamlType( "double" ) ) ) );
        assertThat( value.properties().get( 6 ), is( new ValueObjectProperty( "booleanProp", new PrimitiveYamlType( "bool" ) ) ) );
        assertThat( value.properties().get( 7 ), is( new ValueObjectProperty( "date-prop", new PrimitiveYamlType( "date" ) ) ) );
        assertThat( value.properties().get( 8 ), is( new ValueObjectProperty( "timeProp", new PrimitiveYamlType( "time" ) ) ) );
        assertThat( value.properties().get( 9 ), is( new ValueObjectProperty( "dateTimeProp", new PrimitiveYamlType( "date-time" ) ) ) );
        assertThat( value.properties().get( 10 ), is( new ValueObjectProperty( "tzDateTimeProp", new PrimitiveYamlType( "tz-date-time" ) ) ) );
    }

    @Test
    public void testPrimitiveList() throws SyntaxError {
        InputStream resource = loadSpec( "03_simpleObjectWithPrimitiveList.yaml" );
        ParsedYAMLSpec spec = parser.parse( resource );
        assertThat( spec.valueObjects().size(), is( 1 ) );
        ParsedValueObject value = spec.valueObjects().get( 0 );
        assertThat( value.properties().get( 0 ), is( new ValueObjectProperty( "stringList", new ListType( new PrimitiveYamlType( "string" ) ) ) ) );
        assertThat( value.properties().get( 1 ), is( new ValueObjectProperty( "integerList", new ListType( new PrimitiveYamlType( "int" ) ) ) ) );
        assertThat( value.properties().get( 2 ), is( new ValueObjectProperty( "longList", new ListType( new PrimitiveYamlType( "long" ) ) ) ) );
        assertThat( value.properties().get( 3 ), is( new ValueObjectProperty( "floatList", new ListType( new PrimitiveYamlType( "float" ) ) ) ) );
        assertThat( value.properties().get( 4 ), is( new ValueObjectProperty( "doubleList", new ListType( new PrimitiveYamlType( "double" ) ) ) ) );
        assertThat( value.properties().get( 5 ), is( new ValueObjectProperty( "booleanList", new ListType( new PrimitiveYamlType( "bool" ) ) ) ) );
        assertThat( value.properties().get( 6 ), is( new ValueObjectProperty( "dateList", new ListType( new PrimitiveYamlType( "date" ) ) ) ) );
        assertThat( value.properties().get( 7 ), is( new ValueObjectProperty( "timeList", new ListType( new PrimitiveYamlType( "time" ) ) ) ) );
        assertThat( value.properties().get( 8 ), is( new ValueObjectProperty( "dateTimeList", new ListType( new PrimitiveYamlType( "date-time" ) ) ) ) );
        assertThat( value.properties().get( 9 ), is( new ValueObjectProperty( "tzDateTimeList", new ListType( new PrimitiveYamlType( "tz-date-time" ) ) ) ) );
    }

    @Test
    public void testNestedObjectValue() throws Exception, SyntaxError {
        InputStream resource = loadSpec( "04_objectWithEmbeddedValueSpec.yaml" );
        ParsedYAMLSpec spec = parser.parse( resource );
        assertThat( spec.valueObjects().size(), is( 1 ) );
        ParsedValueObject value = spec.valueObjects().get( 0 );
        ParsedValueObject complexTypeComplexProps = new ParsedValueObject( "ComplexTypeComplexProps" );
        complexTypeComplexProps.properties().add( new ValueObjectProperty( "string-prop", new PrimitiveYamlType( "string" ) ) );
        complexTypeComplexProps.properties().add( new ValueObjectProperty( "intList", new ListType( new PrimitiveYamlType( "int" ) ) ) );
        assertThat( value.properties().get( 0 ), is( new ValueObjectProperty( "complexProps", new NestedObjectType( complexTypeComplexProps ) ) ) );
        assertThat( value.properties().get( 1 ), is( new ValueObjectProperty( "test-is-ok", new PrimitiveYamlType( "bool" ) ) ) );
        assertThat( value.properties().get( 2 ), is( new ValueObjectProperty( "foo", new PrimitiveYamlType( "float" ) ) ) );
    }

    @Test
    public void testEnum() throws Exception, SyntaxError {
        InputStream resource = loadSpec( "05_objectWithInSpecEnum.yaml" );
        ParsedYAMLSpec spec = parser.parse( resource );
        assertThat( spec.valueObjects().size(), is( 1 ) );
        ParsedValueObject value = spec.valueObjects().get( 0 );
        assertThat( value.properties().get( 0 ), is( new ValueObjectProperty( "single", new InSpecEnum( "SA", "SB", "SC" ) ) ) );
        assertThat( value.properties().get( 1 ), is( new ValueObjectProperty( "multiple", new ListType( new InSpecEnum( "MA", "MB", "MC" ) ) ) ) );
    }

    @Test
    public void testReferencedTypes() throws Exception, SyntaxError {
        InputStream resource = loadSpec( "06_enumWithExternalType.yaml" );
        ParsedYAMLSpec spec = parser.parse( resource );
        assertThat( spec.valueObjects().size(), is( 2 ) );
        ParsedValueObject value = spec.valueObjects().get( 0 );
        assertThat( value.properties().get( 0 ), is( new ValueObjectProperty( "single", new ExternalEnum( "java.time.DayOfWeek" ) ) ) );
        assertThat( value.properties().get( 1 ), is( new ValueObjectProperty( "multiple", new ListType( new ExternalEnum( "java.time.DayOfWeek" ) ) ) ) );
        assertThat( value.properties().get( 2 ), is( new ValueObjectProperty( "reference", new ExternalValueObjectType( "org.generated.ref.ExtReferenced" ) ) ) );
        assertThat( value.properties().get( 3 ), is( new ValueObjectProperty( "referenceList", new ListType( new ExternalValueObjectType( "org.generated.ref.ExtReferenced" ) ) ) ) );
        assertThat( value.properties().get( 4 ), is( new ValueObjectProperty( "internalReference", new InSpecValueObjectType( "internal" ) ) ) );

        assertThat( spec.valueObjects().get( 1 ).properties().get( 0 ), is( new ValueObjectProperty( "toto", new PrimitiveYamlType( "string" ) ) ) );
    }

    @Test
    public void testEnumListInNestedType() throws Exception, SyntaxError {
        InputStream resource = loadSpec( "07_enumListInEmbeddedSpec.yaml" );
        ParsedYAMLSpec spec = parser.parse( resource );
        assertThat( spec.valueObjects().size(), is( 1 ) );
        ParsedValueObject value = spec.valueObjects().get( 0 );
        ParsedValueObject complexProperty = new ParsedValueObject( "EnumListInEmbeddedComplexProperty" );
        complexProperty.properties().add( new ValueObjectProperty( "stringProp", new PrimitiveYamlType( "string" ) ) );
        complexProperty.properties().add( new ValueObjectProperty( "enums", new ListType( new InSpecEnum( "A", "B", "C" ) ) ) );
        NestedObjectType nestedObjectType = new NestedObjectType( complexProperty );

        assertThat( value.properties().get( 0 ), is( new ValueObjectProperty( "dontCare", new PrimitiveYamlType( "string" ) ) ) );
        assertThat( value.properties().get( 1 ), is( new ValueObjectProperty( "complexProperty", nestedObjectType ) ) );
    }

    private InputStream loadSpec( String name ) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream( name );
    }

    @Test
    public void testPrimitiveTypeRecognition() throws Exception {
        assertThat( PrimitiveYamlType.YAML_PRIMITIVE_TYPES.from( "toto" ), is( nullValue() ) );
        assertThat( PrimitiveYamlType.YAML_PRIMITIVE_TYPES.from( "date" ), is( PrimitiveYamlType.YAML_PRIMITIVE_TYPES.DATE ) );
    }

}
