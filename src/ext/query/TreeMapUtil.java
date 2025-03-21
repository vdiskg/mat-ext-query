package ext.query;

import java.util.Map;

import org.eclipse.mat.snapshot.ISnapshot;
import org.eclipse.mat.snapshot.model.IObject;

public class TreeMapUtil {

    public static void collectNode(IObject nodeObject, ISnapshot snapshot, Map<String, IObject> objectMap) throws Exception {
        collectNodeInternal(nodeObject, snapshot, objectMap);
    }

    private static void collectNodeInternal(IObject nodeObject, ISnapshot snapshot, Map<String, IObject> objectMap) throws Exception {
        if (nodeObject == null) {
            return;
        }
        String key = IObjectUtil.fieldString(nodeObject, "key", snapshot);
        IObject valObject = IObjectUtil.fieldRef(nodeObject, "value", snapshot);
        if (valObject != null) {
            objectMap.put(key, valObject);
        }
        IObject leftNode = IObjectUtil.fieldRef(nodeObject, "left", snapshot);
        if (leftNode != null) {
            collectNodeInternal(leftNode, snapshot, objectMap);
        }
        IObject rightNode = IObjectUtil.fieldRef(nodeObject, "right", snapshot);
        if (rightNode != null) {
            collectNodeInternal(rightNode, snapshot, objectMap);
        }
    }
}
