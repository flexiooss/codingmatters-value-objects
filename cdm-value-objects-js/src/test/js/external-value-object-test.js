import {ValueObjectProps, ValueObjectPropsBuilder} from '../org/generated/ValueObjectProps'
import {PrimitiveProps, PrimitivePropsBuilder} from '../org/generated/PrimitiveProps'

test( 'test builder', () => {
    var prop1 = PrimitivePropsBuilder.fromJson(
        "{\"stringProp\":\"str1\",\"bytesProp\":\"bytes\",\"integerProp\":9,\"longProp\":7,\"floatProp\":9.7,\"doubleProp\":7.9,\"booleanProp\":true,\"date-prop\":\"2019-01-09\",\"timeProp\":\"14:17:32\",\"dateTimeProp\":\"2019-01-09T14:17:32\",\"tzDateTimeProp\":\"2019-01-09T14:17:32-03:00\"}"
    );
    var prop2 = PrimitivePropsBuilder.fromJson(
        "{\"stringProp\":\"str2\",\"bytesProp\":\"bytes\",\"integerProp\":9,\"longProp\":7,\"floatProp\":9.7,\"doubleProp\":7.9,\"booleanProp\":true,\"date-prop\":\"2019-01-09\",\"timeProp\":\"14:17:32\",\"dateTimeProp\":\"2019-01-09T14:17:32\",\"tzDateTimeProp\":\"2019-01-09T14:17:32-03:00\"}"
    );
    var prop3 = PrimitivePropsBuilder.fromJson(
        "{\"stringProp\":\"str3\",\"bytesProp\":\"bytes\",\"integerProp\":9,\"longProp\":7,\"floatProp\":9.7,\"doubleProp\":7.9,\"booleanProp\":true,\"date-prop\":\"2019-01-09\",\"timeProp\":\"14:17:32\",\"dateTimeProp\":\"2019-01-09T14:17:32\",\"tzDateTimeProp\":\"2019-01-09T14:17:32-03:00\"}"
    );

    var builder = new ValueObjectPropsBuilder();
    builder.prop( prop1 )
    builder.propList( [prop2, prop3] );


} );


test( 'assert object immutable', () => {

} );


test( 'assert object frozen', () => {

} );


test( 'test serialization', () => {

} );


test( 'test deserialization', () => {

} );
