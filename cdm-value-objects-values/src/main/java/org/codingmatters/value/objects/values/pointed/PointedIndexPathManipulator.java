package org.codingmatters.value.objects.values.pointed;

public class PointedIndexPathManipulator {
    private final String path;

    public PointedIndexPathManipulator(String path) {
        this.path = path;
    }

    public boolean hasIndex() {
        if (path == null || path.isEmpty() || path.isBlank()) {
            return false;
        }
        return path.contains("[") && path.endsWith("]");
    }

    public int getIndex() {
        if (this.path == null) {
            return 0;
        }
        String index = path.substring(path.indexOf("[") + 1, path.length() - 1);
        return Integer.parseInt(index);
    }

    public String getProperty() {
        if (this.path == null) {
            return null;
        }
        if (this.hasIndex()) {
            return this.path.substring(0, path.indexOf("["));
        }
        return this.path;
    }
}
