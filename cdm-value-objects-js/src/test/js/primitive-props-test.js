import {FLEXIO_IMPORT_OBJECT, FlexDate, FlexDateTime, FlexTime, FlexZonedDateTime} from 'flexio-jshelpers'
import "../org/package"

test( 'test builder', () => {
    var builder = new window.FLEXIO_IMPORT_OBJECT.org.generated.PrimitivePropsBuilder();
    builder.stringProp( "str" );
    builder.bytesProp( "bytes" );
    builder.integerProp( 9 );
    builder.longProp( 7 );
    builder.floatProp( 9.7 );
    builder.doubleProp( 7.9 );
    builder.booleanProp( true );

    var primitiveProp = builder.build();

    expect( primitiveProp.stringProp() ).toBe( "str" );
    expect( primitiveProp.bytesProp() ).toBe( "bytes" );
    expect( primitiveProp.integerProp() ).toBe( 9 );
    expect( primitiveProp.longProp() ).toBe( 7 );
    expect( primitiveProp.floatProp() ).toBe( 9.7 );
    expect( primitiveProp.doubleProp() ).toBe( 7.9 );
    expect( primitiveProp.booleanProp() ).toBe( true );
} );

test( 'assert object immutable', () => {
    var builder = new window.FLEXIO_IMPORT_OBJECT.org.generated.PrimitivePropsBuilder();
    builder.stringProp( "str" )

    var primitiveProp = builder.build()

    expect( () => {
        primitiveProp.floatProp = 12.5
    } ).toThrow( TypeError );

} );

test( 'assert object freezed', () => {
    var builder = new window.FLEXIO_IMPORT_OBJECT.org.generated.PrimitivePropsBuilder();
    builder.stringProp( "str" )

    var primitiveProp = builder.build()

    expect( () => {
        primitiveProp.newProp = 12.5
    } ).toThrow( TypeError );
} );

test( 'test serialization', () => {
    var builder = new window.FLEXIO_IMPORT_OBJECT.org.generated.PrimitivePropsBuilder();
    builder.stringProp( "str" );
    builder.bytesProp( "bytes" );
    builder.integerProp( 9 );
    builder.longProp( 7 );
    builder.floatProp( 9.7 );
    builder.doubleProp( 7.9 );
    builder.booleanProp( true );
    builder.dateProp( new FlexDate( "2019-01-09" ) );
    builder.timeProp( new FlexTime( "14:17:32" ) );
    builder.dateTimeProp( new FlexDateTime( "2019-01-09T14:17:32" ) );
    builder.tzDateTimeProp( new FlexZonedDateTime( "2019-01-09T14:17:32-03:00" ) );
    var primitiveProp = builder.build();
    expect( JSON.stringify( primitiveProp ) ).toBe( "{\"stringProp\":\"str\",\"bytesProp\":\"bytes\",\"integerProp\":9,\"longProp\":7,\"floatProp\":9.7,\"doubleProp\":7.9,\"booleanProp\":true,\"date-prop\":\"2019-01-09\",\"timeProp\":\"14:17:32\",\"dateTimeProp\":\"2019-01-09T14:17:32\",\"tzDateTimeProp\":\"2019-01-09T14:17:32-03:00\"}" );
} );

test( 'test deserialization', () => {
    var json = "{\"stringProp\":\"str\",\"bytesProp\":\"bytes\",\"integerProp\":9,\"longProp\":7,\"floatProp\":9.7,\"doubleProp\":7.9,\"booleanProp\":true,\"date-prop\":\"2019-01-09\",\"timeProp\":\"14:17:32\",\"dateTimeProp\":\"2019-01-09T14:17:32\",\"tzDateTimeProp\":\"2019-01-09T14:17:32-03:00\"}";
    var primitiveProp = window.FLEXIO_IMPORT_OBJECT.org.generated.PrimitivePropsBuilder.fromJson( json ).build();
    expect( primitiveProp.stringProp() ).toBe( "str" );
    expect( primitiveProp.bytesProp() ).toBe( "bytes" );
    expect( primitiveProp.integerProp() ).toBe( 9 );
    expect( primitiveProp.longProp() ).toBe( 7 );
    expect( primitiveProp.floatProp() ).toBe( 9.7 );
    expect( primitiveProp.doubleProp() ).toBe( 7.9 );
    expect( primitiveProp.booleanProp() ).toBe( true );
    expect( primitiveProp.dateProp().toJSON() ).toBe("2019-01-09" );
    expect( primitiveProp.timeProp().toJSON() ).toBe( "14:17:32" );
    expect( primitiveProp.dateTimeProp().toJSON() ).toBe( "2019-01-09T14:17:32" );
    expect( primitiveProp.tzDateTimeProp().toJSON() ).toBe(  "2019-01-09T14:17:32-03:00" );
    expect( typeof(primitiveProp.tzDateTimeProp() )).toBe( "object" );
    expect( typeof(primitiveProp.dateTimeProp() )).toBe( "object" );
    expect( typeof(primitiveProp.dateProp() )).toBe( "object" );
    expect( typeof(primitiveProp.timeProp() )).toBe( "object" );
} );

