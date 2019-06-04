import {TestCase} from 'code-altimeter-js'
import "../org/package"
import { globalFlexioImport } from '@flexio-oss/global-import-registry'

const assert = require('assert')

class ComplexType extends TestCase {

    testBuilder() {
        let propBuilder = new globalFlexioImport.org.generated.complextype.ComplexPropsBuilder();
        propBuilder.stringProp( "toto" );
        propBuilder.intList( new globalFlexioImport.org.generated.complextype.complexprops.ComplexPropsIntListList( 4, 7, 5 ));
        let prop = propBuilder.build();

        let builder = new globalFlexioImport.org.generated.ComplexTypeBuilder();
        builder.complexProps( prop );
        builder.foo( 1 );
        builder.testIsOk( true );
        let complexType = builder.build();

        assert.strictEqual( complexType.testIsOk(), true );
        assert.strictEqual( complexType.foo(), 1 );
        assert.strictEqual( complexType.complexProps().stringProp(), "toto" );
        assert.strictDeepEqual( complexType.complexProps().intList(), [4, 7, 5] );
        console.log("plokoko")
    }

    testObjectImmutable() {
        let propBuilder = new globalFlexioImport.org.generated.complextype.ComplexPropsBuilder();
        propBuilder.stringProp( "toto" );
        propBuilder.intList( new globalFlexioImport.org.generated.complextype.complexprops.ComplexPropsIntListList( 4, 7, 5 ));
        let prop = propBuilder.build();

        let builder = new globalFlexioImport.org.generated.ComplexTypeBuilder();
        builder.complexProps( prop );
        builder.foo( 1 );
        builder.testIsOk( true );
        let complexType = builder.build();

        assert.throws(() => {
            complexType.complexProps = "bar"
        }, TypeError );

        assert.throws(() => {
            complexType.complexProps()._stringProp = "yoyo"
        }, TypeError );
    }

    testObjectFrozen() {
        let propBuilder = new globalFlexioImport.org.generated.complextype.ComplexPropsBuilder();
        propBuilder.stringProp( "toto" );
        propBuilder.intList( new globalFlexioImport.org.generated.complextype.complexprops.ComplexPropsIntListList( 4, 7, 5 ));
        let prop = propBuilder.build();

        let builder = new globalFlexioImport.org.generated.ComplexTypeBuilder();
        builder.complexProps( prop );
        builder.foo( 1 );
        builder.testIsOk( true );
        let complexType = builder.build();

        assert.throws(() => {
            complexType.toto = 12.5
        }, TypeError );

        assert.throws(() => {
            complexType.complexProps().toto = 12.5
        }, TypeError );
    }

    testSerialization() {
        let propBuilder = new globalFlexioImport.org.generated.complextype.ComplexPropsBuilder();
        propBuilder.stringProp( "toto" );
        propBuilder.intList( new globalFlexioImport.org.generated.complextype.complexprops.ComplexPropsIntListList( 4, 7, 5 ));

        let prop = propBuilder.build();

        let builder = new globalFlexioImport.org.generated.ComplexTypeBuilder();
        builder.complexProps( prop );
        builder.foo( 1 );
        builder.testIsOk( true );
        let complexType = builder.build();

        assert.equal( JSON.stringify( prop ), "{\"string-prop\":\"toto\",\"intList\":[4,7,5]}" );
        assert.equal( JSON.stringify( complexType ), "{\"complexProps\":{\"string-prop\":\"toto\",\"intList\":[4,7,5]},\"test-is-ok\":true,\"foo\":1}" );
    }

    testDeserialization() {
        let json = "{\"complexProps\":{\"string-prop\":\"toto\",\"intList\":[4,7,5]},\"test-is-ok\":true,\"foo\":2.1}";
        let complexType = globalFlexioImport.org.generated.ComplexTypeBuilder.fromJson( json ).build();
        assert.equal( complexType.testIsOk(), true );
        assert.equal( complexType.foo(), 2.1 );
        assert.equal( complexType.complexProps().stringProp(), "toto" );
        assert.deepEqual( complexType.complexProps().intList(), [4, 7, 5] );
    }

}

runTest( ComplexType );
