import {TestCase} from 'code-altimeter-js'
const assert = require('assert')
import "../org/package"

class ComplexTypeTest extends TestCase {

    testBuilder() {
        var propBuilder = new window.FLEXIO_IMPORT_OBJECT.org.generated.complextype.ComplexPropsBuilder();
        propBuilder.stringProp( "toto" );
        propBuilder.intList( [4, 7, 5] );
        var prop = propBuilder.build();

        var builder = new window.FLEXIO_IMPORT_OBJECT.org.generated.ComplexTypeBuilder();
        builder.complexProps( prop );
        builder.foo( "foo" );
        builder.testIsOk( true );
        var complexType = builder.build();

        assert.equal( complexType.testIsOk(), true );
        assert.equal( complexType.foo(), "foo" );
        assert.equal( complexType.complexProps().stringProp(), "toto" );
        assert.deepEqual( complexType.complexProps().intList(), [4, 7, 5] );
    }

    testObjectImmutable() {
        var propBuilder = new window.FLEXIO_IMPORT_OBJECT.org.generated.complextype.ComplexPropsBuilder();
        propBuilder.stringProp( "toto" );
        propBuilder.intList( [4, 7, 5] );
        var prop = propBuilder.build();

        var builder = new window.FLEXIO_IMPORT_OBJECT.org.generated.ComplexTypeBuilder();
        builder.complexProps( prop );
        builder.foo( "foo" );
        builder.testIsOk( true );
        var complexType = builder.build();

        assert.throws(() => {
            complexType.complexProps = "bar"
        }, TypeError );

        assert.throws(() => {
            complexType.complexProps()._stringProp = "yoyo"
        }, TypeError );
    }

    testObjectFrozen() {
        var propBuilder = new window.FLEXIO_IMPORT_OBJECT.org.generated.complextype.ComplexPropsBuilder();
        propBuilder.stringProp( "toto" );
        propBuilder.intList( [4, 7, 5] );
        var prop = propBuilder.build();

        var builder = new window.FLEXIO_IMPORT_OBJECT.org.generated.ComplexTypeBuilder();
        builder.complexProps( prop );
        builder.foo( "foo" );
        builder.testIsOk( true );
        var complexType = builder.build();

        assert.throws(() => {
            complexType.toto = 12.5
        }, TypeError );

        assert.throws(() => {
            complexType.complexProps().toto = 12.5
        }, TypeError );
    }

    testSerialization() {
        var propBuilder = new window.FLEXIO_IMPORT_OBJECT.org.generated.complextype.ComplexPropsBuilder();
        propBuilder.stringProp( "toto" );
        propBuilder.intList( [4, 7, 5] );
        var prop = propBuilder.build();

        var builder = new window.FLEXIO_IMPORT_OBJECT.org.generated.ComplexTypeBuilder();
        builder.complexProps( prop );
        builder.foo( "foo" );
        builder.testIsOk( true );
        var complexType = builder.build();

        assert.equal( JSON.stringify( prop ), "{\"string-prop\":\"toto\",\"intList\":[4,7,5]}" );
        assert.equal( JSON.stringify( complexType ), "{\"complexProps\":{\"string-prop\":\"toto\",\"intList\":[4,7,5]},\"test-is-ok\":true,\"foo\":\"foo\"}" );
    }

    testDeserialization() {
        var json = "{\"complexProps\":{\"string-prop\":\"toto\",\"intList\":[4,7,5]},\"test-is-ok\":true,\"foo\":2.1}";
        var complexType = window.FLEXIO_IMPORT_OBJECT.org.generated.ComplexTypeBuilder.fromJson( json ).build();
        assert.equal( complexType.testIsOk(), true );
        assert.equal( complexType.foo(), 2.1 );
        assert.equal( complexType.complexProps().stringProp(), "toto" );
        assert.deepEqual( complexType.complexProps().intList(), [4, 7, 5] );
    }

}

runTest( ComplexTypeTest );
