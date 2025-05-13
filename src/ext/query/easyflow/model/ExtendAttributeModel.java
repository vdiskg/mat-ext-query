package ext.query.easyflow.model;

import java.util.Objects;
import java.util.TreeMap;

public class ExtendAttributeModel {

    /**
     * 扩展属性map
     */
    private final TreeMap<String, Object> attrMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    public TreeMap<String, Object> getAttrMap() {
        return this.attrMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ExtendAttributeModel that = (ExtendAttributeModel) o;
        return Objects.equals(this.attrMap, that.attrMap);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.attrMap);
    }
}
