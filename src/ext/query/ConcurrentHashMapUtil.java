package ext.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.mat.snapshot.ISnapshot;
import org.eclipse.mat.snapshot.model.IObject;
import org.eclipse.mat.snapshot.model.NamedReference;

public class ConcurrentHashMapUtil {

    public static List<NamedReference> nodeRefs(IObject mapObject, ISnapshot snapshot) throws Exception {
        IObject tableObject = IObjectUtil.fieldRef(mapObject, "table", snapshot);
        List<NamedReference> outboundReferences = tableObject.getOutboundReferences();
        List<NamedReference> tableNodes = new ArrayList<>();
        for (NamedReference reference : outboundReferences) {
            if (reference.getName().startsWith("[")) {
                tableNodes.add(reference);
            }
        }
        return tableNodes;
    }

    public static void collectNode(IObject nodeObject, ISnapshot snapshot, Map<String, IObject> objectMap) throws Exception {
        String className = nodeObject.getClazz().getName();
        switch (className) {
            case "java.util.concurrent.ConcurrentHashMap$Node": {
                collectLinkedNode(nodeObject, snapshot, objectMap);
            }
            break;
            case  "java.util.concurrent.ConcurrentHashMap$TreeNode": {
                collectTreeNode(nodeObject, snapshot, objectMap);
            }
            break;
            default: {
                throw new IllegalArgumentException("Unknown node type: " + className);
            }
        }
    }

    public static void collectLinkedNode(IObject nodeObject, ISnapshot snapshot, Map<String, IObject> objectMap) throws Exception {
        IObject current = nodeObject;
        while (current != null) {
            // 获取值
            String key = IObjectUtil.fieldString(nodeObject, "key", snapshot);
            IObject valObject = IObjectUtil.fieldRef(current, "val", snapshot);
            if (valObject != null) {
                objectMap.put(key, valObject);
            }

            // 获取下一个节点
            IObject nextObject = IObjectUtil.fieldRef(current, "next", snapshot);
            current = nextObject;
        }
    }

    public static void collectTreeNode(IObject nodeObject, ISnapshot snapshot, Map<String, IObject> objectMap) throws Exception {
        collectTreeNodeInternal(nodeObject, snapshot, objectMap);
    }

    private static void collectTreeNodeInternal(IObject nodeObject, ISnapshot snapshot, Map<String, IObject> objectMap) throws Exception {
        if (nodeObject != null) {
            // 获取当前节点的值
            String key = IObjectUtil.fieldString(nodeObject, "key", snapshot);
            IObject valObject = IObjectUtil.fieldRef(nodeObject, "val", snapshot);
            if (valObject != null) {
                objectMap.put(key, valObject);
            }

            // 处理左子节点
            IObject leftNode = IObjectUtil.fieldRef(nodeObject, "left", snapshot);
            if (leftNode != null) {
                collectTreeNodeInternal(leftNode, snapshot, objectMap);
            }

            // 处理右子节点
            IObject rightNode = IObjectUtil.fieldRef(nodeObject, "right", snapshot);
            if (rightNode != null) {
                collectTreeNodeInternal(rightNode, snapshot, objectMap);
            }
        }
    }
}
