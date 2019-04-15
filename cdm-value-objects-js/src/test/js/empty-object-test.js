import {TestCase} from 'code-altimeter-js'
const assert = require('assert')
import "../org/package"
import {FLEXIO_IMPORT_OBJECT } from 'flexio-jshelpers'

class EmptyObjectTest extends TestCase {

    testInitialization() {
        var myEmptyObject = new window[FLEXIO_IMPORT_OBJECT].org.generated.EmptyObject();
        assert.notEqual( myEmptyObject, null );
    }

    testSerialization() {
        var myEmptyObject = new window[FLEXIO_IMPORT_OBJECT].org.generated.EmptyObject();
        assert.equal( JSON.stringify(myEmptyObject),  "{}" );
    }

    testDeserialization() {
        var myEmptyObject = window[FLEXIO_IMPORT_OBJECT].org.generated.EmptyObjectBuilder.fromJson("{}");
        assert.notEqual( myEmptyObject, null );
        assert.equal( JSON.stringify(myEmptyObject), "{}" );
    }

    testObjectImmutable() {
        var myEmptyObject = new window[FLEXIO_IMPORT_OBJECT].org.generated.EmptyObject();
        assert.throws(() => {
            myEmptyObject.floatProp = 12.5
        }, TypeError);
    }

}

runTest( EmptyObjectTest );
