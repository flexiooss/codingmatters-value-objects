package org.codingmatters.value.objects.php;

import org.codingmatters.value.objects.generation.preprocessor.PackagedValueSpec;
import org.codingmatters.value.objects.php.generator.PhpSpecPreprocessor;
import org.codingmatters.value.objects.php.generator.SpecReaderPhp;
import org.codingmatters.value.objects.spec.*;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SpecProcessorTest {

    public Spec loadSpec( String specName ) throws Exception {
        return new SpecReaderPhp().read(
                Thread.currentThread().getContextClassLoader().getResourceAsStream( specName )
        );
    }

    private List<PackagedValueSpec> getValues( String specName ) throws Exception {
        return new PhpSpecPreprocessor( loadSpec( specName ), "org.generated" ).packagedValueSpec();
    }

    @Test
    public void test_01_emptyObject() throws Exception {
        List<PackagedValueSpec> values = getValues( "01_emptyObject.yaml" );
        assertThat( values.size(), is( 1 ) );
        assertThat( values.get( 0 ).packagename(), is( "org.generated" ) );
        assertThat( values.get( 0 ).valueSpec().name(), is( "EmptyObject" ) );
        assertThat( values.get( 0 ).valueSpec().propertySpecs().size(), is( 0 ) );
    }

    @Test
    public void test_02_primitiveProps() throws Exception {
        List<PackagedValueSpec> values = getValues( "02_simpleObjectWithPrimitiveProperties.yaml" );
        assertThat( values.size(), is( 1 ) );
        PackagedValueSpec spec = values.get( 0 );
        assertThat( spec.packagename(), is( "org.generated" ) );
        assertThat( spec.valueSpec().name(), is( "PrimitiveProps" ) );
        assertThat( spec.valueSpec().propertySpecs().size(), is( 11 ) );
        assertThat( spec.valueSpec().propertySpecs().stream()
                .filter( property->property.typeSpec().typeKind() == TypeKind.JAVA_TYPE ).count(), is( 7L ) );
        assertThat( spec.valueSpec().propertySpecs().stream()
                .filter( property->property.typeSpec().typeKind() == TypeKind.EXTERNAL_VALUE_OBJECT ).count(), is( 4L ) );
        assertThat( spec.valueSpec().propertySpecs().stream()
                .anyMatch( property->property.typeSpec().cardinality() != PropertyCardinality.SINGLE ), is( false ) );
        assertThat( spec.valueSpec().propertySpec( "stringProp" ).typeSpec().typeRef(), is( "string" ) );
        assertThat( spec.valueSpec().propertySpec( "bytesProp" ).typeSpec().typeRef(), is( "string" ) );
        assertThat( spec.valueSpec().propertySpec( "integerProp" ).typeSpec().typeRef(), is( "int" ) );
        assertThat( spec.valueSpec().propertySpec( "longProp" ).typeSpec().typeRef(), is( "int" ) );
        assertThat( spec.valueSpec().propertySpec( "floatProp" ).typeSpec().typeRef(), is( "float" ) );
        assertThat( spec.valueSpec().propertySpec( "doubleProp" ).typeSpec().typeRef(), is( "float" ) );
        assertThat( spec.valueSpec().propertySpec( "booleanProp" ).typeSpec().typeRef(), is( "bool" ) );
        assertThat( spec.valueSpec().propertySpec( "dateProp" ).typeSpec().typeRef(), is( "io.flexio.utils.FlexDate" ) );
        assertThat( spec.valueSpec().propertySpec( "timeProp" ).typeSpec().typeRef(), is( "io.flexio.utils.FlexDate" ) );
        assertThat( spec.valueSpec().propertySpec( "dateTimeProp" ).typeSpec().typeRef(), is( "io.flexio.utils.FlexDate" ) );
        assertThat( spec.valueSpec().propertySpec( "tzDateTimeProp" ).typeSpec().typeRef(), is( "io.flexio.utils.FlexDate" ) );
    }

    @Test
    public void test_03_arrayOfPrimitiveProps() throws Exception {
        List<PackagedValueSpec> values = getValues( "03_simpleObjectWithPrimitiveList.yaml" );
        assertThat( values.size(), is( 1 ) );
        PackagedValueSpec spec = values.get( 0 );
        assertThat( spec.packagename(), is( "org.generated" ) );
        assertThat( spec.valueSpec().name(), is( "ArraySimpleProps" ) );
        assertThat( spec.valueSpec().propertySpecs().size(), is( 20 ) );
        assertThat( spec.valueSpec().propertySpecs().stream()
                .anyMatch( property->property.typeSpec().typeKind() != TypeKind.EXTERNAL_VALUE_OBJECT ), is( false ) );
        assertThat( spec.valueSpec().propertySpecs().stream()
                .filter( property->property.typeSpec().cardinality() == PropertyCardinality.LIST ).count(), is( 20L ) );

        assertThat( spec.valueSpec().propertySpec( "stringList" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef(), is( "string" ) );
        assertThat( spec.valueSpec().propertySpec( "integerList" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef(), is( "int" ) );
        assertThat( spec.valueSpec().propertySpec( "longList" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef(), is( "int" ) );
        assertThat( spec.valueSpec().propertySpec( "floatList" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef(), is( "float" ) );
        assertThat( spec.valueSpec().propertySpec( "doubleList" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef(), is( "float" ) );
        assertThat( spec.valueSpec().propertySpec( "booleanList" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef(), is( "bool" ) );
        assertThat( spec.valueSpec().propertySpec( "dateList" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef(), is( "io.flexio.utils.FlexDate" ) );
        assertThat( spec.valueSpec().propertySpec( "timeList" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef(), is( "io.flexio.utils.FlexDate" ) );
        assertThat( spec.valueSpec().propertySpec( "dateTimeList" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef(), is( "io.flexio.utils.FlexDate" ) );
        assertThat( spec.valueSpec().propertySpec( "tzDateTimeList" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef(), is( "io.flexio.utils.FlexDate" ) );

        assertThat( spec.valueSpec().propertySpec( "stringSet" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef(), is( "string" ) );
        assertThat( spec.valueSpec().propertySpec( "integerSet" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef(), is( "int" ) );
        assertThat( spec.valueSpec().propertySpec( "longSet" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef(), is( "int" ) );
        assertThat( spec.valueSpec().propertySpec( "floatSet" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef(), is( "float" ) );
        assertThat( spec.valueSpec().propertySpec( "doubleSet" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef(), is( "float" ) );
        assertThat( spec.valueSpec().propertySpec( "booleanSet" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef(), is( "bool" ) );
        assertThat( spec.valueSpec().propertySpec( "dateSet" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef(), is( "io.flexio.utils.FlexDate" ) );
        assertThat( spec.valueSpec().propertySpec( "timeSet" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef(), is( "io.flexio.utils.FlexDate" ) );
        assertThat( spec.valueSpec().propertySpec( "dateTimeSet" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef(), is( "io.flexio.utils.FlexDate" ) );
        assertThat( spec.valueSpec().propertySpec( "tzDateTimeSet" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef(), is( "io.flexio.utils.FlexDate" ) );
    }

    @Test
    public void test_04_objectWithSimpleEmbeddedValue() throws Exception {
        List<PackagedValueSpec> values = getValues( "04_objectWithEmbeddedValueSpec.yaml" );
        assertThat( values.size(), is( 2 ) );

        PackagedValueSpec rootSpec = values.get( 1 );
        PackagedValueSpec embeddedSpec = values.get( 0 );

        assertThat( rootSpec.packagename(), is( "org.generated" ) );
        assertThat( embeddedSpec.packagename(), is( "org.generated.complextype" ) );

        assertThat( rootSpec.valueSpec().propertySpecs().size(), is( 3 ) );
        assertThat( embeddedSpec.valueSpec().propertySpecs().size(), is( 2 ) );

        assertThat( rootSpec.valueSpec().propertySpecs().get( 0 ).typeSpec().typeKind(), is( TypeKind.IN_SPEC_VALUE_OBJECT ) );

        // ***** ROOT SPEC - PROP 1 ***** \\
        PropertySpec embeddedSpecProperty = rootSpec.valueSpec().propertySpecs().get( 0 );
        assertThat( embeddedSpecProperty.name(), is( "complexProps" ) );
        assertThat( embeddedSpecProperty.typeSpec().cardinality(), is( PropertyCardinality.SINGLE ) );
        assertThat( embeddedSpecProperty.typeSpec().typeKind(), is( TypeKind.IN_SPEC_VALUE_OBJECT ) );
        assertThat( embeddedSpecProperty.typeSpec().typeRef(), is( "org.generated.complextype.ComplexProps" ) );

        // ***** ROOT SPEC - PROP 2 ***** \\
        PropertySpec testIsOKProperty = rootSpec.valueSpec().propertySpecs().get( 1 );
        assertThat( testIsOKProperty.name(), is( "testIsOk" ) );
        assertThat( testIsOKProperty.typeSpec().cardinality(), is( PropertyCardinality.SINGLE ) );
        assertThat( testIsOKProperty.typeSpec().typeKind(), is( TypeKind.JAVA_TYPE ) );
        assertThat( testIsOKProperty.typeSpec().typeRef(), is( "bool" ) );

        // ***** ROOT SPEC - PROP 3 ***** \\
        PropertySpec fooProperty = rootSpec.valueSpec().propertySpecs().get( 2 );
        assertThat( fooProperty.name(), is( "foo" ) );
        assertThat( fooProperty.typeSpec().cardinality(), is( PropertyCardinality.SINGLE ) );
        assertThat( fooProperty.typeSpec().typeKind(), is( TypeKind.JAVA_TYPE ) );
        assertThat( fooProperty.typeSpec().typeRef(), is( "float" ) );

        assertThat( embeddedSpec.valueSpec().propertySpecs().size(), is( 2 ) );

        // ***** ROOT SPEC - PROP 1 ***** \\
        PropertySpec embeddedStringProperty = embeddedSpec.valueSpec().propertySpecs().get( 0 );
        assertThat( embeddedStringProperty.typeSpec().typeKind(), is( TypeKind.JAVA_TYPE ) );
        assertThat( embeddedStringProperty.name(), is( "stringProp" ) );
        assertThat( embeddedStringProperty.typeSpec().cardinality(), is( PropertyCardinality.SINGLE ) );
        assertThat( embeddedStringProperty.typeSpec().typeRef(), is( "string" ) );

        // ***** ROOT SPEC - PROP 2 ***** \\
        PropertySpec embeddedIntListProperty = embeddedSpec.valueSpec().propertySpecs().get( 1 );
        assertThat( embeddedIntListProperty.typeSpec().typeKind(), is( TypeKind.EXTERNAL_VALUE_OBJECT ) );
        assertThat( embeddedIntListProperty.name(), is( "intList" ) );
        assertThat( embeddedIntListProperty.typeSpec().cardinality(), is( PropertyCardinality.LIST ) );
        assertThat( embeddedIntListProperty.typeSpec().typeRef(), is( "org.generated.complextype.complexprops.ComplexPropsIntListList" ) );
    }

    @Test
    public void test_05_testObjectWithInSpecEnum() throws Exception {
        List<PackagedValueSpec> values = getValues( "05_objectWithInSpecEnum.yaml" );
        assertThat( values.size(), is( 1 ) );

        PackagedValueSpec spec = values.get( 0 );

        assertThat( spec.packagename(), is( "org.generated" ) );
        assertThat( spec.valueSpec().propertySpecs().size(), is( 2 ) );
        assertThat( spec.valueSpec().propertySpec( "single" ).typeSpec().typeKind(), is( TypeKind.ENUM ) );
        assertThat( spec.valueSpec().propertySpec( "single" ).typeSpec().typeRef(), is( "org.generated.inspecenumproperties.InSpecEnumPropertiesSingle" ) );
        assertThat( spec.valueSpec().propertySpec( "single" ).typeSpec().cardinality(), is( PropertyCardinality.SINGLE ) );
        assertThat( spec.valueSpec().propertySpec( "single" ).typeSpec().enumValues(), is( new String[]{ "SA", "SB", "SC" } ) );
        assertThat( spec.valueSpec().propertySpec( "single" ).typeSpec().isInSpecEnum(), is( true ) );

        assertThat( spec.valueSpec().propertySpec( "multiple" ).typeSpec().typeKind(), is( TypeKind.ENUM ) );
        assertThat( spec.valueSpec().propertySpec( "multiple" ).typeSpec().typeRef(), is( "org.generated.inspecenumproperties.InSpecEnumPropertiesMultipleList" ) );
        assertThat( spec.valueSpec().propertySpec( "multiple" ).typeSpec().cardinality(), is( PropertyCardinality.LIST ) );
        assertThat( spec.valueSpec().propertySpec( "multiple" ).typeSpec().enumValues(), is( new String[]{ "MA", "MB", "MC" } ) );
        assertThat( spec.valueSpec().propertySpec( "multiple" ).typeSpec().isInSpecEnum(), is( true ) );
    }

    @Test
    public void test_06_testObjectWithExternalEnum() throws Exception {
        List<PackagedValueSpec> values = getValues( "06_enumWithExternalType.yaml" );
        assertThat( values.size(), is( 1 ) );

        PackagedValueSpec spec = values.get( 0 );
        assertThat( spec.packagename(), is( "org.generated" ) );
        assertThat( spec.valueSpec().propertySpecs().size(), is( 2 ) );

        assertThat( spec.valueSpec().propertySpec( "single" ).typeSpec().typeKind(), is( TypeKind.ENUM ) );
        assertThat( spec.valueSpec().propertySpec( "single" ).typeSpec().typeRef(), is( "org.generated.enumproperties.EnumPropertiesSingle" ) );
        assertThat( spec.valueSpec().propertySpec( "single" ).typeSpec().cardinality(), is( PropertyCardinality.SINGLE ) );
        assertThat( spec.valueSpec().propertySpec( "single" ).typeSpec().isInSpecEnum(), is( true ) );

        assertThat( spec.valueSpec().propertySpec( "multiple" ).typeSpec().typeKind(), is( TypeKind.ENUM ) );
        assertThat( spec.valueSpec().propertySpec( "multiple" ).typeSpec().typeRef(), is( "org.generated.enumproperties.EnumPropertiesMultipleList" ) );
        assertThat( spec.valueSpec().propertySpec( "multiple" ).typeSpec().cardinality(), is( PropertyCardinality.LIST ) );
        assertThat( spec.valueSpec().propertySpec( "multiple" ).typeSpec().isInSpecEnum(), is( true ) );
    }

    @Test
    public void test_07_testObjectWithValueObjectProperties() throws Exception {
        List<PackagedValueSpec> values = getValues( "07_propertiesWithExternalValueObject.yaml" );
        assertThat( values.size(), is( 1 ) );

        ValueSpec spec = values.get( 0 ).valueSpec();

        assertThat( values.get( 0 ).packagename(), is( "org.generated" ) );
        assertThat( spec.propertySpecs().size(), is( 2 ) );

        assertThat( spec.propertySpec( "prop" ).name(), is( "prop" ) );
        assertThat( spec.propertySpec( "prop" ).typeSpec().cardinality(), is( PropertyCardinality.SINGLE ) );
        assertThat( spec.propertySpec( "prop" ).typeSpec().typeKind(), is( TypeKind.EXTERNAL_VALUE_OBJECT ) );
        assertThat( spec.propertySpec( "prop" ).typeSpec().typeRef(), is( "org.generated.PrimitiveProps" ) );

        assertThat( spec.propertySpec( "propList" ).name(), is( "propList" ) );
        assertThat( spec.propertySpec( "propList" ).typeSpec().cardinality(), is( PropertyCardinality.LIST ) );
        assertThat( spec.propertySpec( "propList" ).typeSpec().typeKind(), is( TypeKind.EXTERNAL_VALUE_OBJECT ) );
        assertThat( spec.propertySpec( "propList" ).typeSpec().typeRef(), is( "org.generated.valueobjectprops.ValueObjectPropsPropListList" ) );
    }

}
