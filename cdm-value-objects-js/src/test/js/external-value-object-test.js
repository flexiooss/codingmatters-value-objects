import {TestCase} from '@flexio-oss/code-altimeter-js'
import '../org/package'
import {globalFlexioImport} from '@flexio-oss/js-commons-bundle/global-import-registry'

const assert = require('assert')

class ExternalValueObjectTest extends TestCase {

  getTestObject() {
    let prop1 = globalFlexioImport.org.generated.PrimitivePropsBuilder.fromJson(
      '{"stringProp":"str1","integerProp":9,"longProp":7,"floatProp":9.7,"doubleProp":7.9,"booleanProp":true,"date-prop":"2019-01-09","timeProp":"14:17:32","dateTimeProp":"2019-01-09T14:17:32","tzDateTimeProp":"2019-01-09T14:17:32-03:00"}'
    ).build()
    let prop2 = globalFlexioImport.org.generated.PrimitivePropsBuilder.fromJson(
      '{"stringProp":"str2","integerProp":9,"longProp":7,"floatProp":9.7,"doubleProp":7.9,"booleanProp":true,"date-prop":"2019-01-09","timeProp":"14:17:32","dateTimeProp":"2019-01-09T14:17:32","tzDateTimeProp":"2019-01-09T14:17:32-03:00"}'
    ).build()
    let prop3 = globalFlexioImport.org.generated.PrimitivePropsBuilder.fromJson(
      '{"stringProp":"str3","integerProp":9,"longProp":7,"floatProp":9.7,"doubleProp":7.9,"booleanProp":true,"date-prop":"2019-01-09","timeProp":"14:17:32","dateTimeProp":"2019-01-09T14:17:32","tzDateTimeProp":"2019-01-09T14:17:32-03:00"}'
    ).build()

    let builder = new globalFlexioImport.org.generated.ValueObjectPropsBuilder()
    builder.prop(prop1)
    builder.propList(new globalFlexioImport.org.generated.PrimitivePropsList(prop2, prop3))
    return builder.build()
  }

  testBuilder() {
    let object = this.getTestObject()
    assert.strictEqual(object.prop().stringProp(), 'str1')
    assert.strictEqual(object.propList()[0].stringProp(), 'str2')
    assert.strictEqual(object.propList()[1].stringProp(), 'str3')

    assert.strictEqual(object.prop().dateProp().toJSON(), '2019-01-09')
    assert.strictEqual(object.propList()[0].dateProp().toJSON(), '2019-01-09')

  }

  testObjectImmutable() {
    let object = this.getTestObject()
    assert.throws(() => {
      object._prop = 'toto'
    }, TypeError)

    assert.throws(() => {
      object._prop._stringProp = 'toto'
    }, TypeError)
  }

  testObjectFrozen() {
    let object = this.getTestObject()
    assert.throws(() => {
      object.toto = 'toto'
    }, TypeError)
  }

  testSerialization() {
    let object = this.getTestObject()
    assert.strictEqual(JSON.stringify(object), '{"prop":{"stringProp":"str1","integerProp":9,"longProp":7,"floatProp":9.7,"doubleProp":7.9,"booleanProp":true,"date-prop":"2019-01-09","timeProp":"14:17:32","dateTimeProp":"2019-01-09T14:17:32","tzDateTimeProp":"2019-01-09T14:17:32-03:00"},"propList":[{"stringProp":"str2","integerProp":9,"longProp":7,"floatProp":9.7,"doubleProp":7.9,"booleanProp":true,"date-prop":"2019-01-09","timeProp":"14:17:32","dateTimeProp":"2019-01-09T14:17:32","tzDateTimeProp":"2019-01-09T14:17:32-03:00"},{"stringProp":"str3","integerProp":9,"longProp":7,"floatProp":9.7,"doubleProp":7.9,"booleanProp":true,"date-prop":"2019-01-09","timeProp":"14:17:32","dateTimeProp":"2019-01-09T14:17:32","tzDateTimeProp":"2019-01-09T14:17:32-03:00"}]}')
  }

  testDeserialization() {
    let json = '{"prop":{"stringProp":"str1","integerProp":9,"longProp":7,"floatProp":9.7,"doubleProp":7.9,"booleanProp":true,"date-prop":"2019-01-09","timeProp":"14:17:32","dateTimeProp":"2019-01-09T14:17:32","tzDateTimeProp":"2019-01-09T14:17:32-03:00"},"propList":[{"stringProp":"str2","integerProp":9,"longProp":7,"floatProp":9.7,"doubleProp":7.9,"booleanProp":true,"date-prop":"2019-01-09","timeProp":"14:17:32","dateTimeProp":"2019-01-09T14:17:32","tzDateTimeProp":"2019-01-09T14:17:32-03:00"},{"stringProp":"str3","integerProp":9,"longProp":7,"floatProp":9.7,"doubleProp":7.9,"booleanProp":true,"date-prop":"2019-01-09","timeProp":"14:17:32","dateTimeProp":"2019-01-09T14:17:32","tzDateTimeProp":"2019-01-09T14:17:32-03:00"}]}'
    let object = globalFlexioImport.org.generated.ValueObjectPropsBuilder.fromJson(json).build()
    assert.strictEqual(object.prop().stringProp(), 'str1')
    assert.strictEqual(object.propList()[0].stringProp(), 'str2')
    assert.strictEqual(object.propList()[1].stringProp(), 'str3')
    assert.strictEqual(object.prop().dateProp().toJSON(), '2019-01-09')
    assert.strictEqual(object.propList()[0].dateProp().toJSON(), '2019-01-09')
  }

}

runTest(ExternalValueObjectTest)
