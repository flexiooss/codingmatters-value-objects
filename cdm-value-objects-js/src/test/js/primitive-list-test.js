import {TestCase} from 'code-altimeter-js'
import "../org/package"
import { globalFlexioImport } from '@flexio-oss/global-import-registry'
import {FlexDate, FlexDateTime, FlexTime, FlexZonedDateTime} from '@flexio-oss/flex-types'

const assert = require('assert')

class PrimitiveListTest extends TestCase {

    testBuilder(){
        let builder = new globalFlexioImport.org.generated.ArraySimplePropsBuilder();
        builder.booleanList( new globalFlexioImport.org.generated.arraysimpleprops.ArraySimplePropsBooleanListList( true, true, false ) );
        builder.dateList( new globalFlexioImport.org.generated.arraysimpleprops.ArraySimplePropsDateListList( new FlexDate( "2019-01-17" ) ) );
        builder.dateTimeList( new globalFlexioImport.org.generated.arraysimpleprops.ArraySimplePropsDateTimeListList( new FlexDateTime( "2019-01-17T14:15:37" ) ) );
        builder.timeList( new globalFlexioImport.org.generated.arraysimpleprops.ArraySimplePropsTimeListList( new FlexTime( "14:15:37" ) ) );
        builder.tzDateTimeList( new globalFlexioImport.org.generated.arraysimpleprops.ArraySimplePropsTzDateTimeListList( new FlexZonedDateTime( "2019-01-17T14:15:37+04:30" ) ) );
        builder.doubleList( new globalFlexioImport.org.generated.arraysimpleprops.ArraySimplePropsDoubleListList( 1.4, 1.7, 1.9 ) );
        builder.floatList( new globalFlexioImport.org.generated.arraysimpleprops.ArraySimplePropsFloatListList( 1.5, 1.6, 1.9 ) );
        builder.integerList( new globalFlexioImport.org.generated.arraysimpleprops.ArraySimplePropsIntegerListList( 1, 2, 3 ) );
        builder.longList( new globalFlexioImport.org.generated.arraysimpleprops.ArraySimplePropsLongListList( 4, 5, 6 ) );
        builder.stringList( new globalFlexioImport.org.generated.arraysimpleprops.ArraySimplePropsStringListList( "1", "2", "3" ) );

        let arrayProps = builder.build();

        assert.deepEqual( arrayProps.booleanList(), [true, true, false] );
        assert.equal( arrayProps.dateList()[0].toJSON(), "2019-01-17" );
        assert.equal( arrayProps.dateTimeList()[0].toJSON(), "2019-01-17T14:15:37" );
        assert.equal( arrayProps.timeList()[0].toJSON(), "14:15:37" );
        assert.equal( arrayProps.tzDateTimeList()[0].toJSON(), "2019-01-17T14:15:37+04:30" );
        assert.deepEqual( arrayProps.doubleList(), [1.4, 1.7, 1.9] );
        assert.deepEqual( arrayProps.floatList(), [1.5, 1.6, 1.9] );
        assert.deepEqual( arrayProps.integerList(), [1, 2, 3] );
        assert.deepEqual( arrayProps.longList(), [4, 5, 6] );
        assert.deepEqual( arrayProps.stringList(), ["1", "2", "3"] );

        // TODO test check types
    }


    testObjectImmutable(){
        let builder = new globalFlexioImport.org.generated.ArraySimplePropsBuilder();
        builder.booleanList( new globalFlexioImport.org.generated.arraysimpleprops.ArraySimplePropsBooleanListList( true, true, false ) );
        let primitiveProp = builder.build()

        assert.throws( () => {
            primitiveProp.booleanList = 12.5
        }, TypeError );
    }

    testObjectFrozen(){
        let builder = new globalFlexioImport.org.generated.ArraySimplePropsBuilder();
        builder.booleanList( new globalFlexioImport.org.generated.arraysimpleprops.ArraySimplePropsBooleanListList( true, true, false ) );

        let primitiveProp = builder.build();

        assert.throws( () => {
            primitiveProp.newProp = 12.5
        }, TypeError );
    }

    testSerialization(){
        let builder = new globalFlexioImport.org.generated.ArraySimplePropsBuilder();
        builder.booleanList( new globalFlexioImport.org.generated.arraysimpleprops.ArraySimplePropsBooleanListList( true, true, false ) );
        builder.dateList( new globalFlexioImport.org.generated.arraysimpleprops.ArraySimplePropsDateListList( new FlexDate( "2019-01-17" ) ) );
        builder.dateTimeList( new globalFlexioImport.org.generated.arraysimpleprops.ArraySimplePropsDateTimeListList( new FlexDateTime( "2019-01-17T14:15:37" ) ) );
        builder.timeList( new globalFlexioImport.org.generated.arraysimpleprops.ArraySimplePropsTimeListList( new FlexTime( "14:15:37" ) ) );
        builder.tzDateTimeList( new globalFlexioImport.org.generated.arraysimpleprops.ArraySimplePropsTzDateTimeListList( new FlexZonedDateTime( "2019-01-17T14:15:37+04:30" ) ) );
        builder.doubleList( new globalFlexioImport.org.generated.arraysimpleprops.ArraySimplePropsDoubleListList( 1.4, 1.7, 1.9 ) );
        builder.floatList( new globalFlexioImport.org.generated.arraysimpleprops.ArraySimplePropsFloatListList( 1.5, 1.6, 1.9 ) );
        builder.integerList( new globalFlexioImport.org.generated.arraysimpleprops.ArraySimplePropsIntegerListList( 1, 2, 3 ) );
        builder.longList( new globalFlexioImport.org.generated.arraysimpleprops.ArraySimplePropsLongListList( 4, 5, 6 ) );
        builder.stringList( new globalFlexioImport.org.generated.arraysimpleprops.ArraySimplePropsStringListList( "1", "2", "3" ) );
        let arrayProps = builder.build();
        assert.equal( JSON.stringify( arrayProps ), "{\"stringList\":[\"1\",\"2\",\"3\"],\"integerList\":[1,2,3],\"longList\":[4,5,6],\"floatList\":[1.5,1.6,1.9],\"doubleList\":[1.4,1.7,1.9],\"booleanList\":[true,true,false],\"dateList\":[\"2019-01-17\"],\"timeList\":[\"14:15:37\"],\"dateTimeList\":[\"2019-01-17T14:15:37\"],\"tzDateTimeList\":[\"2019-01-17T14:15:37+04:30\"]}" );
    }

    testDeserialization(){
        let json = "{\"stringList\":[\"1\",\"2\",\"3\"],\"integerList\":[1,2,3],\"longList\":[4,5,6],\"floatList\":[1.5,1.6,1.9],\"doubleList\":[1.4,1.7,1.9],\"booleanList\":[true,true,false],\"dateList\":[\"2019-01-17\"],\"timeList\":[\"14:15:37\"],\"dateTimeList\":[\"2019-01-17T14:15:37\"],\"tzDateTimeList\":[\"2019-01-17T14:15:37+04:30\"]}"

        let arrayProps = globalFlexioImport.org.generated.ArraySimplePropsBuilder.fromJson( json ).build();
        assert.deepEqual( arrayProps.booleanList(), [true, true, false] );
        assert.equal( arrayProps.dateList()[0].toJSON(), "2019-01-17" );
        assert.equal( arrayProps.dateTimeList()[0].toJSON(), "2019-01-17T14:15:37" );
        assert.equal( arrayProps.timeList()[0].toJSON(), "14:15:37" );
        assert.equal( arrayProps.tzDateTimeList()[0].toJSON(), "2019-01-17T14:15:37+04:30" );
        assert.deepEqual( arrayProps.doubleList(), [1.4, 1.7, 1.9] );
        assert.deepEqual( arrayProps.floatList(), [1.5, 1.6, 1.9] );
        assert.deepEqual( arrayProps.integerList(), [1, 2, 3] );
        assert.deepEqual( arrayProps.longList(), [4, 5, 6] );
        assert.deepEqual( arrayProps.stringList(), ["1", "2", "3"] );
    }

}

runTest( PrimitiveListTest )
