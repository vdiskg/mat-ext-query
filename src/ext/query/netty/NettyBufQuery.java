package ext.query.netty;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import org.eclipse.mat.SnapshotException;
import org.eclipse.mat.query.IQuery;
import org.eclipse.mat.query.IResult;
import org.eclipse.mat.query.annotations.Argument;
import org.eclipse.mat.query.annotations.CommandName;
import org.eclipse.mat.query.annotations.Name;
import org.eclipse.mat.query.results.CompositeResult;
import org.eclipse.mat.query.results.TextResult;
import org.eclipse.mat.snapshot.ISnapshot;
import org.eclipse.mat.snapshot.model.IClass;
import org.eclipse.mat.snapshot.model.IObject;
import org.eclipse.mat.util.IProgressListener;

import ext.query.IClassUtil;
import ext.query.IObjectUtil;

@CommandName("ext_NettyBufQuery")
@Name("ext_NettyBufQuery")
public class NettyBufQuery implements IQuery {

    @Argument
    public ISnapshot snapshot;

    public NettyBufQuery() {
    }

    @Override
    public IResult execute(IProgressListener listener) throws Exception {
        IClass platformDependentClass = IClassUtil.getClassByName("io.netty.util.internal.PlatformDependent", false, snapshot);
        IObject directMemoryCounter = IClassUtil.getStaticField(platformDependentClass, "DIRECT_MEMORY_COUNTER", snapshot);
        Objects.requireNonNull(directMemoryCounter, "PlatformDependent#DIRECT_MEMORY_COUNTER");
        Long directMemoryCounterValue = (Long) directMemoryCounter.resolveValue("value");
        Collection<IClass> nettyBufSubClasses = snapshot.getClassesByName(Pattern.compile("io.netty.buffer.ByteBuf"), true);
        Map<String, Collection<IObject>> nettyBufObjectMap = new LinkedHashMap<>();
        for (IClass nettyBufSubClass : nettyBufSubClasses) {
            Collection<IObject> objects = IObjectUtil.objects(nettyBufSubClass.getObjectIds(), snapshot);
            nettyBufObjectMap.put(nettyBufSubClass.getName(), objects);
        }

        CompositeResult compositeResult = new CompositeResult();

        String text2 = this.toResultText2(nettyBufObjectMap, directMemoryCounterValue, listener);
        TextResult result2 = new TextResult(text2, false);
        compositeResult.addResult("result2", result2);

        return compositeResult;
    }

    private String toResultText2(Map<String, Collection<IObject>> nettyBufObjectMap, Long directMemoryCounterValue, IProgressListener listener) throws SnapshotException {
        listener.beginTask("gen result", nettyBufObjectMap.size());
        StringBuilder sb = new StringBuilder();
        sb.append(MessageFormat.format("Found {0} values\n", nettyBufObjectMap.size()));
        sb.append(MessageFormat.format("Used Direct Memory: {0}\n", directMemoryCounterValue));
        List<Map.Entry<String, Collection<IObject>>> classes = new ArrayList<>(nettyBufObjectMap.entrySet());
        Comparator<Map.Entry<String, Collection<IObject>>> comparator = Comparator.comparing(entry -> entry.getValue()
            .size());
        classes.sort(comparator.reversed());
        int i = 0;
        for (Map.Entry<String, Collection<IObject>> entry : classes) {
            i++;
            listener.worked(i);
            String nettyBufClassName = entry.getKey();
            Collection<IObject> nettyBufObjects = entry.getValue();
            int totalLength = 0;
            int totalAlive = 0;
            int totalAliveLength = 0;
            for (IObject nettyBufObject : nettyBufObjects) {
                Integer length = NettyBufUtil.getLength(nettyBufClassName, nettyBufObject, false);
                Objects.requireNonNull(length);
                totalLength += length;

                Integer aliveLength = NettyBufUtil.getLength(nettyBufClassName, nettyBufObject, true);
                if (aliveLength != null) {
                    totalAlive++;
                    totalAliveLength += aliveLength;
                }
            }
            sb.append(MessageFormat.format("{0}: [count:{1}][length:{2}][alive:{3}][aliveLength:{4}]\n", nettyBufClassName, nettyBufObjects.size(), totalLength, totalAlive,
                totalAliveLength));
        }

        return sb.toString();
    }

}
