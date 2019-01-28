package org.codingmatters.value.objects.js.generator.packages;

import org.codingmatters.value.objects.js.generator.GenerationException;
import org.codingmatters.value.objects.js.generator.JsFileWriter;

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
            for( String className : rootPackage.classes() ){
                fileWriter.writeLine( "import {" + className + "} from \"./" + className + "\";" );
            }
            for( String className : rootPackage.lists() ){
                fileWriter.writeLine( "import {" + className + "} from \"./" + className + "\";" );
            }
            for( String classe : rootPackage.classes() ){
                fileWriter.writeLine( "Object.assign( " + rootPackage.wrapInObject( "{\"" + classe + "\": " + classe + "}" ) + ", window );" );
            }
            for( String classe : rootPackage.lists() ){
                fileWriter.writeLine( "Object.assign( " + rootPackage.wrapInObject( "{\"" + classe + "\": " + classe + "}" ) + ", window );" );
            }
            for( PackageConfiguration subPackage : rootPackage.subPackages() ){
                fileWriter.writeLine( "import('./" + subPackage.name() + "/package');" );
            }
        } catch( Exception e ){
            throw new GenerationException( "Error generating package files", e );
        }
    }

}
