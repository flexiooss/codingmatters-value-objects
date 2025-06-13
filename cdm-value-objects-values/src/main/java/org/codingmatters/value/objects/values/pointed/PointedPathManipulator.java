package org.codingmatters.value.objects.values.pointed;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PointedPathManipulator {
    private final String pointedPath;

    public PointedPathManipulator(String pointedPath) {
        this.pointedPath = pointedPath;
    }

    public List<String> getPath() {
        if (pointedPath == null || pointedPath.isEmpty() || pointedPath.isBlank()) {
            return List.of();
        }
        String[] split = pointedPath.split("\\.");
        if (split.length == 0) {
            String[] array = new String[1];
            array[0] = pointedPath;
            split = array;
        }
        return Arrays.stream(split).toList();
    }

    public List<String> getPathIndexSeparated() {
        List<String> res = new ArrayList<>();
        List<String> path = this.getPath();
        for (String val : path) {
            PointedIndexPathManipulator pathManipulator = new PointedIndexPathManipulator(val);
            if (pathManipulator.hasIndex()) {
                res.add(pathManipulator.getProperty());
                res.add(String.format("[%d]", pathManipulator.getIndex()));
            } else {
                res.add(val);
            }
        }
        return res;
    }
}
