import {InSpecEnumProperties, InSpecEnumPropertiesBuilder} from '../org/generated/InSpecEnumProperties'
import {InSpecEnumPropertiesSingle} from '../org/generated/inSpecEnumProperties/InSpecEnumPropertiesSingle'
import {InSpecEnumPropertiesMultiple} from '../org/generated/inSpecEnumProperties/InSpecEnumPropertiesMultiple'

test( 'test builder', () => {
    var builder = new InSpecEnumPropertiesBuilder();
    builder.single( InSpecEnumPropertiesSingle.SA );
    builder.multiple( [InSpecEnumPropertiesMultiple.MB, InSpecEnumPropertiesMultiple.MC] );
    var inSpecEnum = builder.build();

    expect( inSpecEnum.single().name ).toEqual( "SA" );
    expect( inSpecEnum.multiple()[0].name ).toEqual( "MB" );
    expect( inSpecEnum.multiple()[1].name ).toEqual( "MC" );
    expect( inSpecEnum.multiple().length ).toEqual( 2 );
} );

test( 'assert object immutable', () => {
    var builder = new InSpecEnumPropertiesBuilder();
    builder.single( InSpecEnumPropertiesSingle.SA );
    builder.multiple( [InSpecEnumPropertiesMultiple.MB, InSpecEnumPropertiesMultiple.MC] );
    var inSpecEnum = builder.build();

    expect( () => {
        inSpecEnum._single = InSpecEnumPropertiesSingle.SB;
    } ).toThrow( TypeError );
} );

test( 'assert object frozen', () => {
    var builder = new InSpecEnumPropertiesBuilder();
    builder.single( InSpecEnumPropertiesSingle.SA );
    builder.multiple( [InSpecEnumPropertiesMultiple.MB, InSpecEnumPropertiesMultiple.MC] );
    var inSpecEnum = builder.build();

    expect( () => {
        inSpecEnum.toto = "toto";
    } ).toThrow( TypeError );
} );

test( 'test serialization', () => {
    var builder = new InSpecEnumPropertiesBuilder();
    builder.single( InSpecEnumPropertiesSingle.SA );
    builder.multiple( [InSpecEnumPropertiesMultiple.MB, InSpecEnumPropertiesMultiple.MC] );
    var inSpecEnum = builder.build();
    expect( JSON.stringify( inSpecEnum ) ).toBe( "{\"single\":\"SA\",\"multiple\":[\"MB\",\"MC\"]}" );
} );

test( 'test deserialization', () => {
    var json = "{\"single\":\"SA\",\"multiple\":[\"MB\",\"MC\"]}";
    var inSpecEnum = InSpecEnumPropertiesBuilder.fromJson( json );
    expect( inSpecEnum.single().name ).toEqual( "SA" );
    expect( inSpecEnum.multiple()[0].name ).toEqual( "MB" );
    expect( inSpecEnum.multiple()[1].name ).toEqual( "MC" );
    expect( inSpecEnum.multiple().length ).toEqual( 2 );
} );
