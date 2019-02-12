import {TestCase} from 'code-altimeter-js';
const assert = require('assert');
import "../org/package";

class ExternalValueObjectTest extends TestCase {

    getTestObject(){
        var prop1 = window.FLEXIO_IMPORT_OBJECT.org.generated.PrimitivePropsBuilder.fromJson(
            "{\"stringProp\":\"str1\",\"bytesProp\":\"bytes\",\"integerProp\":9,\"longProp\":7,\"floatProp\":9.7,\"doubleProp\":7.9,\"booleanProp\":true,\"date-prop\":\"2019-01-09\",\"timeProp\":\"14:17:32\",\"dateTimeProp\":\"2019-01-09T14:17:32\",\"tzDateTimeProp\":\"2019-01-09T14:17:32-03:00\"}"
        ).build();
        var prop2 = window.FLEXIO_IMPORT_OBJECT.org.generated.PrimitivePropsBuilder.fromJson(
            "{\"stringProp\":\"str2\",\"bytesProp\":\"bytes\",\"integerProp\":9,\"longProp\":7,\"floatProp\":9.7,\"doubleProp\":7.9,\"booleanProp\":true,\"date-prop\":\"2019-01-09\",\"timeProp\":\"14:17:32\",\"dateTimeProp\":\"2019-01-09T14:17:32\",\"tzDateTimeProp\":\"2019-01-09T14:17:32-03:00\"}"
        ).build();
        var prop3 = window.FLEXIO_IMPORT_OBJECT.org.generated.PrimitivePropsBuilder.fromJson(
            "{\"stringProp\":\"str3\",\"bytesProp\":\"bytes\",\"integerProp\":9,\"longProp\":7,\"floatProp\":9.7,\"doubleProp\":7.9,\"booleanProp\":true,\"date-prop\":\"2019-01-09\",\"timeProp\":\"14:17:32\",\"dateTimeProp\":\"2019-01-09T14:17:32\",\"tzDateTimeProp\":\"2019-01-09T14:17:32-03:00\"}"
        ).build();

        var builder = new window.FLEXIO_IMPORT_OBJECT.org.generated.ValueObjectPropsBuilder();
        builder.prop( prop1 );
        builder.propList( [prop2, prop3] );
        return builder.build();
    }

    testBuilder() {
        var object = this.getTestObject();
        assert.strictEqual( object.prop().stringProp(), "str1" );
        assert.strictEqual( object.propList()[0].stringProp(), "str2" );
        assert.strictEqual( object.propList()[1].stringProp(), "str3" );

        assert.strictEqual( object.prop().dateProp().toJSON(), "2019-01-09" );
        assert.strictEqual( object.propList()[0].dateProp().toJSON(), "2019-01-09" );

    }

    testObjectImmutable() {
        var object = this.getTestObject();
        assert.throws(() => {
            object._prop = "toto";
        }, TypeError );

        assert.throws(() => {
            object._prop._stringProp = "toto";
        }, TypeError );
    }

    testObjectFrozen() {
        var object = this.getTestObject();
        assert.throws(() => {
            object.toto = "toto"
        }, TypeError );
    }

    testSerialization() {
        var object = this.getTestObject();
        assert.equal( JSON.stringify( object ), '{"prop":{"stringProp":"str1","bytesProp":"bytes","integerProp":9,"longProp":7,"floatProp":9.7,"doubleProp":7.9,"booleanProp":true,"date-prop":"2019-01-09","timeProp":"14:17:32","dateTimeProp":"2019-01-09T14:17:32","tzDateTimeProp":"2019-01-09T14:17:32-03:00"},"propList":[{"stringProp":"str2","bytesProp":"bytes","integerProp":9,"longProp":7,"floatProp":9.7,"doubleProp":7.9,"booleanProp":true,"date-prop":"2019-01-09","timeProp":"14:17:32","dateTimeProp":"2019-01-09T14:17:32","tzDateTimeProp":"2019-01-09T14:17:32-03:00"},{"stringProp":"str3","bytesProp":"bytes","integerProp":9,"longProp":7,"floatProp":9.7,"doubleProp":7.9,"booleanProp":true,"date-prop":"2019-01-09","timeProp":"14:17:32","dateTimeProp":"2019-01-09T14:17:32","tzDateTimeProp":"2019-01-09T14:17:32-03:00"}]}' );
    }

    testDeserialization() {
        var json = '{"prop":{"stringProp":"str1","bytesProp":"bytes","integerProp":9,"longProp":7,"floatProp":9.7,"doubleProp":7.9,"booleanProp":true,"date-prop":"2019-01-09","timeProp":"14:17:32","dateTimeProp":"2019-01-09T14:17:32","tzDateTimeProp":"2019-01-09T14:17:32-03:00"},"propList":[{"stringProp":"str2","bytesProp":"bytes","integerProp":9,"longProp":7,"floatProp":9.7,"doubleProp":7.9,"booleanProp":true,"date-prop":"2019-01-09","timeProp":"14:17:32","dateTimeProp":"2019-01-09T14:17:32","tzDateTimeProp":"2019-01-09T14:17:32-03:00"},{"stringProp":"str3","bytesProp":"bytes","integerProp":9,"longProp":7,"floatProp":9.7,"doubleProp":7.9,"booleanProp":true,"date-prop":"2019-01-09","timeProp":"14:17:32","dateTimeProp":"2019-01-09T14:17:32","tzDateTimeProp":"2019-01-09T14:17:32-03:00"}]}';
        var object = window.FLEXIO_IMPORT_OBJECT.org.generated.ValueObjectPropsBuilder.fromJson( json ).build();
        assert.strictEqual( object.prop().stringProp(), "str1" );
        assert.strictEqual( object.propList()[0].stringProp(), "str2" );
        assert.strictEqual( object.propList()[1].stringProp(), "str3" );
        assert.strictEqual( object.prop().dateProp().toJSON(), "2019-01-09" );
        assert.strictEqual( object.propList()[0].dateProp().toJSON(), "2019-01-09" );
    }

}

runTest( ExternalValueObjectTest );