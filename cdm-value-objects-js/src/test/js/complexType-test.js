import {ComplexTypeBuilder} from '../org/generated/ComplexType'
import {ComplexPropsBuilder} from '../org/generated/complexType/ComplexProps'

test( 'test builder', () => {
    var propBuilder = new ComplexPropsBuilder();
    propBuilder.stringProp( "toto" );
    propBuilder.intList( [4, 7, 5] );
    var prop = propBuilder.build();

    var builder = new ComplexTypeBuilder();
    builder.complexProps( prop );
    builder.foo( "foo" );
    builder.testIsOk( true );
    var complexType = builder.build();

    expect( complexType.testIsOk() ).toEqual( true );
    expect( complexType.foo() ).toEqual( "foo" );
    expect( complexType.complexProps().stringProp() ).toEqual( "toto" );
    expect( complexType.complexProps().intList() ).toEqual( [4, 7, 5] );
} );

test( 'assert object immutable', () => {
 var propBuilder = new ComplexPropsBuilder();
    propBuilder.stringProp( "toto" );
    propBuilder.intList( [4, 7, 5] );
    var prop = propBuilder.build();

    var builder = new ComplexTypeBuilder();
    builder.complexProps( prop );
    builder.foo( "foo" );
    builder.testIsOk( true );
    var complexType = builder.build();

    expect( () => {
        complexType.complexProps = "bar"
    } ).toThrow( TypeError );

    expect( () => {
        complexType.complexProps.stringProp = "yoyo"
    } ).toThrow( TypeError );
} );

test( 'assert object frozen', () => {
    var propBuilder = new ComplexPropsBuilder();
    propBuilder.stringProp( "toto" );
    propBuilder.intList( [4, 7, 5] );
    var prop = propBuilder.build();

    var builder = new ComplexTypeBuilder();
    builder.complexProps( prop );
    builder.foo( "foo" );
    builder.testIsOk( true );
    var complexType = builder.build();

    expect( () => {
        complexType.toto = 12.5
    } ).toThrow( TypeError );

    expect( () => {
        complexType.complexProps.toto = 12.5
    } ).toThrow( TypeError );
} );

test( 'test serialization', () => {
    var propBuilder = new ComplexPropsBuilder();
    propBuilder.stringProp( "toto" );
    propBuilder.intList( [4, 7, 5] );
    var prop = propBuilder.build();

    var builder = new ComplexTypeBuilder();
    builder.complexProps( prop );
    builder.foo( "foo" );
    builder.testIsOk( true );
    var complexType = builder.build();

    expect( JSON.stringify( prop ) ).toBe( "{\"string-prop\":\"toto\",\"intList\":[4,7,5]}" );
    expect( JSON.stringify( complexType ) ).toBe( "{\"complexProps\":{\"string-prop\":\"toto\",\"intList\":[4,7,5]},\"test-is-ok\":true,\"foo\":\"foo\"}" );
} );

test( 'test deserialization', () => {
    var json = "{\"complexProps\":{\"string-prop\":\"toto\",\"intList\":[4,7,5]},\"test-is-ok\":true,\"foo\":\"foo\"}";
    var complexType = ComplexTypeBuilder.fromJson( json );
    expect( complexType.testIsOk() ).toEqual( true );
    expect( complexType.foo() ).toEqual( "foo" );
    expect( complexType.complexProps().stringProp() ).toEqual( "toto" );
    expect( complexType.complexProps().intList() ).toEqual( [4, 7, 5] );
});