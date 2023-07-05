import {TestCase} from '@flexio-oss/code-altimeter-js'
import '../org/package'
import {globalFlexioImport} from '@flexio-oss/js-commons-bundle/global-import-registry'

const assert = require('assert')

class ExternalEnumTest extends TestCase {

  testBuilder(){
    let builder = new globalFlexioImport.org.generated.EnumPropertiesBuilder();
    builder.single(globalFlexioImport.org.generated.inspecenumproperties.Single.SA)
    builder.multiple(new globalFlexioImport.org.generated.inspecenumproperties.SingleList(
        globalFlexioImport.org.generated.inspecenumproperties.Single.SA,
        globalFlexioImport.org.generated.inspecenumproperties.Single.SB
    ))
    let inSpecEnum = this.getInSpecEnum();
    builder.reference(inSpecEnum);
    builder.referenceList(
        new globalFlexioImport.org.generated.InSpecEnumPropertiesList(
            inSpecEnum
        )
    );
    let internalB = new globalFlexioImport.org.generated.InternalBuilder();
    internalB.toto("plok");
    let internal = internalB.build();
    builder.internalReference(internal);
    builder.internalReferenceList(
      new globalFlexioImport.org.generated.InternalList(
        internal
      )
    )
  }

  testSerialization(){
    let builder = new globalFlexioImport.org.generated.EnumPropertiesBuilder();
    builder.single(globalFlexioImport.org.generated.inspecenumproperties.Single.SA)
    builder.multiple(new globalFlexioImport.org.generated.inspecenumproperties.SingleList(
        globalFlexioImport.org.generated.inspecenumproperties.Single.SA,
        globalFlexioImport.org.generated.inspecenumproperties.Single.SB
    ))
    let inSpecEnum = this.getInSpecEnum();
    builder.reference(inSpecEnum);
    builder.referenceList(
        new globalFlexioImport.org.generated.InSpecEnumPropertiesList(
            inSpecEnum
        )
    );
    let internalB = new globalFlexioImport.org.generated.InternalBuilder();
    internalB.toto("plok");
    let internal = internalB.build();
    builder.internalReference(internal);
    builder.internalReferenceList(
      new globalFlexioImport.org.generated.InternalList(
        internal
      )
    )

    let enumProps = builder.build();
    assert.strictEqual(JSON.stringify(enumProps), '{"single":"SA","multiple":["SA","SB"],"reference":{"single":"SA","multiple":["MB","MC"]},"referenceList":[{"single":"SA","multiple":["MB","MC"]}],"internalReference":{"toto":"plok"},"internalReferenceList":[{"toto":"plok"}]}')
  }

  testDeserialization(){
    let json = '{"single":"SA","multiple":["SA","SB"],"reference":{"single":"SA","multiple":["MB","MC"]},"referenceList":[{"single":"SA","multiple":["MB","MC"]}],"internalReference":{"toto":"plok"},"internalReferenceList":[{"toto":"plok"}]}';
    let enumProps = globalFlexioImport.org.generated.EnumPropertiesBuilder.fromJson(json).build()
    assert.deepEqual(enumProps.single().name(), 'SA')
    assert.deepEqual(enumProps.multiple()[0].name(), 'SA')
    assert.deepEqual(enumProps.multiple()[1].name(), 'SB')
    assert.deepEqual(enumProps.reference().single().name(), 'SA')
    assert.deepEqual(enumProps.reference().multiple()[0].name(), 'MB')
    assert.deepEqual(enumProps.reference().multiple()[1].name(), 'MC')
    assert.deepEqual(enumProps.referenceList()[0].single().name(), 'SA')
    assert.deepEqual(enumProps.referenceList()[0].multiple()[0].name(), 'MB')
    assert.deepEqual(enumProps.referenceList()[0].multiple()[1].name(), 'MC')
    assert.deepEqual(enumProps.internalReference().toto(), 'plok')
    assert.deepEqual(enumProps.internalReferenceList()[0].toto(), 'plok')
  }

  getInSpecEnum() {
     let builder = new globalFlexioImport.org.generated.InSpecEnumPropertiesBuilder()
     builder.single(globalFlexioImport.org.generated.inspecenumproperties.Single.SA)
     builder.multiple(new globalFlexioImport.org.generated.inspecenumproperties.MultipleList(
       globalFlexioImport.org.generated.inspecenumproperties.Multiple.MB,
       globalFlexioImport.org.generated.inspecenumproperties.Multiple.MC
       )
     )
     let inSpecEnum = builder.build()
     return inSpecEnum;
 }

}

runTest(ExternalEnumTest)