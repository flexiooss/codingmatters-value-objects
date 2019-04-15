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
        try( JsFileWriter fileWriter = new JsFileWriter( targetFile ) ) {
            fileWriter.line( "import{ FLEXIO_IMPORT_OBJECT, deepKeyAssigner } from 'flexio-jshelpers' " );

            for( String className : rootPackage.classes() ){
                String builder = className + "Builder";
                fileWriter.line( "import {" + className + ", " + builder + "} from \"./" + className + "\";" );
            }
            for( String className : rootPackage.lists() ){
                fileWriter.line( "import {" + className + "} from \"./" + className + "\";" );
            }
            fileWriter.newLine();
            for( String classe : rootPackage.classes() ){
                String builder = classe + "Builder";
                line( rootPackage, classe, fileWriter );
                line( rootPackage, builder, fileWriter );
            }
            for( String classe : rootPackage.lists() ){
                line( rootPackage, classe, fileWriter );
            }
            fileWriter.newLine();
            for( PackageConfiguration subPackage : rootPackage.subPackages() ){
                fileWriter.line( "import './" + subPackage.name() + "/package';" );
            }
            fileWriter.flush();
        } catch( Exception e ){
            throw new GenerationException( "Error generating package files", e );
        }
    }

    private void comment( String packageName, String classe, JsFileWriter fileWriter ) throws IOException {
        fileWriter.line( "/**" );
//        fileWriter.line( "* @property {" + classe + "} " + String.join( ".", "window[FLEXIO_IMPORT_OBJECT]", packageName, classe ) );
        fileWriter.line( "* @property {" + classe + "} " + classe );
        fileWriter.line( "*/" );
    }

    private void line( PackageConfiguration rootPackage, String classe, JsFileWriter fileWriter ) throws IOException {
        comment( rootPackage.fullName(), classe, fileWriter );
        fileWriter.line( "deepKeyAssigner( window[FLEXIO_IMPORT_OBJECT], '" + rootPackage.fullName() + "." + classe + "' ," + classe + " );" );
    }

}
