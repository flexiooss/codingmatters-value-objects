import {TestCase} from 'code-altimeter-js'
import "../org/package"
import { globalFlexioImport } from '@flexio-oss/global-import-registry'
import {FlexDate, FlexDateTime, FlexTime, FlexZonedDateTime} from '@flexio-oss/flex-types'

const assert = require('assert')

class PrimitivePropsTest extends TestCase {

    testBuilder() {
        let builder = new globalFlexioImport.org.generated.PrimitivePropsBuilder();
        builder.stringProp( "str" );
        builder.bytesProp( "bytes" );
        builder.integerProp( 9 );
        builder.longProp( 7 );
        builder.floatProp( 9.7 );
        builder.doubleProp( 7.9 );
        builder.booleanProp( true );

        let primitiveProp = builder.build();

        assert.equal( primitiveProp.stringProp(), "str" );
        assert.equal( primitiveProp.bytesProp(), "bytes" );
        assert.equal( primitiveProp.integerProp(), 9 );
        assert.equal( primitiveProp.longProp(), 7 );
        assert.equal( primitiveProp.floatProp(), 9.7 );
        assert.equal( primitiveProp.doubleProp(), 7.9 );
        assert.equal( primitiveProp.booleanProp(), true );
    }

    testWithMethod(){
        let builder = new globalFlexioImport.org.generated.PrimitivePropsBuilder();
        builder.stringProp( "str" );
        builder.bytesProp( "bytes" );
        builder.integerProp( 9 );
        builder.longProp( 7 );
        builder.floatProp( 9.7 );
        builder.doubleProp( 7.9 );
        builder.booleanProp( true );
        let primitiveProp = builder.build();

        assert.equal( primitiveProp.stringProp(), "str" );
        let primitiveProp2 = primitiveProp.withStringProp( "str2" );
        assert.equal( primitiveProp.stringProp(), "str" );
        assert.equal( primitiveProp2.stringProp(), "str2" );
    }

    testObjectImmutable() {
        let builder = new globalFlexioImport.org.generated.PrimitivePropsBuilder();
        builder.stringProp( "str" )

        let primitiveProp = builder.build()

        assert.throws(() => {
            primitiveProp.floatProp = 12.5
        }, TypeError );
    }

    testObjectFrozen() {
        let builder = new globalFlexioImport.org.generated.PrimitivePropsBuilder();
        builder.stringProp( "str" )

        let primitiveProp = builder.build()

        assert.throws(() => {
            primitiveProp.newProp = 12.5
        }, TypeError );
    }

    testSerialization() {
        let builder = new globalFlexioImport.org.generated.PrimitivePropsBuilder();
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
        let primitiveProp = builder.build();
        assert.equal( JSON.stringify( primitiveProp ), "{\"stringProp\":\"str\",\"bytesProp\":\"bytes\",\"integerProp\":9,\"longProp\":7,\"floatProp\":9.7,\"doubleProp\":7.9,\"booleanProp\":true,\"date-prop\":\"2019-01-09\",\"timeProp\":\"14:17:32\",\"dateTimeProp\":\"2019-01-09T14:17:32\",\"tzDateTimeProp\":\"2019-01-09T14:17:32-03:00\"}" );
    }

    testDeserialization() {
        let json = "{\"stringProp\":\"str\",\"bytesProp\":\"bytes\",\"integerProp\":9,\"longProp\":7,\"floatProp\":9.7,\"doubleProp\":7.9,\"booleanProp\":true,\"date-prop\":\"2019-01-09\",\"timeProp\":\"14:17:32\",\"dateTimeProp\":\"2019-01-09T14:17:32\",\"tzDateTimeProp\":\"2019-01-09T14:17:32-03:00\"}";
        let primitiveProp = globalFlexioImport.org.generated.PrimitivePropsBuilder.fromJson( json ).build();
        assert.equal( primitiveProp.stringProp() , "str" );
        assert.equal( primitiveProp.bytesProp() , "bytes" );
        assert.equal( primitiveProp.integerProp() , 9 );
        assert.equal( primitiveProp.longProp() , 7 );
        assert.equal( primitiveProp.floatProp() , 9.7 );
        assert.equal( primitiveProp.doubleProp() , 7.9 );
        assert.equal( primitiveProp.booleanProp() , true );
        assert.equal( primitiveProp.dateProp().toJSON() ,"2019-01-09" );
        assert.equal( primitiveProp.timeProp().toJSON() , "14:17:32" );
        assert.equal( primitiveProp.dateTimeProp().toJSON() , "2019-01-09T14:17:32" );
        assert.equal( primitiveProp.tzDateTimeProp().toJSON() ,  "2019-01-09T14:17:32-03:00" );
        assert.equal( typeof(primitiveProp.tzDateTimeProp() ), "object" );
        assert.equal( typeof(primitiveProp.dateTimeProp() ), "object" );
        assert.equal( typeof(primitiveProp.dateProp() ), "object" );
        assert.equal( typeof(primitiveProp.timeProp() ), "object" );
    }

}

runTest( PrimitivePropsTest );
