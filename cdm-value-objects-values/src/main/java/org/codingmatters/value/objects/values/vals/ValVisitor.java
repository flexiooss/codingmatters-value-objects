package org.codingmatters.value.objects.values.vals;

public interface ValVisitor<T> {
    T visitObject(Val.ObjectVal object);
    T visitArray(Val.ArrayVal array);
    <V> T visitBaseValue(Val.BaseTypeVal<V> value);
}
