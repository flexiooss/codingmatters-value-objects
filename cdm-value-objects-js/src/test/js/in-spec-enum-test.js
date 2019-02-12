import {TestCase} from 'code-altimeter-js'
const assert = require('assert')
import "../org/package"

class InSpecEnumTest extends TestCase {

    testBuilder() {
        var builder = new window.FLEXIO_IMPORT_OBJECT.org.generated.InSpecEnumPropertiesBuilder();
        builder.single( window.FLEXIO_IMPORT_OBJECT.org.generated.inspecenumproperties.InSpecEnumPropertiesSingle.SA );
        builder.multiple( [window.FLEXIO_IMPORT_OBJECT.org.generated.inspecenumproperties.InSpecEnumPropertiesMultiple.MB, window.FLEXIO_IMPORT_OBJECT.org.generated.inspecenumproperties.InSpecEnumPropertiesMultiple.MC] );
        var inSpecEnum = builder.build();

        assert.equal( inSpecEnum.single().name, "SA" );
        assert.equal( inSpecEnum.multiple()[0].name, "MB" );
        assert.equal( inSpecEnum.multiple()[1].name, "MC" );
        assert.equal( inSpecEnum.multiple().length, 2 );
    }

    testObjectImmutable() {
        var builder = new window.FLEXIO_IMPORT_OBJECT.org.generated.InSpecEnumPropertiesBuilder();
        builder.single( window.FLEXIO_IMPORT_OBJECT.org.generated.inspecenumproperties.InSpecEnumPropertiesSingle.SA );
        builder.multiple( [window.FLEXIO_IMPORT_OBJECT.org.generated.inspecenumproperties.InSpecEnumPropertiesMultiple.MB, window.FLEXIO_IMPORT_OBJECT.org.generated.inspecenumproperties.InSpecEnumPropertiesMultiple.MC] );
        var inSpecEnum = builder.build();

        assert.throws(() => {
            inSpecEnum._single = window.FLEXIO_IMPORT_OBJECT.org.generated.inspecenumproperties.InSpecEnumPropertiesSingle.SB;
        }, TypeError );
    }

    testObjectFrozen() {
        var builder = new window.FLEXIO_IMPORT_OBJECT.org.generated.InSpecEnumPropertiesBuilder();
        builder.single( window.FLEXIO_IMPORT_OBJECT.org.generated.inspecenumproperties.InSpecEnumPropertiesSingle.SA );
        builder.multiple( [window.FLEXIO_IMPORT_OBJECT.org.generated.inspecenumproperties.InSpecEnumPropertiesMultiple.MB, window.FLEXIO_IMPORT_OBJECT.org.generated.inspecenumproperties.InSpecEnumPropertiesMultiple.MC] );
        var inSpecEnum = builder.build();

        assert.throws(() => {
            inSpecEnum.toto = "toto";
        }, TypeError );
    }

    testSerialization() {
        var builder = new window.FLEXIO_IMPORT_OBJECT.org.generated.InSpecEnumPropertiesBuilder();
        builder.single( window.FLEXIO_IMPORT_OBJECT.org.generated.inspecenumproperties.InSpecEnumPropertiesSingle.SA );
        builder.multiple( [window.FLEXIO_IMPORT_OBJECT.org.generated.inspecenumproperties.InSpecEnumPropertiesMultiple.MB, window.FLEXIO_IMPORT_OBJECT.org.generated.inspecenumproperties.InSpecEnumPropertiesMultiple.MC] );
        var inSpecEnum = builder.build();
        assert.equal( JSON.stringify( inSpecEnum ), "{\"single\":\"SA\",\"multiple\":[\"MB\",\"MC\"]}" );
    }

    testDeserialization() {
        var json = "{\"single\":\"SA\",\"multiple\":[\"MB\",\"MC\"]}";
        var inSpecEnum = window.FLEXIO_IMPORT_OBJECT.org.generated.InSpecEnumPropertiesBuilder.fromJson( json ).build();
        assert.equal( inSpecEnum.single().name, "SA" );
        assert.equal( inSpecEnum.multiple()[0].name, "MB" );
        assert.equal( inSpecEnum.multiple()[1].name, "MC" );
        assert.equal( inSpecEnum.multiple().length, 2 );
    }
}

runTest( InSpecEnumTest );