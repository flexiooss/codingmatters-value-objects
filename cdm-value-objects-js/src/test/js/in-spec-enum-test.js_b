import {InSpecEnumProperties, InSpecEnumPropertiesBuilder} from '../org/generated/InSpecEnumProperties'
import {InSpecEnumPropertiesSingle} from '../org/generated/inspecenumproperties/InSpecEnumPropertiesSingle'

test( 'test builder', () => {
    var builder = new InSpecEnumPropertiesBuilder();
    builder.single( InSpecEnumPropertiesSingle.SA );
    builder.multiple( [InSpecEnumPropertiesSingle.SB, InSpecEnumPropertiesSingle.SC] );
    var inSpecEnum = builder.build();

    expect( inSpecEnum.single.name ).toEqual( "SA" );
    expect( inSpecEnum.multiple[0].name ).toEqual( "SB" );
    expect( inSpecEnum.multiple[1].name ).toEqual( "SC" );
    expect( inSpecEnum.multiple.length ).toEqual( 2 );
} );

test( 'assert object immutable', () => {
    var builder = new InSpecEnumPropertiesBuilder();
    builder.single( InSpecEnumPropertiesSingle.SA );
    builder.multiple( [InSpecEnumPropertiesSingle.SB, InSpecEnumPropertiesSingle.SC] );
    var inSpecEnum = builder.build();

    expect( () => {
        inSpecEnum.single = InSpecEnumPropertiesSingle.SB;
    } ).toThrow( TypeError );
} );

test( 'assert object frozen', () => {
    var builder = new InSpecEnumPropertiesBuilder();
    builder.single( InSpecEnumPropertiesSingle.SA );
    builder.multiple( [InSpecEnumPropertiesSingle.SB, InSpecEnumPropertiesSingle.SC] );
    var inSpecEnum = builder.build();

    expect( () => {
        inSpecEnum.toto = "toto";
    } ).toThrow( TypeError );
} );

test( 'test serialization', () => {
    var builder = new InSpecEnumPropertiesBuilder();
    builder.single( InSpecEnumPropertiesSingle.SA );
    builder.multiple( [InSpecEnumPropertiesSingle.SB, InSpecEnumPropertiesSingle.SC] );
    var inSpecEnum = builder.build();
    expect( JSON.stringify( inSpecEnum ) ).toBe( "{\"single\":\"SA\",\"multiple\":[\"SB\",\"SC\"]}" );
} );

test( 'test deserialization', () => {
    var json = "{\"single\":\"SA\",\"multiple\":[\"SB\",\"SC\"]}";
    var inSpecEnum = InSpecEnumPropertiesBuilder.fromJson( json );
    expect( inSpecEnum.single.name ).toEqual( "SA" );
    expect( inSpecEnum.multiple[0].name ).toEqual( "SB" );
    expect( inSpecEnum.multiple[1].name ).toEqual( "SC" );
    expect( inSpecEnum.multiple.length ).toEqual( 2 );
} );
