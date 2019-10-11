package org.codingmatters.value.objects.java;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.parser.model.ParsedEnum;
import org.codingmatters.value.objects.js.parser.model.ParsedValueObject;
import org.codingmatters.value.objects.js.parser.model.ParsedYAMLSpec;
import org.codingmatters.value.objects.js.parser.model.ValueObjectProperty;
import org.codingmatters.value.objects.js.parser.model.types.*;
import org.codingmatters.value.objects.js.parser.processing.ParsedYamlProcessor;

public class TypeResolver implements ParsedYamlProcessor {

    private TypeName type;

    @Override
    public void process( ParsedYAMLSpec spec )   {

    }

    @Override
    public void process( ParsedValueObject valueObject ) {
        this.type =  ClassName.get( valueObject.packageName(), valueObject.name() );
    }

    @Override
    public void process( ValueObjectProperty property ) throws ProcessingException {

    }

    @Override
    public void process( ObjectTypeExternalValue externalValueObject ) throws ProcessingException {

    }

    @Override
    public void process( ObjectTypeInSpecValueObject inSpecValueObject ) throws ProcessingException {

    }

    @Override
    public void process( ObjectTypeNested nestedValueObject ) throws ProcessingException {

    }

    @Override
    public void process( ValueObjectTypeList list ) throws ProcessingException {

    }

    @Override
    public void process( ValueObjectTypePrimitiveType primitiveType ) throws ProcessingException {

    }

    @Override
    public void process( YamlEnumExternalEnum externalEnum ) throws ProcessingException {

    }

    @Override
    public void process( YamlEnumInSpecEnum inSpecEnum ) throws ProcessingException {

    }

    @Override
    public void process( ValueObjectTypeExternalType externalType ) throws ProcessingException {

    }

    @Override
    public void process( ParsedEnum parsedEnum ) throws ProcessingException {

    }

    public TypeName type() {
        return type;
    }

}
