package org.codingmatters.value.objects.js.parser.processing;

import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.parser.model.ParsedValueObject;
import org.codingmatters.value.objects.js.parser.model.ParsedYAMLSpec;
import org.codingmatters.value.objects.js.parser.model.ValueObjectProperty;
import org.codingmatters.value.objects.js.parser.model.types.*;

public interface ParsedYamlProcessor {
    public void process( ParsedYAMLSpec spec ) throws ProcessingException;

    public void process( ParsedValueObject valueObject ) throws ProcessingException;

    public void process( ValueObjectProperty property ) throws ProcessingException;

    public void process( ObjectTypeExternalValue externalValueObject ) throws ProcessingException;

    public void process( ObjectTypeInSpecValueObject inSpecValueObject ) throws ProcessingException;

    public void process( ObjectTypeNested nestedValueObject ) throws ProcessingException;

    public void process( ValueObjectTypeList list ) throws ProcessingException;

    public void process( ValueObjectTypePrimitiveType primitiveType ) throws ProcessingException;

    public void process( YamlEnumExternalEnum externalEnum ) throws ProcessingException;

    public void process( YamlEnumInSpecEnum inSpecEnum ) throws ProcessingException;

    public void process( ValueObjectTypeExternalType externalType ) throws ProcessingException;
}
