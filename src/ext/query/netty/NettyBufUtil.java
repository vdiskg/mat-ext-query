package ext.query.netty;

import java.text.MessageFormat;
import java.util.Objects;

import org.eclipse.mat.SnapshotException;
import org.eclipse.mat.snapshot.model.IArray;
import org.eclipse.mat.snapshot.model.IClass;
import org.eclipse.mat.snapshot.model.IObject;

public class NettyBufUtil {

    public static Integer getLength(String nettyBufClassName, IObject nettyBufObject) throws SnapshotException {
        return getLengthInternal(nettyBufClassName, nettyBufObject, 0);
    }

    private static Integer getLengthInternal(String nettyBufClassName, IObject nettyBufObject, int depth) throws SnapshotException {
        if (depth > 10) {
            throw new SnapshotException("Too deep recursion");
        }
        switch (nettyBufClassName) {
            case "io.netty.buffer.PooledUnsafeDirectByteBuf":
            case "io.netty.buffer.PooledUnsafeHeapByteBuf":
            case "io.netty.buffer.PooledByteBuf": {
                return getLengthPooledByteBuf(nettyBufObject);
            }
            case "io.netty.buffer.PooledSlicedByteBuf": {
                return getLengthPooledSlicedByteBuf(nettyBufObject);
            }
            case "io.netty.buffer.AdvancedLeakAwareByteBuf":
            case "io.netty.buffer.UnreleasableByteBuf":
            case "io.netty.buffer.WrappedByteBuf": {
                return getLengthWrappedByteBuf(nettyBufObject, depth);
            }
            case "io.netty.buffer.UnpooledByteBufAllocator$InstrumentedUnpooledUnsafeNoCleanerDirectByteBuf":
            case "io.netty.buffer.UnpooledDirectByteBuf": {
                return getLengthUnpooledDirectByteBuf(nettyBufObject);
            }
            case "io.netty.buffer.UnpooledByteBufAllocator$InstrumentedUnpooledUnsafeHeapByteBuf":
            case "io.netty.buffer.UnpooledHeapByteBuf": {
                return getLengthUnpooledHeapByteBuf(nettyBufObject);
            }
            case "io.netty.buffer.ReadOnlyByteBuf": {
                return getLengthReadOnlyByteBuf(nettyBufObject, depth);
            }
            case "io.netty.buffer.EmptyByteBuf": {
                return 0;
            }
            default: {
                throw new SnapshotException(MessageFormat.format("Unsupported netty buffer class: {0}", nettyBufClassName));
            }
        }
    }

    private static Integer getLengthPooledByteBuf(IObject nettyBufObject) throws SnapshotException {
        int refCnt = getRefCnt(nettyBufObject);
        if (refCnt > 0) {
            Integer length = (Integer) nettyBufObject.resolveValue("length");
            Objects.requireNonNull(length);
            return length;
        } else {
            return null;
        }
    }

    private static int getRefCnt(IObject nettyBufObject) throws SnapshotException {
        Integer refCnt = (Integer) nettyBufObject.resolveValue("refCnt");
        if (refCnt == null) {
            throw new SnapshotException("refCnt is null, " + nettyBufObject.getClazz()
                .getName());
        }
        return refCnt;
    }

    private static Integer getLengthPooledSlicedByteBuf(IObject nettyBufObject) throws SnapshotException {
        int refCnt = getRefCnt(nettyBufObject);
        if (refCnt > 0) {
            Integer maxCapacity = (Integer) nettyBufObject.resolveValue("maxCapacity");
            Objects.requireNonNull(maxCapacity);
            return maxCapacity;
        } else {
            return null;
        }
    }

    private static Integer getLengthWrappedByteBuf(IObject nettyBufObject, int depth) throws SnapshotException {
        IObject buf = (IObject) nettyBufObject.resolveValue("buf");
        Objects.requireNonNull(buf);
        IClass clazz = buf.getClazz();
        String bufClassName = clazz.getName();
        return getLengthInternal(bufClassName, buf, depth + 1);
    }

    private static Integer getLengthUnpooledDirectByteBuf(IObject nettyBufObject) throws SnapshotException {
        int refCnt = getRefCnt(nettyBufObject);
        if (refCnt > 0) {
            Integer capacity = (Integer) nettyBufObject.resolveValue("capacity");
            Objects.requireNonNull(capacity);
            return capacity;
        } else {
            return null;
        }
    }

    private static Integer getLengthUnpooledHeapByteBuf(IObject nettyBufObject) throws SnapshotException {
        int refCnt = getRefCnt(nettyBufObject);
        if (refCnt > 0) {
            IArray iArray = (IArray) nettyBufObject.resolveValue("array");
            Objects.requireNonNull(iArray);
            return iArray.getLength();
        } else {
            return null;
        }
    }

    private static Integer getLengthReadOnlyByteBuf(IObject nettyBufObject, int depth) throws SnapshotException {
        IObject buf = (IObject) nettyBufObject.resolveValue("buffer");
        Objects.requireNonNull(buf);
        IClass clazz = buf.getClazz();
        String bufClassName = clazz.getName();
        return getLengthInternal(bufClassName, buf, depth + 1);
    }
}
