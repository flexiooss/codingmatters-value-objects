import {TestCase} from 'code-altimeter-js'
import "../org/package"
import { globalFlexioImport } from '@flexio-oss/global-import-registry'

const assert = require('assert')

class EmptyObjectTest extends TestCase {

    testInitialization() {
        let myEmptyObject = new globalFlexioImport.org.generated.EmptyObject();
        assert.notEqual( myEmptyObject, null );
    }

    testSerialization() {
        let myEmptyObject = new globalFlexioImport.org.generated.EmptyObject();
        assert.equal( JSON.stringify(myEmptyObject),  "{}" );
    }

    testDeserialization() {
        let myEmptyObject = globalFlexioImport.org.generated.EmptyObjectBuilder.fromJson("{}");
        assert.notEqual( myEmptyObject, null );
        assert.equal( JSON.stringify(myEmptyObject), "{}" );
    }

    testObjectImmutable() {
        let myEmptyObject = new globalFlexioImport.org.generated.EmptyObject();
        assert.throws(() => {
            myEmptyObject.floatProp = 12.5
        }, TypeError);
    }

}

runTest( EmptyObjectTest );
