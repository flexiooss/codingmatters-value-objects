package org.codingmatters.value.objects.values.pointed;

import org.codingmatters.value.objects.values.ObjectValue;
import org.codingmatters.value.objects.values.PropertyValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class ObjectValuePointedPathManipulator {
    private final ObjectValue value;

    public ObjectValuePointedPathManipulator(ObjectValue value) {
        this.value = value;
    }

    public PropertyValue valueAtPath(String pointedPath) {
        Iterator<String> iterator = this.getIterator(pointedPath);
        PropertyValue currentValue = PropertyValue.builder().objectValue(this.value).build();

        while (iterator.hasNext()) {
            String path = iterator.next();

            if (currentValue == null) {
                return null;
            }

            PointedIndexPathManipulator indexPathManipulator = new PointedIndexPathManipulator(path);
            if (indexPathManipulator.hasIndex()) {
                currentValue = this.multipleValue(indexPathManipulator, currentValue.single().objectValue());
            } else {
                if (currentValue.isNullValue() || !currentValue.single().objectValue().has(path)) {
                    return null;
                }
                currentValue = currentValue.single().objectValue().property(path);
            }
        }
        return currentValue;
    }

    private PropertyValue multipleValue(PointedIndexPathManipulator indexPathManipulator, ObjectValue currentValue) {
        String prop = indexPathManipulator.getProperty();
        if (!currentValue.has(prop) || currentValue.property(prop) == null) {
            return null;
        }
        int index = indexPathManipulator.getIndex();
        PropertyValue property = currentValue.property(prop);
        PropertyValue.Value[] values = property.multiple();
        if (values.length <= index) {
            return null;
        }
        return PropertyValue.single(values[index]);
    }

    public ObjectValue updateValueAt(String pointedPath, ObjectValue value) {
        if (pointedPath == null || pointedPath.isEmpty() || value == null) {
            return this.value;
        }
        Iterator<String> iterator = this.getIterator(pointedPath);
        return this.travel(iterator, this.value, value);
    }

    private Iterator<String> getIterator(String pointedPath) {
        return new PointedPathManipulator(pointedPath).getPath().iterator();
    }

    private ObjectValue travel(Iterator<String> iterator, ObjectValue baseValue, ObjectValue newValue) {
        if (!iterator.hasNext()) {
            return newValue;
        } else {
            String path = iterator.next();
            PointedIndexPathManipulator indexPathManipulator = new PointedIndexPathManipulator(path);
            if (indexPathManipulator.hasIndex()) {
                return this.travelMultiple(iterator, indexPathManipulator, baseValue, newValue);
            } else {
                ObjectValue subValue = baseValue.has(path) ? baseValue.property(path).single().objectValue() : ObjectValue.builder().build();
                ObjectValue updatedValue = this.travel(iterator, subValue, newValue);
                return baseValue.withProperty(path, v -> v.objectValue(updatedValue));
            }
        }
    }

    private ObjectValue travelMultiple(Iterator<String> iterator, PointedIndexPathManipulator indexPathManipulator, ObjectValue baseValue, ObjectValue newValue) {
        int index = indexPathManipulator.getIndex();
        String prop = indexPathManipulator.getProperty();
        List<PropertyValue.Value> listValue = new ArrayList<>();

        if (baseValue.has(prop) && baseValue.property(prop) != null) {
            PropertyValue.Value[] values = baseValue.property(prop).multiple();
            listValue = Arrays.stream(values).collect(Collectors.toList());
        }
        while (listValue.size() < index + 1) {
            listValue.add(PropertyValue.builder().objectValue(ObjectValue.builder().build()).buildValue());
        }

        ObjectValue subValue = listValue.get(index).objectValue();
        ObjectValue updatedValue = this.travel(iterator, subValue, newValue);
        listValue.set(index, PropertyValue.builder().objectValue(updatedValue).buildValue());
        return baseValue.withProperty(prop, PropertyValue.multiple(PropertyValue.Type.OBJECT, listValue.toArray(new PropertyValue.Value[0])));
    }
}
