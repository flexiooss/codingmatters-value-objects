import "../org/package"

test( 'test builder', () => {
    var propBuilder = new window.FLEXIO_IMPORT_OBJECT.org.generated.complextype.ComplexPropsBuilder();
    propBuilder.stringProp( "toto" );
    propBuilder.intList( [4, 7, 5] );
    var prop = propBuilder.build();

    var builder = new window.FLEXIO_IMPORT_OBJECT.org.generated.ComplexTypeBuilder();
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
 var propBuilder = new window.FLEXIO_IMPORT_OBJECT.org.generated.complextype.ComplexPropsBuilder();
    propBuilder.stringProp( "toto" );
    propBuilder.intList( [4, 7, 5] );
    var prop = propBuilder.build();

    var builder = new window.FLEXIO_IMPORT_OBJECT.org.generated.ComplexTypeBuilder();
    builder.complexProps( prop );
    builder.foo( "foo" );
    builder.testIsOk( true );
    var complexType = builder.build();

    expect( () => {
        complexType.complexProps = "bar"
    } ).toThrow( TypeError );

    expect( () => {
        complexType.complexProps()._stringProp = "yoyo"
    } ).toThrow( TypeError );
} );

test( 'assert object frozen', () => {
    var propBuilder = new window.FLEXIO_IMPORT_OBJECT.org.generated.complextype.ComplexPropsBuilder();
    propBuilder.stringProp( "toto" );
    propBuilder.intList( [4, 7, 5] );
    var prop = propBuilder.build();

    var builder = new window.FLEXIO_IMPORT_OBJECT.org.generated.ComplexTypeBuilder();
    builder.complexProps( prop );
    builder.foo( "foo" );
    builder.testIsOk( true );
    var complexType = builder.build();

    expect( () => {
        complexType.toto = 12.5
    } ).toThrow( TypeError );

    expect( () => {
        complexType.complexProps().toto = 12.5
    } ).toThrow( TypeError );
} );

test( 'test serialization', () => {
    var propBuilder = new window.FLEXIO_IMPORT_OBJECT.org.generated.complextype.ComplexPropsBuilder();
    propBuilder.stringProp( "toto" );
    propBuilder.intList( [4, 7, 5] );
    var prop = propBuilder.build();

    var builder = new window.FLEXIO_IMPORT_OBJECT.org.generated.ComplexTypeBuilder();
    builder.complexProps( prop );
    builder.foo( "foo" );
    builder.testIsOk( true );
    var complexType = builder.build();

    expect( JSON.stringify( prop ) ).toBe( "{\"string-prop\":\"toto\",\"intList\":[4,7,5]}" );
    expect( JSON.stringify( complexType ) ).toBe( "{\"complexProps\":{\"string-prop\":\"toto\",\"intList\":[4,7,5]},\"test-is-ok\":true,\"foo\":\"foo\"}" );
} );

test( 'test deserialization', () => {
    var json = "{\"complexProps\":{\"string-prop\":\"toto\",\"intList\":[4,7,5]},\"test-is-ok\":true,\"foo\":2.1}";
    var complexType = window.FLEXIO_IMPORT_OBJECT.org.generated.ComplexTypeBuilder.fromJson( json ).build();
    expect( complexType.testIsOk() ).toEqual( true );
    expect( complexType.foo() ).toEqual( 2.1 );
    expect( complexType.complexProps().stringProp() ).toEqual( "toto" );
    expect( complexType.complexProps().intList() ).toEqual( [4, 7, 5] );
});