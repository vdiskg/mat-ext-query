package ext.query;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.eclipse.mat.SnapshotException;
import org.eclipse.mat.snapshot.ISnapshot;
import org.eclipse.mat.snapshot.model.IObject;
import org.eclipse.mat.snapshot.model.NamedReference;

public class IObjectUtil {

    public static IObject fieldRef(IObject obj, String fieldName, ISnapshot snapshot) throws Exception {
        if (obj == null) {
            return null;
        }
        List<NamedReference> referents =  obj.getOutboundReferences();
        for (NamedReference referent : referents) {
            if (Objects.equals(fieldName, referent.getName())) {
                int objectId = referent.getObjectId();
                return snapshot.getObject(objectId);
            }
        }
        return null;
    }

    public static String fieldString(IObject obj, String fieldName, ISnapshot snapshot) throws Exception {
        IObject field = fieldRef(obj, fieldName, snapshot);
        return field != null ? field.getClassSpecificName() : null;
    }

    public static int[] objectIds(Collection<IObject> objects) {
        int[] ids = new int[objects.size()];
        int i = 0;
        for (IObject obj : objects) {
            ids[i] = obj.getObjectId();
            i++;
        }
        return ids;
    }

    public static int[] objectIds(IObject... objects) {
       return objectIds(Arrays.asList(objects));
    }
}
