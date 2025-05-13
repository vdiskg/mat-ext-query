package ext.query.easyflow;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.mat.internal.snapshot.inspections.DominatorQuery;
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
import org.eclipse.mat.snapshot.model.NamedReference;
import org.eclipse.mat.util.IProgressListener;

import ext.query.ConcurrentHashMapUtil;
import ext.query.IObjectUtil;
import ext.query.TreeMapUtil;
import ext.query.easyflow.model.MergerTempFlowChartModel;

@CommandName("ext_FlowChartQuery")
@Name("ext_FlowChartQuery")
public class FlowChartQuery implements IQuery {
    @Argument
    public ISnapshot snapshot;

	public FlowChartQuery() {
	}

	@Override
	public IResult execute(IProgressListener listener) throws Exception {
        IObject managerObj = this.getSingletonReferent("hsjry.easyflow.common.flow.FlowChartManager", snapshot);
        IObject mapObject = IObjectUtil.fieldRef(managerObj, "flowChartMap", snapshot);

        // 获取 table 数组中的所有引用
        List<NamedReference> nodeRefs = ConcurrentHashMapUtil.nodeRefs(mapObject, snapshot);

        Map<String, IObject> objectMap = this.scanNodes(nodeRefs, listener);

        Map<String, List<IObject>> valueObjectGroupByCode = this.groupByCode(objectMap, listener);

        List<IObject> firstValueObjects = valueObjectGroupByCode.getOrDefault("EI_interfaceUrlTestImpl.interfaceUrlTest006", Collections.emptyList());

        List<MergerTempFlowChartModel> models = this.toModels(firstValueObjects);
        Set<MergerTempFlowChartModel> modelSet = new LinkedHashSet<>(models);

        CompositeResult compositeResult = new CompositeResult();
        String text = this.toResultText(objectMap, valueObjectGroupByCode, listener);
        TextResult result = new TextResult(text, false);
        compositeResult.addResult("result", result);

        DominatorQuery.Tree managerResult = DominatorQuery.Factory.create(snapshot, IObjectUtil.objectIds(managerObj), listener);
        compositeResult.addResult("managerResult", managerResult);

        DominatorQuery.Tree firstValueResult = DominatorQuery.Factory.create(snapshot, IObjectUtil.objectIds(firstValueObjects), listener);
        compositeResult.addResult("firstValueResult", firstValueResult);
        return compositeResult;
    }

    private String toResultText(Map<String, IObject> objectMap, Map<String, List<IObject>> valueObjectGroupByCode, IProgressListener listener) {

        listener.beginTask("gen result", valueObjectGroupByCode.size());
        StringBuilder sb = new StringBuilder();
        sb.append(MessageFormat.format("Found {0} values\n", objectMap.size()));
        int i = 0;
        for (Map.Entry<String, List<IObject>> entry : valueObjectGroupByCode.entrySet()) {
            i ++;
            listener.worked(i);
            sb.append(MessageFormat.format("{0}: {1} \n", entry.getKey(), entry.getValue().size()));
        }
        return sb.toString();
    }

    private Map<String, IObject> scanNodes(List<NamedReference> nodeRefs, IProgressListener listener) throws Exception {
        Map<String, IObject> objectMap = new LinkedHashMap<>();
        listener.beginTask("scan nodeRefs", nodeRefs.size());
        int i = 0;
        // 遍历 table 数组中的每个位置
        for (NamedReference entry : nodeRefs) {
            i ++;
            if (i % 1000 == 0) {
                listener.worked(i);
            }
            IObject nodeObject = snapshot.getObject(entry.getObjectId());
            ConcurrentHashMapUtil.collectNode(nodeObject, snapshot, objectMap);
        }
        return objectMap;
    }

    private Map<String, List<IObject>> groupByCode(Map<String, IObject> objectMap, IProgressListener listener) throws Exception {
        Map<String, List<IObject>> valueObjectGroupByCode = new LinkedHashMap<>();
        listener.beginTask("scan values", objectMap.size());
        int i = 0;
        for (IObject valueObject : objectMap.values()) {
        	i ++;
            if (i % 1000 == 0) {
                listener.worked(i);
            }
            String codeText = IObjectUtil.fieldString(valueObject, "code", snapshot);
            List<IObject> group = valueObjectGroupByCode.computeIfAbsent(codeText, key -> new ArrayList<>());
            group.add(valueObject);
        }
        return valueObjectGroupByCode;
    }

    private IObject getSingletonReferent(String className, ISnapshot snapshot) throws Exception {
        Collection<IClass> classes = snapshot.getClassesByName(className, false);
        for (IClass clazz : classes) {
            int[] instances = clazz.getObjectIds();
            for (int objectId : instances) {
                IObject obj = snapshot.getObject(objectId);
                return obj;
            }
        }
        return null;
    }

    private NamedReference getFieldReference(String fieldName, IObject obj) {
        if (obj == null) {
            return null;
        }
    	List<NamedReference> referents =  obj.getOutboundReferences();
    	for (NamedReference referent : referents) {
    		if (Objects.equals(fieldName, referent.getName())) {
    			return referent;
    		}
    	}
    	return null;
    }

    private List<MergerTempFlowChartModel> toModels(List<IObject> firstValueObjects) throws Exception {
        List<MergerTempFlowChartModel> models = new ArrayList<>();
        for (IObject valueObject : firstValueObjects) {
            MergerTempFlowChartModel model = this.toModel(valueObject);
            models.add(model);
        }
        return models;
    }

    private MergerTempFlowChartModel toModel(IObject valueObject) throws Exception {
        MergerTempFlowChartModel model = new MergerTempFlowChartModel();
        String projectCode  = IObjectUtil.fieldString(valueObject, "projectCode", snapshot);
        model.setProjectCode(projectCode);
        String code = IObjectUtil.fieldString(valueObject, "code", snapshot);
        model.setCode(code);
        String originCode = IObjectUtil.fieldString(valueObject, "originCode", snapshot);
        model.setOriginCode(originCode);
        String version = IObjectUtil.fieldString(valueObject, "version", snapshot);
        model.setVersion(version);
        String name = IObjectUtil.fieldString(valueObject, "name", snapshot);
        model.setName(name);
        Boolean isMerged = (Boolean) valueObject.resolveValue("isMerged");
        model.setMerged(isMerged);

        IObject extendAttribute = IObjectUtil.fieldRef(valueObject, "extendAttribute", snapshot);
        if (extendAttribute != null) {
            IObject attrMap = IObjectUtil.fieldRef(extendAttribute, "attrMap", snapshot);
            IObject root = IObjectUtil.fieldRef(attrMap, "root", snapshot);
            TreeMap<String, IObject> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            TreeMapUtil.collectNode(root, snapshot, map);
            model.getExtendAttribute().getAttrMap().putAll(map);
        }
        return model;
    }
}
