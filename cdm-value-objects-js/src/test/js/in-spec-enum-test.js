import {TestCase} from 'code-altimeter-js'
import "../org/package"
import { globalFlexioImport } from '@flexio-oss/global-import-registry'

const assert = require('assert')

class InSpecEnumTest extends TestCase {

    testBuilder(){
        let builder = new globalFlexioImport.org.generated.InSpecEnumPropertiesBuilder();
        builder.single( globalFlexioImport.org.generated.inspecenumproperties.InSpecEnumPropertiesSingle.SA );
        builder.multiple( new globalFlexioImport.org.generated.inspecenumproperties.InSpecEnumPropertiesMultipleList(
            globalFlexioImport.org.generated.inspecenumproperties.InSpecEnumPropertiesMultiple.MB,
            globalFlexioImport.org.generated.inspecenumproperties.InSpecEnumPropertiesMultiple.MC
            )
        );
        let inSpecEnum = builder.build();

        assert.strictEqual( inSpecEnum.single().name, "SA" );
        assert.strictEqual( inSpecEnum.multiple()[0].name, "MB" );
        assert.strictEqual( inSpecEnum.multiple()[1].name, "MC" );
        assert.strictEqual( inSpecEnum.multiple().length, 2 );
    }

    testObjectImmutable(){
        let builder = new globalFlexioImport.org.generated.InSpecEnumPropertiesBuilder();
        builder.single( globalFlexioImport.org.generated.inspecenumproperties.InSpecEnumPropertiesSingle.SA );
        builder.multiple( new globalFlexioImport.org.generated.inspecenumproperties.InSpecEnumPropertiesMultipleList(
            globalFlexioImport.org.generated.inspecenumproperties.InSpecEnumPropertiesMultiple.MB,
            globalFlexioImport.org.generated.inspecenumproperties.InSpecEnumPropertiesMultiple.MC
            )
        );
        let inSpecEnum = builder.build();

        assert.throws( () => {
            inSpecEnum._single = globalFlexioImport.org.generated.inspecenumproperties.InSpecEnumPropertiesSingle.SB;
        }, TypeError );
    }

    testObjectFrozen(){
        let builder = new globalFlexioImport.org.generated.InSpecEnumPropertiesBuilder();
        builder.single( globalFlexioImport.org.generated.inspecenumproperties.InSpecEnumPropertiesSingle.SA );
        builder.multiple( new globalFlexioImport.org.generated.inspecenumproperties.InSpecEnumPropertiesMultipleList(
            globalFlexioImport.org.generated.inspecenumproperties.InSpecEnumPropertiesMultiple.MB,
            globalFlexioImport.org.generated.inspecenumproperties.InSpecEnumPropertiesMultiple.MC
            )
        );
                let inSpecEnum = builder.build();

        assert.throws( () => {
            inSpecEnum.toto = "toto";
        }, TypeError );
    }

    testSerialization(){
        let builder = new globalFlexioImport.org.generated.InSpecEnumPropertiesBuilder();
        builder.single( globalFlexioImport.org.generated.inspecenumproperties.InSpecEnumPropertiesSingle.SA );
        builder.multiple( new globalFlexioImport.org.generated.inspecenumproperties.InSpecEnumPropertiesMultipleList(
                    globalFlexioImport.org.generated.inspecenumproperties.InSpecEnumPropertiesMultiple.MB,
                    globalFlexioImport.org.generated.inspecenumproperties.InSpecEnumPropertiesMultiple.MC
                    )
                );
        let inSpecEnum = builder.build();
        assert.strictEqual( JSON.stringify( inSpecEnum ), "{\"single\":\"SA\",\"multiple\":[\"MB\",\"MC\"]}" );
    }

    testDeserialization(){
        let json = "{\"single\":\"SA\",\"multiple\":[\"MB\",\"MC\"]}";
        let inSpecEnum = globalFlexioImport.org.generated.InSpecEnumPropertiesBuilder.fromJson( json ).build();
        assert.strictEqual( inSpecEnum.single().name, "SA" );
        assert.strictEqual( inSpecEnum.multiple()[0].name, "MB" );
        assert.strictEqual( inSpecEnum.multiple()[1].name, "MC" );
        assert.strictEqual( inSpecEnum.multiple().length, 2 );
    }
}

runTest( InSpecEnumTest );
