package org.codingmatters.value.objects.maven.plugin.stub;

/**
 * Created by nelt on 9/16/16.
 */
public class ConfigurationDefaultProjectStub extends AbstractMavenProjectStub {
    @Override
    protected String projectPath() {
        System.out.println("YOPYOPYOPYOP");
        return "configuration/default/";
    }
}
