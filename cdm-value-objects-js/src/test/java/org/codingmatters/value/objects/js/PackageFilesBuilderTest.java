package org.codingmatters.value.objects.js;

import org.codingmatters.value.objects.js.generator.packages.PackageConfiguration;
import org.codingmatters.value.objects.js.generator.packages.PackageConfiguration.ObjectValueConfiguration;
import org.codingmatters.value.objects.js.generator.packages.PackageFilesBuilder;
import org.junit.Test;

import javax.naming.event.ObjectChangeListener;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PackageFilesBuilderTest {

    @Test
    public void whenAddClass_thenPackageHaveRootClass() throws Exception {
        PackageFilesBuilder packageFilesBuilder = new PackageFilesBuilder();
        packageFilesBuilder.addClass( "org.generated", "ClassA", true );

        PackageConfiguration orgPackage = packageFilesBuilder.packageConfig().get( "org" );
        assertThat( orgPackage.name(), is( "org" ) );
        assertThat( orgPackage.subPackages().length, is( 1 ) );
        assertThat( orgPackage.fullName(), is( "org" ) );

        PackageConfiguration subPackage = orgPackage.subPackages()[0];
        assertThat( subPackage.subPackages().length, is( 0 ) );
        assertThat( subPackage.name(), is( "generated" ) );
        assertThat( subPackage.fullName(), is( "org.generated" ) );
        assertThat( subPackage.classes(), is( new ObjectValueConfiguration[]{new ObjectValueConfiguration( "ClassA", true )} ) );
    }

    @Test
    public void whenAddMultipleClasses_thenPackagesAreMerged() throws Exception {
        PackageFilesBuilder packageFilesBuilder = new PackageFilesBuilder();
        packageFilesBuilder.addClass( "org.generated", "ClassA", true );
        packageFilesBuilder.addClass( "org.generated", "ClassB", true );
        packageFilesBuilder.addClass( "org.generated.toto", "ClassC", true );
        packageFilesBuilder.addClass( "io.toto", "ClassD", true );
        packageFilesBuilder.addClass( "org", "ClassE", true );

        PackageConfiguration orgPackage = packageFilesBuilder.packageConfig().get( "org" );
        assertThat( orgPackage.name(), is( "org" ) );
        assertThat( orgPackage.fullName(), is( "org" ) );
        assertThat( orgPackage.subPackages().length, is( 1 ) );
        assertThat( orgPackage.classes(), is( new ObjectValueConfiguration[]{new ObjectValueConfiguration( "ClassE", true )} ) );

        PackageConfiguration subPackage = orgPackage.subPackage( "generated" );
        assertThat( subPackage.fullName(), is( "org.generated" ) );
        assertThat( subPackage.classes(), is( new ObjectValueConfiguration[]{new ObjectValueConfiguration( "ClassA", true ), new ObjectValueConfiguration( "ClassB", true )} ) );
        assertThat( subPackage.subPackages().length, is( 1 ) );

        subPackage = subPackage.subPackage( "toto" );
        assertThat( subPackage.fullName(), is( "org.generated.toto" ) );
        assertThat( subPackage.classes(), is( new ObjectValueConfiguration[]{new ObjectValueConfiguration( "ClassC", true )} ) );
        assertThat( subPackage.subPackages().length, is( 0 ) );

        PackageConfiguration ioPackage = packageFilesBuilder.packageConfig().get( "io" );
        assertThat( ioPackage.name(), is( "io" ) );
        assertThat( ioPackage.subPackage( "toto" ).classes(), is( new ObjectValueConfiguration[]{new ObjectValueConfiguration( "ClassD", true )} ) );
        assertThat( ioPackage.subPackage( "toto" ).fullName(), is( "io.toto" ) );
    }


    @Test
    public void whenAddMultipleLists_thenPackageAreMerged() throws Exception {
        PackageFilesBuilder packageFilesBuilder = new PackageFilesBuilder();
        packageFilesBuilder.addEnum( "org.generated", "ClassA", true );
        packageFilesBuilder.addEnum( "org.generated", "ClassB", true );
        packageFilesBuilder.addEnum( "org.generated.toto", "ClassC", true );
        packageFilesBuilder.addEnum( "io.toto", "ClassD", true );
        packageFilesBuilder.addEnum( "org", "ClassE", true );

        PackageConfiguration orgPackage = packageFilesBuilder.packageConfig().get( "org" );
        assertThat( orgPackage.name(), is( "org" ) );
        assertThat( orgPackage.subPackages().length, is( 1 ) );
        assertThat( orgPackage.lists(), is( new ObjectValueConfiguration[]{new ObjectValueConfiguration( "ClassE", true )} ) );

        PackageConfiguration subPackage = orgPackage.subPackage( "generated" );
        assertThat( subPackage.lists(), is( new ObjectValueConfiguration[]{new ObjectValueConfiguration( "ClassA", true ), new ObjectValueConfiguration( "ClassB", true )} ) );
        assertThat( subPackage.subPackages().length, is( 1 ) );

        subPackage = subPackage.subPackage( "toto" );
        assertThat( subPackage.lists(), is( new ObjectValueConfiguration[]{new ObjectValueConfiguration( "ClassC", true )} ) );
        assertThat( subPackage.subPackages().length, is( 0 ) );

        PackageConfiguration ioPackage = packageFilesBuilder.packageConfig().get( "io" );
        assertThat( ioPackage.name(), is( "io" ) );
        assertThat( ioPackage.subPackage( "toto" ).lists(), is( new ObjectValueConfiguration[]{new ObjectValueConfiguration( "ClassD", true )} ) );
    }

}
