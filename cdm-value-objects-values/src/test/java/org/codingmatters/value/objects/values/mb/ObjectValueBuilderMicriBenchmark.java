package org.codingmatters.value.objects.values.mb;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.codingmatters.value.objects.values.ObjectValue;
import org.codingmatters.value.objects.values.PropertyValue;
import org.codingmatters.value.objects.values.json.ObjectValueWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

public class ObjectValueBuilderMicriBenchmark {

    private final JsonFactory jsonFactory = new JsonFactory();

    /**
     * MAVEN_OPTS=-Xmx64m mvn exec:java -Dexec.mainClass=org.codingmatters.value.objects.values.mb.ObjectValueBuildMicriBenchmark -Dexec.classpathScope="test"
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        ObjectValueBuilderMicriBenchmark bench = new ObjectValueBuilderMicriBenchmark();

        bench.setup();
        bench.run();
    }

    private void setup() throws IOException {
        System.out.println("press enter to start");
        System.in.read();
    }

    private void run() throws IOException {
        long count = 0;
        while(true) {
            this.unit();
            count++;
            if(count % 10000 == 0) {
                System.out.printf("%010d\n", count);
            }
        }
    }

    private void unit() throws IOException {
        PropertyValue propertyValue = PropertyValue.builder().stringValue(UUID.randomUUID().toString()).build();
        ObjectValue.Builder builder = ObjectValue.builder();
        for (int i = 0; i < 50; i++) {
            builder.property(
                    "p" + i,
                    PropertyValue.builder().stringValue(
                            propertyValue.single().rawValue().toString()
                    ).build()
            );
        }
        ObjectValue value = builder.build();
        try(ByteArrayOutputStream out = new ByteArrayOutputStream() ; JsonGenerator gen = this.jsonFactory.createGenerator(out)) {
            new ObjectValueWriter().write(gen, value);
        }
    }


    public ObjectValue filterRecordToLine(ObjectValue record, PropertyValue.Value[] properties ) {
        ObjectValue.Builder builder = ObjectValue.builder();
        Long column = 1L;
        String propertyName;
        PropertyValue propertyValue;
        PropertyValue.Type type;
        for( PropertyValue.Value property : properties ){
            propertyName = column.toString();
            if( record.has( property.stringValue() ) ){
                propertyValue = record.property( property.stringValue() );
                if( !propertyValue.isNullValue() ){
//*** CETTE LIGNE EST SUSPECTE *****
                    builder.property(
                            propertyName,
                            PropertyValue.builder().stringValue(
                                    propertyValue.single().rawValue().toString()
                            ).build()
                    );
// type = propertyValue.type();
// switch( type ){
// case TIME:
// builder.property( propertyName, PropertyValue.builder().stringValue( propertyValue.single().timeValue().toString() ) );
// break;
// case DATETIME:
// builder.property( propertyName, PropertyValue.builder().stringValue( propertyValue.single().datetimeValue().toString() ) );
// break;
// case DATE:
// builder.property( propertyName, PropertyValue.builder().stringValue( propertyValue.single().dateValue().toString() ) );
// break;
// case BYTES:
// StringBuilder sb = new StringBuilder();
// for( byte b : propertyValue.single().bytesValue() ){
// sb.append( String.format( "%02x ", b ) );
// }
// builder.property( propertyName, PropertyValue.builder().stringValue( sb.toString() ) );
// break;
// default:
// builder.property( propertyName, PropertyValue.builder().stringValue( propertyValue.single().rawValue().toString() ) );
// }
                }
            }
            column++;
        }
        return builder.build();
    }
}
