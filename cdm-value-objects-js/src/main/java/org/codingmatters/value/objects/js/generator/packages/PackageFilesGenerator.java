package org.codingmatters.value.objects.js.generator.packages;

import org.codingmatters.value.objects.js.generator.GenerationException;
import org.codingmatters.value.objects.js.generator.JsFileWriter;

import java.io.IOException;

public class PackageFilesGenerator {

    private final PackageFilesBuilder filesBuilder;
    private final String targetDirectory;

    public PackageFilesGenerator( PackageFilesBuilder filesBuilder, String targetDirectory ) {
        this.targetDirectory = targetDirectory;
        this.filesBuilder = filesBuilder;
    }

    public void generateFiles() throws GenerationException {
        generateFilesRecursively( filesBuilder.packageConfig().values().toArray( new PackageConfiguration[0] ) );
    }

    private void generateFilesRecursively( PackageConfiguration[] packages ) throws GenerationException {
        for( PackageConfiguration rootPackage : packages ){
            generatePackageFile( rootPackage );
            generateFilesRecursively( rootPackage.subPackages() );
        }
    }

    private void generatePackageFile( PackageConfiguration rootPackage ) throws GenerationException {
        String targetFile = targetDirectory + "/" + rootPackage.fullName().replace( ".", "/" ) + "/package.js";
        try( JsFileWriter fileWriter = new JsFileWriter( targetFile ) ){
            fileWriter.line( "import { globalFlexioImport } from '@flexio-oss/js-commons-bundle/global-import-registry'" );
            fileWriter.line( "import { deepKeyAssigner } from '@flexio-oss/js-commons-bundle/js-generator-helpers'" );

            for( PackageConfiguration.ObjectValueConfiguration objectValueConfig : rootPackage.classes() ){
                String builder = objectValueConfig.name() + "Builder";
                fileWriter.line( "import { " + objectValueConfig.name() + ", " + builder + " } from './" + objectValueConfig.name() + "'" );
                if( objectValueConfig.generateList() ){
                    fileWriter.line( "import { " + objectValueConfig.name() + "List } from './" + objectValueConfig.name() + "List'" );
                }
            }
            for( PackageConfiguration.ObjectValueConfiguration className : rootPackage.lists() ){
                fileWriter.line( "import { " + className.name() + " } from './" + className.name() + "'" );
                if( className.generateList() ){
                    fileWriter.line( "import { " + className.name() + "List } from './" + className.name() + "List'" );
                }
            }
            fileWriter.newLine();
            for( PackageConfiguration.ObjectValueConfiguration objectValueConfig : rootPackage.classes() ){
                String builder = objectValueConfig.name() + "Builder";
                line( rootPackage, objectValueConfig.name(), fileWriter );
                if( objectValueConfig.generateList() ){
                    line( rootPackage, objectValueConfig.name() + "List", fileWriter );
                }
                line( rootPackage, builder, fileWriter );
            }
            for( PackageConfiguration.ObjectValueConfiguration classe : rootPackage.lists() ){
                line( rootPackage, classe.name(), fileWriter );
                if( classe.generateList() ){
                    line( rootPackage, classe.name() + "List", fileWriter );
                }
            }
            fileWriter.newLine();
            for( PackageConfiguration subPackage : rootPackage.subPackages() ){
                fileWriter.line( "import './" + subPackage.name() + "/package'" );
            }
            fileWriter.flush();
        } catch( Exception e ) {
            throw new GenerationException( "Error generating package files", e );
        }
    }

    private void comment( String packageName, String classe, JsFileWriter fileWriter ) throws IOException {
        fileWriter.line( "/**" );
        fileWriter.line( " * @property {" + classe + ".} " + String.join( ".", "globalFlexioImport", packageName, classe ) );
        fileWriter.line( " */" );
    }

    private void line( PackageConfiguration rootPackage, String classe, JsFileWriter fileWriter ) throws IOException {
        comment( rootPackage.fullName(), classe, fileWriter );
        fileWriter.line( "deepKeyAssigner(globalFlexioImport, '" + rootPackage.fullName() + "." + classe + "', " + classe + ")" );
    }

}
