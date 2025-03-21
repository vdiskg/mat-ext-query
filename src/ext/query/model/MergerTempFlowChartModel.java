package ext.query.model;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class MergerTempFlowChartModel {

    /**
     * 流程图所属项目编号
     */
    private String projectCode;

    /**
     * 流程ID
     */
    private String code;
    /**
     * 克隆后原code
     */
    private String originCode;
    /**
     * 流程版本
     */
    private String version;
    /**
     * 流程名称
     */
    private String name;

    /**
     * 是否为合并后的图
     */
    private Boolean isMerged;

    /**
     * 流程节点
     * 必须是 final 否则浅 clone 会出问题
     * 带Comparator的 treeMap 保证顺序
     */
    private final Map<String, FlowNodeModel> flowNodes = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    private final ExtendAttributeModel extendAttribute = new ExtendAttributeModel();

    public String getProjectCode() {
        return this.projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getOriginCode() {
        return this.originCode;
    }

    public void setOriginCode(String originCode) {
        this.originCode = originCode;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getMerged() {
        return this.isMerged;
    }

    public void setMerged(Boolean merged) {
        this.isMerged = merged;
    }

    public Map<String, FlowNodeModel> getFlowNodes() {
        return this.flowNodes;
    }

    public ExtendAttributeModel getExtendAttribute() {
        return this.extendAttribute;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        MergerTempFlowChartModel that = (MergerTempFlowChartModel) o;
        return Objects.equals(this.isMerged, that.isMerged) && Objects.equals(this.projectCode, that.projectCode) && Objects.equals(this.code, that.code) && Objects.equals(this.originCode, that.originCode)
            && Objects.equals(this.version, that.version) && Objects.equals(this.name, that.name) && Objects.equals(this.flowNodes, that.flowNodes) && Objects.equals(this.extendAttribute,
            that.extendAttribute);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.projectCode, this.code, this.originCode, this.version, this.name, this.isMerged, this.flowNodes, this.extendAttribute);
    }
}
