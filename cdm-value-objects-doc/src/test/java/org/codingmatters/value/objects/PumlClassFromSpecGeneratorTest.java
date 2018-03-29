package org.codingmatters.value.objects;

import net.sourceforge.plantuml.GeneratedImage;
import net.sourceforge.plantuml.SourceFileReader;
import org.codingmatters.value.objects.spec.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;

import java.io.*;

import static org.codingmatters.value.objects.PackageGenerator.EXT_VALUE_OBJECT_STEREOTYPE;
import static org.codingmatters.value.objects.PackageGenerator.VALUE_OBJECT_STEREOTYPE;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class PumlClassFromSpecGeneratorTest {

    @Rule
    public TemporaryFolder dir = new TemporaryFolder();
    @Rule
    public TestName name = new TestName();

    @Test
    public void emptySpec() throws Exception {
        this.generate(Spec.spec().build());

        assertThat(this.fileContent("org.generated.classes.puml"), is(this.lines(
                "@startuml",
                "@enduml"
        )));

        this.generatePng("org.generated.classes.puml");
    }

    @Test
    public void classForValue() throws Exception {
        this.generate(Spec.spec()
                .addValue(ValueSpec.valueSpec()
                        .name("a value object"))
                .build());

        assertThat(this.fileContent("org.generated.classes.puml"), is(this.lines(
                "@startuml",
                "  ",
                "  class \"org.generated.A value object\" " + VALUE_OBJECT_STEREOTYPE + " {",
                "  }",
                "@enduml"
        )));

        this.generatePng("org.generated.classes.puml");
    }

    @Test
    public void valueProtocolAsInterface() throws Exception {
        this.generate(Spec.spec()
                .addValue(ValueSpec.valueSpec()
                        .name("value").addConformsTo(Serializable.class.getName())
                )
                .build());

        assertThat(this.fileContent("org.generated.classes.puml"), is(this.lines(
                "@startuml",
                "",
                "interface \"" + Serializable.class.getName() + "\"",
                "  ",
                "  class \"org.generated.Value\" " + VALUE_OBJECT_STEREOTYPE + " {",
                "  }",
                "  \"org.generated.Value\" <|- \"" + Serializable.class.getName() + "\"",
                "@enduml"
        )));

        this.generatePng("org.generated.classes.puml");
    }

    @Test
    public void classProperty() throws Exception {
        this.generate(Spec.spec()
                .addValue(ValueSpec.valueSpec()
                        .name("value")
                        .addProperty(PropertySpec.property()
                                .name("singleProp").type(PropertyTypeSpec.type().typeKind(TypeKind.JAVA_TYPE).typeRef(String.class.getName()).cardinality(PropertyCardinality.SINGLE))
                                .build())
                        .addProperty(PropertySpec.property()
                                .name("listProp").type(PropertyTypeSpec.type().typeKind(TypeKind.JAVA_TYPE).typeRef(String.class.getName()).cardinality(PropertyCardinality.LIST))
                                .build())
                        .addProperty(PropertySpec.property()
                                .name("setProp").type(PropertyTypeSpec.type().typeKind(TypeKind.JAVA_TYPE).typeRef(String.class.getName()).cardinality(PropertyCardinality.SET))
                                .build())
                ).build());

        assertThat(this.fileContent("org.generated.classes.puml"), is(this.lines(
                "@startuml",
                "  ",
                "  class \"org.generated.Value\" " + VALUE_OBJECT_STEREOTYPE + " {",
                "    {field} singleProp : " + String.class.getName(),
                "    {field} listProp : ValueList<" + String.class.getName() + ">",
                "    {field} setProp : ValueSet<" + String.class.getName() + ">",
                "  }",
                "@enduml"
        )));

        this.generatePng("org.generated.classes.puml");
    }

    @Test
    public void inSpecValuePropertiesAsAggregation() throws Exception {
        this.generate(Spec.spec()
                .addValue(ValueSpec.valueSpec()
                        .name("value")
                        .addProperty(PropertySpec.property()
                                .name("singleProp").type(PropertyTypeSpec.type()
                                        .typeKind(TypeKind.IN_SPEC_VALUE_OBJECT)
                                        .typeRef("value2")
                                        .cardinality(PropertyCardinality.SINGLE))
                        )
                        .addProperty(PropertySpec.property()
                                .name("listProp").type(PropertyTypeSpec.type()
                                        .typeKind(TypeKind.IN_SPEC_VALUE_OBJECT)
                                        .typeRef("value2")
                                        .cardinality(PropertyCardinality.LIST))
                        )
                        .addProperty(PropertySpec.property()
                                .name("setProp").type(PropertyTypeSpec.type()
                                        .typeKind(TypeKind.IN_SPEC_VALUE_OBJECT)
                                        .typeRef("value2")
                                        .cardinality(PropertyCardinality.SET))
                        )
                ).build());

        assertThat(this.fileContent("org.generated.classes.puml"), is(this.lines(
                "@startuml",
                "  ",
                "  class \"org.generated.Value\" " + VALUE_OBJECT_STEREOTYPE + " {",
                "  }",
                "  \"org.generated.Value\" *-- \"org.generated.Value2\" : singleProp",
                "  \"org.generated.Value\" *-- \"*\" \"org.generated.Value2\" : listProp",
                "  \"org.generated.Value\" *-- \"*\" \"org.generated.Value2\" : setProp\\n[Set]",
                "@enduml"
        )));

        this.generatePng("org.generated.classes.puml");
    }



    @Test
    public void externalSpecValuePropertiesAsAggregation() throws Exception {
        this.generate(Spec.spec()
                .addValue(ValueSpec.valueSpec()
                        .name("value")
                        .addProperty(PropertySpec.property()
                                .name("singleProp").type(PropertyTypeSpec.type()
                                        .typeKind(TypeKind.EXTERNAL_VALUE_OBJECT)
                                        .typeRef("com.ext.Value")
                                        .cardinality(PropertyCardinality.SINGLE))
                        )
                        .addProperty(PropertySpec.property()
                                .name("listProp").type(PropertyTypeSpec.type()
                                        .typeKind(TypeKind.EXTERNAL_VALUE_OBJECT)
                                        .typeRef("com.ext.Value")
                                        .cardinality(PropertyCardinality.LIST))
                        )
                        .addProperty(PropertySpec.property()
                                .name("setProp").type(PropertyTypeSpec.type()
                                        .typeKind(TypeKind.EXTERNAL_VALUE_OBJECT)
                                        .typeRef("com.ext.Value")
                                        .cardinality(PropertyCardinality.SET))
                        )
                ).build());

        assertThat(this.fileContent("org.generated.classes.puml"), is(this.lines(
                "@startuml",
                "  ",
                "  class \"org.generated.Value\" " + VALUE_OBJECT_STEREOTYPE + " {",
                "  }",
                "  \"org.generated.Value\" *-- \"com.ext.Value\" : singleProp",
                "  \"org.generated.Value\" *-- \"*\" \"com.ext.Value\" : listProp",
                "  \"org.generated.Value\" *-- \"*\" \"com.ext.Value\" : setProp\\n[Set]",
                "  class \"com.ext.Value\" " + EXT_VALUE_OBJECT_STEREOTYPE + " {",
                "  }",
                "@enduml"
        )));

        this.generatePng("org.generated.classes.puml");
    }

    @Test
    public void enumProperties() throws Exception {
        this.generate(Spec.spec()
                .addValue(ValueSpec.valueSpec()
                        .name("value")
                        .addProperty(PropertySpec.property()
                                .name("singleProp").type(PropertyTypeSpec.type()
                                        .typeKind(TypeKind.ENUM)
                                        .enumValues("A", "B", "C")
                                        .cardinality(PropertyCardinality.SINGLE))
                        )
                        .addProperty(PropertySpec.property()
                                .name("listProp").type(PropertyTypeSpec.type()
                                        .typeKind(TypeKind.ENUM)
                                        .enumValues("D", "E", "F")
                                        .cardinality(PropertyCardinality.LIST))
                        )
                        .addProperty(PropertySpec.property()
                                .name("setProp").type(PropertyTypeSpec.type()
                                        .typeKind(TypeKind.ENUM)
                                        .enumValues("G", "H", "I")
                                        .cardinality(PropertyCardinality.SET))
                        )
                ).build());

        assertThat(this.fileContent("org.generated.classes.puml"), is(this.lines(
                "@startuml",
                "  ",
                "  class \"org.generated.Value\" " + VALUE_OBJECT_STEREOTYPE + " {",
                "  }",
                "  ",
                "  enum \"org.generated.Value.SingleProp\" {",
                "    A",
                "    B",
                "    C",
                "  }",
                "  \"org.generated.Value\" *-- \"org.generated.Value.SingleProp\" : singleProp",
                "  ",
                "  enum \"org.generated.Value.ListProp\" {",
                "    D",
                "    E",
                "    F",
                "  }",
                "  \"org.generated.Value\" *-- \"*\" \"org.generated.Value.ListProp\" : listProp",
                "  ",
                "  enum \"org.generated.Value.SetProp\" {",
                "    G",
                "    H",
                "    I",
                "  }",
                "  \"org.generated.Value\" *-- \"*\" \"org.generated.Value.SetProp\" : setProp\\n[Set]",
                "@enduml"
        )));

        this.generatePng("org.generated.classes.puml");
    }

    @Test
    public void embeddedPropertiesAsAggregation() throws Exception {
        this.generate(Spec.spec()
                .addValue(ValueSpec.valueSpec()
                        .name("value")
                        .addProperty(PropertySpec.property()
                                .name("singleProp").type(PropertyTypeSpec.type()
                                        .typeKind(TypeKind.EMBEDDED)
                                        .embeddedValueSpec(AnonymousValueSpec.anonymousValueSpec()
                                                .build())
                                        .cardinality(PropertyCardinality.SINGLE))
                        )
                        .addProperty(PropertySpec.property()
                                .name("listProp").type(PropertyTypeSpec.type()
                                        .typeKind(TypeKind.EMBEDDED)
                                        .embeddedValueSpec(AnonymousValueSpec.anonymousValueSpec()
                                                .build())
                                        .cardinality(PropertyCardinality.LIST))
                        )
                        .addProperty(PropertySpec.property()
                                .name("setProp").type(PropertyTypeSpec.type()
                                        .typeKind(TypeKind.EMBEDDED)
                                        .embeddedValueSpec(AnonymousValueSpec.anonymousValueSpec()
                                                .build())
                                        .cardinality(PropertyCardinality.SET))
                        )
                ).build());

        assertThat(this.fileContent("org.generated.classes.puml"), is(this.lines(
                "@startuml",
                "  ",
                "  class \"org.generated.Value\" " + VALUE_OBJECT_STEREOTYPE + " {",
                "  }",
                "  \"org.generated.Value\" *-- \"org.generated.value.SingleProp\" : singleProp",
                "  \"org.generated.Value\" *-- \"*\" \"org.generated.value.ListProp\" : listProp",
                "  \"org.generated.Value\" *-- \"*\" \"org.generated.value.SetProp\" : setProp\\n[Set]",
                "  ",
                "  class \"org.generated.value.SingleProp\" " + VALUE_OBJECT_STEREOTYPE + " {",
                "  }",
                "  ",
                "  class \"org.generated.value.ListProp\" " + VALUE_OBJECT_STEREOTYPE + " {",
                "  }",
                "  ",
                "  class \"org.generated.value.SetProp\" " + VALUE_OBJECT_STEREOTYPE + " {",
                "  }",
                "@enduml"
        )));

        this.generatePng("org.generated.classes.puml");
    }





    private void generatePng(String name) throws IOException {
//        File outputDir = new File("/tmp/exp/" + this.name.getMethodName());
        File outputDir = this.dir.newFolder();
        outputDir.mkdirs();

        SourceFileReader sourceFileReader = new SourceFileReader(new File(this.dir.getRoot(), name), outputDir, "UTF-8");
        if(sourceFileReader.hasError()) {
            for (GeneratedImage generatedImage : sourceFileReader.getGeneratedImages()) {
                System.err.println(generatedImage);
            }
            System.err.println(this.fileContent(name));


            throw new AssertionError("failed generating png from puml");
        }
        for (GeneratedImage generatedImage : sourceFileReader.getGeneratedImages()) {
            System.out.println("generated : " + generatedImage);
        }
    }

    private String lines(String ... lines) {
        if(lines == null) return null;
        StringBuilder result = new StringBuilder();
        for (String line : lines) {
            result.append(line).append("\n");
        }

        return result.toString();
    }

    private String fileContent(String name) throws IOException {
        File file = new File(this.dir.getRoot(), name);
        try(ByteArrayOutputStream out = new ByteArrayOutputStream(); FileInputStream in = new FileInputStream(file)) {
            byte [] buffer = new byte[1024];
            for(int read = in.read(buffer) ; read != -1 ; read = in.read(buffer)) {
                out.write(buffer, 0, read);
            }
            return out.toString();
        }
    }

    private void generate(Spec spec) throws IOException {
        new PumlClassFromSpecGenerator(spec, "org.generated", this.dir.getRoot()).generate();
    }

}