package org.codingmatters.value.objects.maven.plugin;

import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.Enumeration;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 9/15/16.
 */
public class GenerateMojoTest {

    @Rule
    public MojoRule rule = new MojoRule();

    @Test
    public void completeConfiguration() throws Exception {
        File pom = this.fileResource( "configuration/complete/pom.xml" );
        GenerateMojo mojo = (GenerateMojo) rule.lookupMojo("generate", pom);
        assertNotNull(mojo);

        assertThat(mojo.getDestinationPackage(), is("org.generated"));
        assertThat(mojo.getInputSpecification().getAbsolutePath(), endsWith("src/main/resources/spec.yaml"));
        assertThat(mojo.getOutputDirectory().getAbsolutePath(), endsWith("target/generated-sources"));
    }

    @Test
    public void defaultConfiguration() throws Exception {
        File pom = this.fileResource( "configuration/default/pom.xml" );
        GenerateMojo mojo = (GenerateMojo) rule.lookupConfiguredMojo(pom.getParentFile(), "generate");
        assertNotNull(mojo);

        assertThat(mojo.getDestinationPackage(), is(nullValue()));
        assertThat(mojo.getInputSpecification(), is(nullValue()));
        assertThat(mojo.getOutputDirectory().getAbsolutePath(), endsWith("target/generated-sources"));
    }

    private File fileResource(String name) throws Exception {
        Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(name);
        while(urls.hasMoreElements()) {
            URL url = urls.nextElement();
            if(url.getProtocol().equals("file")) {
                return new File(url.toURI());
            }
        }
        return null;
    }
}