package org.codingmatters.value.objects.js.generator.packages;

import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.generator.JsFileWriter;

import java.io.File;

public class JsonPackageGenerator {

    private final File rootDirectory;

    public JsonPackageGenerator( File rootDirectory ) {
        this.rootDirectory = rootDirectory;
    }

    public void generatePackageJson( String vendor, String artifactId, String version, String rootPackage ) throws ProcessingException {
        try( JsFileWriter write = new JsFileWriter( new File( rootDirectory, "package.json" ).getPath() ) ) {
            write.line( "{" );
            write.line( "\"name\": \"@" + vendor + "/" + artifactId + "\"," );
            write.line( "\"version\": \"" + version + "\"," );
            write.line( "\"devDependencies\": {" );
            write.line( "\"@flexio-corp/js-api-client-parent\": \"0.6.0\"" );
            write.unindent();
            write.line( "}," );
            write.line( "\"peerDependencies\": {" );
            write.line( "\"@flexio-corp/js-api-client-parent\": \">0.6.0\"" );
            write.unindent();
            write.line( "}," );
            write.line( "\"main\": \"" + rootPackage + "/package.js\"" );
            write.line( "}" );
            write.flush();
        } catch( Exception e ){
            throw new ProcessingException( "Error generating package.json file", e );
        }
    }
}
