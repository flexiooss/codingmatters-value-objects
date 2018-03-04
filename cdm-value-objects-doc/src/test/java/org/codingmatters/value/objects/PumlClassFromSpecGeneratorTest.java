package org.codingmatters.value.objects;

import net.sourceforge.plantuml.GeneratedImage;
import net.sourceforge.plantuml.SourceFileReader;
import org.codingmatters.value.objects.spec.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;

import java.io.*;

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
                "package org.generated {",
                "}",
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
                "package org.generated {",
                "  ",
                "  class \"A value object\" {",
                "  }",
                "}",
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
                "package org.generated {",
                "  ",
                "  class \"Value\" {",
                "  }",
                "  \"Value\" <|- \"" + Serializable.class.getName() + "\"",
                "}",
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
                "package org.generated {",
                "  ",
                "  class \"Value\" {",
                "    {field} singleProp : " + String.class.getName(),
                "    {field} listProp : ValueList<" + String.class.getName() + ">",
                "    {field} setProp : ValueSet<" + String.class.getName() + ">",
                "  }",
                "}",
                "@enduml"
        )));

        this.generatePng("org.generated.classes.puml");
    }

    @Test
    public void valuePropertiesAsAggregation() throws Exception {
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
                "package org.generated {",
                "  ",
                "  class \"Value\" {",
                "  }",
                "  Value *-- Value2 : singleProp",
                "  Value *-- \"*\" Value2 : listProp",
                "  Value *-- \"*\" Value2 : setProp\\n[Set]",
                "}",
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
                "package org.generated {",
                "  ",
                "  class \"Value\" {",
                "  }",
                "  ",
                "  enum \"Value.SingleProp\" {",
                "    A",
                "    B",
                "    C",
                "  }",
                "  Value *-- Value.SingleProp : singleProp",
                "  ",
                "  enum \"Value.ListProp\" {",
                "    D",
                "    E",
                "    F",
                "  }",
                "  Value *-- \"*\" Value.ListProp : listProp",
                "  ",
                "  enum \"Value.SetProp\" {",
                "    G",
                "    H",
                "    I",
                "  }",
                "  Value *-- \"*\" Value.SetProp : setProp\\n[Set]",
                "}",
                "@enduml"
        )));

        this.generatePng("org.generated.classes.puml");
    }






    private void generatePng(String name) throws IOException {
        File outputDir = new File("/tmp/exp/" + this.name.getMethodName());
        outputDir.mkdirs();

        SourceFileReader sourceFileReader = new SourceFileReader(new File(this.dir.getRoot(), name), outputDir, "UTF-8");
        if(sourceFileReader.hasError()) {
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