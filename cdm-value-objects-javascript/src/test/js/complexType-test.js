import {ComplexTypeBuilder} from '../org/generated/ComplexType'
import {ComplexPropsBuilder} from '../org/generated/complextype/ComplexProps'

test( 'test builder', () => {
    var propBuilder = new ComplexPropsBuilder();
    propBuilder.stringProp( "toto" );
    propBuilder.intList( [4, 7, 5] );
    var prop = propBuilder.build();

    var builder = new ComplexTypeBuilder();
    builder.complexProps( prop );
    builder.foo( "foo" );
    builder.testIsOk( true );
    var complexType = builder.build();

    expect( complexType.testIsOk ).toEqual( true );
    expect( complexType.foo ).toEqual( "foo" );
    expect( complexType.complexProps.stringProp ).toEqual( "toto" );
    expect( complexType.complexProps.intList ).toEqual( [4, 7, 5] );

} );