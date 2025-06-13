package org.codingmatters.value.objects.values.pointed;

import org.codingmatters.value.objects.values.ObjectValue;
import org.codingmatters.value.objects.values.PropertyValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

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

    public ObjectValue removeValue(String pointedPath) {
        if (pointedPath == null || pointedPath.isEmpty()) {
            return this.value;
        }

        Iterator<String> iterator = this.getIterator(pointedPath);
        if (iterator.hasNext()) {
            return this.travelRemove(iterator, this.value, true);
        }
        return this.value;
    }

    public ObjectValue replaceByNullValue(String pointedPath) {
        if (pointedPath == null || pointedPath.isEmpty()) {
            return this.value;
        }

        Iterator<String> iterator = this.getIterator(pointedPath);
        if (iterator.hasNext()) {
            return this.travelRemove(iterator, this.value, false);
        }
        return this.value;
    }

    private ObjectValue travelRemove(Iterator<String> iterator, ObjectValue value, boolean remove) {
        String path = iterator.next();
        PointedIndexPathManipulator indexPathManipulator = new PointedIndexPathManipulator(path);

        if (!iterator.hasNext()) {
            if (indexPathManipulator.hasIndex()) {
                PropertyValue property = value.property(indexPathManipulator.getProperty());
                if (property == null || property.isNullValue()) {
                    return value;
                } else if (property.isMultiple()) {
                    List<PropertyValue.Value> array = new ArrayList<>(Arrays.stream(property.multiple()).toList());
                    if (remove) {
                        array.remove(indexPathManipulator.getIndex());
                    } else {
                        array.set(indexPathManipulator.getIndex(), PropertyValue.builder().objectValue((ObjectValue) null).buildValue());
                    }
                    return value.withProperty(indexPathManipulator.getProperty(), PropertyValue.multiple(PropertyValue.Type.OBJECT, array.toArray(new PropertyValue.Value[0])));
                } else {
                    return value;
                }
            } else {
                if (remove) {
                    return value.withoutProperty(path);
                } else {
                    return value.withProperty(path, PropertyValue.builder().objectValue((ObjectValue) null).build());
                }
            }
        }
        if (value == null) {
            return value;
        } else if (value.has(indexPathManipulator.getProperty())) {
            PropertyValue property = value.property(indexPathManipulator.getProperty());
            if (property == null || property.isNullValue()) {
                return value;
            }

            if (indexPathManipulator.hasIndex()) {
                PropertyValue.Value val = property.multiple()[indexPathManipulator.getIndex()];
                if (val.isNull()) {
                    return value;
                }
                List<PropertyValue.Value> list = new ArrayList<>(Arrays.stream(property.multiple()).toList());
                ObjectValue sub = this.travelRemove(iterator, val.objectValue(), remove);
                list.set(indexPathManipulator.getIndex(), PropertyValue.builder().objectValue(sub).buildValue());
                return value.withProperty(indexPathManipulator.getProperty(), PropertyValue.multiple(PropertyValue.Type.OBJECT, list.toArray(new PropertyValue.Value[0])));
            } else {
                PropertyValue.Value val = property.single();
                if (val.isNull()) {
                    return value;
                }
                return value.withProperty(indexPathManipulator.getProperty(), v -> v.objectValue(this.travelRemove(iterator, val.objectValue(), remove)));
            }
        } else {
            return value;
        }
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
            listValue = Arrays.stream(values).toList();
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
