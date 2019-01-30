import "../org/package"

test( 'test builder', () => {
    var prop1 = window.FLEXIO_IMPORT_OBJECT.org.generated.PrimitivePropsBuilder.fromJson(
        "{\"stringProp\":\"str1\",\"bytesProp\":\"bytes\",\"integerProp\":9,\"longProp\":7,\"floatProp\":9.7,\"doubleProp\":7.9,\"booleanProp\":true,\"date-prop\":\"2019-01-09\",\"timeProp\":\"14:17:32\",\"dateTimeProp\":\"2019-01-09T14:17:32\",\"tzDateTimeProp\":\"2019-01-09T14:17:32-03:00\"}"
    );
    var prop2 = window.FLEXIO_IMPORT_OBJECT.org.generated.PrimitivePropsBuilder.fromJson(
        "{\"stringProp\":\"str2\",\"bytesProp\":\"bytes\",\"integerProp\":9,\"longProp\":7,\"floatProp\":9.7,\"doubleProp\":7.9,\"booleanProp\":true,\"date-prop\":\"2019-01-09\",\"timeProp\":\"14:17:32\",\"dateTimeProp\":\"2019-01-09T14:17:32\",\"tzDateTimeProp\":\"2019-01-09T14:17:32-03:00\"}"
    );
    var prop3 = window.FLEXIO_IMPORT_OBJECT.org.generated.PrimitivePropsBuilder.fromJson(
        "{\"stringProp\":\"str3\",\"bytesProp\":\"bytes\",\"integerProp\":9,\"longProp\":7,\"floatProp\":9.7,\"doubleProp\":7.9,\"booleanProp\":true,\"date-prop\":\"2019-01-09\",\"timeProp\":\"14:17:32\",\"dateTimeProp\":\"2019-01-09T14:17:32\",\"tzDateTimeProp\":\"2019-01-09T14:17:32-03:00\"}"
    );

    var builder = new window.FLEXIO_IMPORT_OBJECT.org.generated.ValueObjectPropsBuilder();
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
