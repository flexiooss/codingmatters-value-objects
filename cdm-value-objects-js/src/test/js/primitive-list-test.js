import {TestCase} from 'code-altimeter-js'
const assert = require('assert')
import {FlexDate, FlexDateTime, FlexTime, FlexZonedDateTime} from 'flexio-jshelpers'
import "../org/package"

class PrimitiveListTest extends TestCase {

    testBuilder() {
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

        assert.deepEqual( arrayProps.booleanList() , [true, true, false] );
        assert.equal( arrayProps.dateList()[0].toJSON() , "2019-01-17" );
        assert.equal( arrayProps.dateTimeList()[0].toJSON() , "2019-01-17T14:15:37" );
        assert.equal( arrayProps.timeList()[0].toJSON() , "14:15:37" );
        assert.equal( arrayProps.tzDateTimeList()[0].toJSON() , "2019-01-17T14:15:37+04:30" );
        assert.deepEqual( arrayProps.doubleList() , [1.4, 1.7, 1.9] );
        assert.deepEqual( arrayProps.floatList() , [1.5, 1.6, 1.9] );
        assert.deepEqual( arrayProps.integerList() , [1, 2, 3] );
        assert.deepEqual( arrayProps.longList() , [4, 5, 6] );
        assert.deepEqual( arrayProps.stringList() , ["1", "2", "3"] );

        // TODO test check types
    }

    testObjectImmutable() {
        var builder = new window.FLEXIO_IMPORT_OBJECT.org.generated.ArraySimplePropsBuilder();
        builder.booleanList( [true, true, false] );
        var primitiveProp = builder.build()

        assert.throws(() => {
            primitiveProp.booleanList = 12.5
        }, TypeError );
    }

    testObjectFrozen() {
        var builder = new window.FLEXIO_IMPORT_OBJECT.org.generated.ArraySimplePropsBuilder();
        builder.booleanList( [true, true, false] );

        var primitiveProp = builder.build();

        assert.throws(() => {
            primitiveProp.newProp = 12.5
        }, TypeError );
    }

    testSerialization() {
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
        assert.equal( JSON.stringify( arrayProps ), "{\"stringList\":[\"1\",\"2\",\"3\"],\"integerList\":[1,2,3],\"longList\":[4,5,6],\"floatList\":[1.5,1.6,1.9],\"doubleList\":[1.4,1.7,1.9],\"booleanList\":[true,true,false],\"dateList\":[\"2019-01-17\"],\"timeList\":[\"14:15:37\"],\"dateTimeList\":[\"2019-01-17T14:15:37\"],\"tzDateTimeList\":[\"2019-01-17T14:15:37+04:30\"]}" );
    }

    testDeserialization() {
        var json = "{\"stringList\":[\"1\",\"2\",\"3\"],\"integerList\":[1,2,3],\"longList\":[4,5,6],\"floatList\":[1.5,1.6,1.9],\"doubleList\":[1.4,1.7,1.9],\"booleanList\":[true,true,false],\"dateList\":[\"2019-01-17\"],\"timeList\":[\"14:15:37\"],\"dateTimeList\":[\"2019-01-17T14:15:37\"],\"tzDateTimeList\":[\"2019-01-17T14:15:37+04:30\"]}"

        var arrayProps = window.FLEXIO_IMPORT_OBJECT.org.generated.ArraySimplePropsBuilder.fromJson( json ).build();
        assert.deepEqual( arrayProps.booleanList(), [true, true, false] );
        assert.equal( arrayProps.dateList()[0].toJSON(), "2019-01-17" );
        assert.equal( arrayProps.dateTimeList()[0].toJSON() , "2019-01-17T14:15:37" );
        assert.equal( arrayProps.timeList()[0].toJSON() , "14:15:37" );
        assert.equal( arrayProps.tzDateTimeList()[0].toJSON() , "2019-01-17T14:15:37+04:30" );
        assert.deepEqual( arrayProps.doubleList() , [1.4, 1.7, 1.9] );
        assert.deepEqual( arrayProps.floatList() , [1.5, 1.6, 1.9] );
        assert.deepEqual( arrayProps.integerList() , [1, 2, 3] );
        assert.deepEqual( arrayProps.longList() , [4, 5, 6] );
        assert.deepEqual( arrayProps.stringList() , ["1", "2", "3"] );
    }

}

runTest( PrimitiveListTest )