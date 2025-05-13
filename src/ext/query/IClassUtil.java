package ext.query;

import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.mat.SnapshotException;
import org.eclipse.mat.snapshot.ISnapshot;
import org.eclipse.mat.snapshot.model.Field;
import org.eclipse.mat.snapshot.model.IClass;
import org.eclipse.mat.snapshot.model.IObject;
import org.eclipse.mat.snapshot.model.ObjectReference;

public class IClassUtil {

    public static IClass getClassByName(String name, boolean includeSubClasses, ISnapshot snapshot) throws SnapshotException {
        Collection<IClass> classesByName = snapshot.getClassesByName(name, includeSubClasses);
        return getUniqueClass(name, classesByName);
    }

    private static IClass getUniqueClass(String name, Collection<IClass> classesByName) throws SnapshotException {
        if (classesByName.isEmpty()) {
            return null;
        }
        if (classesByName.size() > 1) {
            throw new SnapshotException("Multiple classes found with name: " + name);
        }
        return classesByName.iterator()
            .next();
    }

    public static IClass getClassByName(Pattern name, boolean includeSubClasses, ISnapshot snapshot) throws SnapshotException {
        Collection<IClass> classesByName = snapshot.getClassesByName(name, includeSubClasses);
        return getUniqueClass(name.toString(), classesByName);
    }

    public static IObject getStaticField(IClass iClass, String fieldName, ISnapshot snapshot) throws SnapshotException {
        List<Field> staticFields = iClass.getStaticFields();
        for (Field field : staticFields) {
            if (field.getName()
                .equals(fieldName)) {
                ObjectReference value = (ObjectReference) field.getValue();
                return value.getObject();
            }
        }
        return null;
    }

    public static boolean isAssignableFrom(IClass parentClass, IClass otherClass) throws SnapshotException {
        IClass current = otherClass;
        while (current != null) {
            if (current.equals(parentClass)) {
                return true;
            }
            current = current.getSuperClass();
        }
        return false;
    }
}
