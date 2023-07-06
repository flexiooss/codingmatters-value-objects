import {TestCase} from '@flexio-oss/code-altimeter-js'
import '../org/package'
import {globalFlexioImport} from '@flexio-oss/js-commons-bundle/global-import-registry'

const assert = require('assert')

class InSpecEnumTest extends TestCase {

  testBuilder() {
    let builder = new globalFlexioImport.org.generated.InSpecEnumPropertiesBuilder()
    builder.single(globalFlexioImport.org.generated.inspecenumproperties.Single.SA)
    builder.multiple(new globalFlexioImport.org.generated.inspecenumproperties.MultipleList(
      globalFlexioImport.org.generated.inspecenumproperties.Multiple.MB,
      globalFlexioImport.org.generated.inspecenumproperties.Multiple.MC
      )
    )
    let inSpecEnum = builder.build()

    assert.strictEqual(inSpecEnum.single().name(), 'SA')
    assert.strictEqual(inSpecEnum.multiple()[0].name(), 'MB')
    assert.strictEqual(inSpecEnum.multiple()[1].name(), 'MC')
    assert.strictEqual(inSpecEnum.multiple().length, 2)
  }

  testObjectImmutable() {
    let builder = new globalFlexioImport.org.generated.InSpecEnumPropertiesBuilder()
    builder.single(globalFlexioImport.org.generated.inspecenumproperties.Single.SA)
    builder.multiple(new globalFlexioImport.org.generated.inspecenumproperties.MultipleList(
      globalFlexioImport.org.generated.inspecenumproperties.Multiple.MB,
      globalFlexioImport.org.generated.inspecenumproperties.Multiple.MC
      )
    )
    let inSpecEnum = builder.build()

    assert.throws(() => {
      inSpecEnum._single = globalFlexioImport.org.generated.inspecenumproperties.Single.SB
    }, TypeError)
  }

  testObjectFrozen() {
    let builder = new globalFlexioImport.org.generated.InSpecEnumPropertiesBuilder()
    builder.single(globalFlexioImport.org.generated.inspecenumproperties.Single.SA)
    builder.multiple(new globalFlexioImport.org.generated.inspecenumproperties.MultipleList(
      globalFlexioImport.org.generated.inspecenumproperties.Multiple.MB,
      globalFlexioImport.org.generated.inspecenumproperties.Multiple.MC
      )
    )
    let inSpecEnum = builder.build()

    assert.throws(() => {
      inSpecEnum.toto = 'toto'
    }, TypeError)
  }

  testSerialization() {
    let builder = new globalFlexioImport.org.generated.InSpecEnumPropertiesBuilder()
    builder.single(globalFlexioImport.org.generated.inspecenumproperties.Single.SA)
    builder.multiple(new globalFlexioImport.org.generated.inspecenumproperties.MultipleList(
      globalFlexioImport.org.generated.inspecenumproperties.Multiple.MB,
      globalFlexioImport.org.generated.inspecenumproperties.Multiple.MC
      )
    )
    let inSpecEnum = builder.build()
    assert.strictEqual(JSON.stringify(inSpecEnum), '{"single":"SA","multiple":["MB","MC"]}')
  }

  testDeserialization() {
    let json = '{"single":"SA", "secondSingle":"BA", "multiple":["MB","MC"]}'
    let inSpecEnum = globalFlexioImport.org.generated.InSpecEnumPropertiesBuilder.fromJson(json).build()
    assert.strictEqual(inSpecEnum.single().name(), 'SA')
    assert.strictEqual(inSpecEnum.secondSingle().name(), 'BA')
    assert.strictEqual(inSpecEnum.multiple()[0].name(), 'MB')
    assert.strictEqual(inSpecEnum.multiple()[1].name(), 'MC')
    assert.strictEqual(inSpecEnum.multiple().length, 2)
  }

  testDeserializationWithBadEnumValues(){
    let json = '{"single":"TOTO", "secondSingle":"BA", "multiple":["MB", "TOTO"]}'
    let inSpecEnum = globalFlexioImport.org.generated.InSpecEnumPropertiesBuilder.fromJson(json).build()
    assert.strictEqual(inSpecEnum.single(), null)
    assert.strictEqual(inSpecEnum.secondSingle().name(), 'BA')
    assert.strictEqual(inSpecEnum.multiple().length, 2)
    assert.strictEqual(inSpecEnum.multiple()[0].name(), 'MB')
    assert.strictEqual(inSpecEnum.multiple()[1], null)

    assert.strictEqual(JSON.stringify(inSpecEnum), '{"secondSingle":"BA","multiple":["MB",null]}')
  }
}

runTest(InSpecEnumTest)
