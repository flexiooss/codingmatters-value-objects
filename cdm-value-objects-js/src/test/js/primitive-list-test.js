import {FlexDate, FlexDateTime, FlexTime, FlexZonedDateTime} from 'flexio-jshelpers'
import "../org/package"

test( 'test builder', () => {
    var builder = new window.FLEXIO_IMPORT_OBJECT.org.generated.ArraySimplePropsBuilder();
    builder.booleanList( [true, true, false] );
    builder.dateList( [new FlexDate( "2019-01-17" )] );
    builder.dateTimeList( [new FlexDateTime( "2019-01-17T14:15:37" )] );
    builder.timeList( [new FlexTime( "14:15:37" )] );
    builder.tzDateTimeList( [new FlexZonedDateTime( "2019-01-17T14:15:37+04:30" )] );
    builder.doubleList( [1.4, 1.7, 1.9] );
    builder.floatList( [1.5, 1.6, 1.9] );
    builder.integerList( [1, 2, 3] );
    builder.longList( [4, 5, 6] );
    builder.stringList( ["1", "2", "3"] );

    var arrayProps = builder.build();

    expect( arrayProps.booleanList() ).toEqual( [true, true, false] );
    expect( arrayProps.dateList()[0].toJSON() ).toEqual( "2019-01-17" );
    expect( arrayProps.dateTimeList()[0].toJSON() ).toEqual( "2019-01-17T14:15:37" );
    expect( arrayProps.timeList()[0].toJSON() ).toEqual( "14:15:37" );
    expect( arrayProps.tzDateTimeList()[0].toJSON() ).toEqual( "2019-01-17T14:15:37+04:30" );
    expect( arrayProps.doubleList() ).toEqual( [1.4, 1.7, 1.9] );
    expect( arrayProps.floatList() ).toEqual( [1.5, 1.6, 1.9] );
    expect( arrayProps.integerList() ).toEqual( [1, 2, 3] );
    expect( arrayProps.longList() ).toEqual( [4, 5, 6] );
    expect( arrayProps.stringList() ).toEqual( ["1", "2", "3"] );

    // TODO test check types
} );

test( 'assert object immutable', () => {
    var builder = new window.FLEXIO_IMPORT_OBJECT.org.generated.ArraySimplePropsBuilder();
    builder.booleanList( [true, true, false] );
    var primitiveProp = builder.build()

    expect( () => {
        primitiveProp.booleanList = 12.5
    } ).toThrow( TypeError );
} );

test( 'assert object frozen', () => {
    var builder = new window.FLEXIO_IMPORT_OBJECT.org.generated.ArraySimplePropsBuilder();
    builder.booleanList( [true, true, false] );

    var primitiveProp = builder.build();

    expect( () => {
        primitiveProp.newProp = 12.5
    } ).toThrow( TypeError );
} );

test( 'test serialization', () => {
    var builder = new window.FLEXIO_IMPORT_OBJECT.org.generated.ArraySimplePropsBuilder();
    builder.booleanList( [true, true, false] );
    builder.dateList( [new FlexDate( "2019-01-17" )] );
    builder.dateTimeList( [new FlexDateTime( "2019-01-17T14:15:37" )] );
    builder.timeList( [new FlexTime( "14:15:37" )] );
    builder.tzDateTimeList( [new FlexZonedDateTime( "2019-01-17T14:15:37+04:30" )] );
    builder.doubleList( [1.4, 1.7, 1.9] );
    builder.floatList( [1.5, 1.6, 1.9] );
    builder.integerList( [1, 2, 3] );
    builder.longList( [4, 5, 6] );
    builder.stringList( ["1", "2", "3"] );

    var arrayProps = builder.build();
    expect( JSON.stringify( arrayProps ) ).toBe( "{\"stringList\":[\"1\",\"2\",\"3\"],\"integerList\":[1,2,3],\"longList\":[4,5,6],\"floatList\":[1.5,1.6,1.9],\"doubleList\":[1.4,1.7,1.9],\"booleanList\":[true,true,false],\"dateList\":[\"2019-01-17\"],\"timeList\":[\"14:15:37\"],\"dateTimeList\":[\"2019-01-17T14:15:37\"],\"tzDateTimeList\":[\"2019-01-17T14:15:37+04:30\"]}" );
} );


test( 'test deserialization', () => {
    var json = "{\"stringList\":[\"1\",\"2\",\"3\"],\"integerList\":[1,2,3],\"longList\":[4,5,6],\"floatList\":[1.5,1.6,1.9],\"doubleList\":[1.4,1.7,1.9],\"booleanList\":[true,true,false],\"dateList\":[\"2019-01-17\"],\"timeList\":[\"14:15:37\"],\"dateTimeList\":[\"2019-01-17T14:15:37\"],\"tzDateTimeList\":[\"2019-01-17T14:15:37+04:30\"]}"

    var arrayProps = window.FLEXIO_IMPORT_OBJECT.org.generated.ArraySimplePropsBuilder.fromJson( json ).build();
    expect( arrayProps.booleanList() ).toEqual( [true, true, false] );
    expect( arrayProps.dateList()[0].toJSON() ).toEqual( "2019-01-17" );
    expect( arrayProps.dateTimeList()[0].toJSON() ).toEqual( "2019-01-17T14:15:37" );
    expect( arrayProps.timeList()[0].toJSON() ).toEqual( "14:15:37" );
    expect( arrayProps.tzDateTimeList()[0].toJSON() ).toEqual( "2019-01-17T14:15:37+04:30" );
    expect( arrayProps.doubleList() ).toEqual( [1.4, 1.7, 1.9] );
    expect( arrayProps.floatList() ).toEqual( [1.5, 1.6, 1.9] );
    expect( arrayProps.integerList() ).toEqual( [1, 2, 3] );
    expect( arrayProps.longList() ).toEqual( [4, 5, 6] );
    expect( arrayProps.stringList() ).toEqual( ["1", "2", "3"] );
} );