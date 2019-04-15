package org.codingmatters.value.objects.js;

import org.codingmatters.value.objects.js.generator.packages.PackageConfiguration;
import org.codingmatters.value.objects.js.generator.packages.PackageFilesBuilder;
import org.codingmatters.value.objects.js.generator.packages.PackageFilesGenerator;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PackageFilesBuilderTest {

    @Test
    public void whenAddClass_thenPackageHaveRootClass() throws Exception {
        PackageFilesBuilder packageFilesBuilder = new PackageFilesBuilder();
        packageFilesBuilder.addClass( "org.generated", "ClassA" );

        PackageConfiguration orgPackage = packageFilesBuilder.packageConfig().get( "org" );
        assertThat( orgPackage.name(), is( "org" ) );
        assertThat( orgPackage.subPackages().length, is( 1 ) );
        assertThat( orgPackage.fullName(), is( "org" ) );

        PackageConfiguration subPackage = orgPackage.subPackages()[0];
        assertThat( subPackage.subPackages().length, is( 0 ) );
        assertThat( subPackage.name(), is( "generated" ) );
        assertThat( subPackage.fullName(), is( "org.generated" ) );
        assertThat( subPackage.classes(), is( new String[]{ "ClassA" } ) );
    }

    @Test
    public void whenAddMultipleClasses_thenPackagesAreMerged() throws Exception {
        PackageFilesBuilder packageFilesBuilder = new PackageFilesBuilder();
        packageFilesBuilder.addClass( "org.generated", "ClassA" );
        packageFilesBuilder.addClass( "org.generated", "ClassB" );
        packageFilesBuilder.addClass( "org.generated.toto", "ClassC" );
        packageFilesBuilder.addClass( "io.toto", "ClassD" );
        packageFilesBuilder.addClass( "org", "ClassE" );

        PackageConfiguration orgPackage = packageFilesBuilder.packageConfig().get( "org" );
        assertThat( orgPackage.name(), is( "org" ) );
        assertThat( orgPackage.fullName(), is( "org" ) );
        assertThat( orgPackage.subPackages().length, is( 1 ) );
        assertThat( orgPackage.classes(), is( new String[]{ "ClassE" } ) );

        PackageConfiguration subPackage = orgPackage.subPackage( "generated" );
        assertThat( subPackage.fullName(), is( "org.generated" ) );
        assertThat( subPackage.classes(), is( new String[]{ "ClassA", "ClassB" } ) );
        assertThat( subPackage.subPackages().length, is( 1 ) );

        subPackage = subPackage.subPackage( "toto" );
        assertThat( subPackage.fullName(), is( "org.generated.toto" ) );
        assertThat( subPackage.classes(), is( new String[]{ "ClassC" } ) );
        assertThat( subPackage.subPackages().length, is( 0 ) );

        PackageConfiguration ioPackage = packageFilesBuilder.packageConfig().get( "io" );
        assertThat( ioPackage.name(), is( "io" ) );
        assertThat( ioPackage.subPackage( "toto" ).classes(), is( new String[]{ "ClassD" } ) );
        assertThat( ioPackage.subPackage( "toto" ).fullName(), is( "io.toto" ) );
    }


    @Test
    public void whenAddMultipleLists_thenPackageAreMerged() throws Exception {
        PackageFilesBuilder packageFilesBuilder = new PackageFilesBuilder();
        packageFilesBuilder.addList( "org.generated", "ClassA" );
        packageFilesBuilder.addList( "org.generated", "ClassB" );
        packageFilesBuilder.addList( "org.generated.toto", "ClassC" );
        packageFilesBuilder.addList( "io.toto", "ClassD" );
        packageFilesBuilder.addList( "org", "ClassE" );

        PackageConfiguration orgPackage = packageFilesBuilder.packageConfig().get( "org" );
        assertThat( orgPackage.name(), is( "org" ) );
        assertThat( orgPackage.subPackages().length, is( 1 ) );
        assertThat( orgPackage.lists(), is( new String[]{ "ClassE" } ) );

        PackageConfiguration subPackage = orgPackage.subPackage( "generated" );
        assertThat( subPackage.lists(), is( new String[]{ "ClassA", "ClassB" } ) );
        assertThat( subPackage.subPackages().length, is( 1 ) );

        subPackage = subPackage.subPackage( "toto" );
        assertThat( subPackage.lists(), is( new String[]{ "ClassC" } ) );
        assertThat( subPackage.subPackages().length, is( 0 ) );

        PackageConfiguration ioPackage = packageFilesBuilder.packageConfig().get( "io" );
        assertThat( ioPackage.name(), is( "io" ) );
        assertThat( ioPackage.subPackage( "toto" ).lists(), is( new String[]{ "ClassD" } ) );
    }

}
