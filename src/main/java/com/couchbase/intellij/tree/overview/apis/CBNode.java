package com.couchbase.intellij.tree.overview.apis;

import java.util.List;
import java.util.Map;

public class CBNode {

    private String clusterMembership;
    private String recoveryType;
    private String status;
    private String otpNode;
    private boolean thisNode;
    private String hostname;
    private String nodeUUID;
    private Long clusterCompatibility;
    private String version;
    private String os;
    private Long cpuCount;
    private Map<String, String> ports;
    private List<String> services;
    private boolean nodeEncryption;
    private boolean addressFamilyOnly;
    private String configuredHostname;
    private String addressFamily;
    private String serverGroup;
    private String couchApiBase;
    private String couchApiBaseHTTPS;
    private Long nodeHash;
    private SystemStats systemStats;
    private InterestingStats interestingStats;
    private String uptime;
    private Long memoryTotal;
    private Long memoryFree;
    private Long mcdMemoryReserved;
    private Long mcdMemoryAllocated;

    public String getClusterMembership() {
        return clusterMembership;
    }

    public void setClusterMembership(String clusterMembership) {
        this.clusterMembership = clusterMembership;
    }

    public String getRecoveryType() {
        return recoveryType;
    }

    public void setRecoveryType(String recoveryType) {
        this.recoveryType = recoveryType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOtpNode() {
        return otpNode;
    }

    public void setOtpNode(String otpNode) {
        this.otpNode = otpNode;
    }

    public boolean isThisNode() {
        return thisNode;
    }

    public void setThisNode(boolean thisNode) {
        this.thisNode = thisNode;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getNodeUUID() {
        return nodeUUID;
    }

    public void setNodeUUID(String nodeUUID) {
        this.nodeUUID = nodeUUID;
    }

    public Long getClusterCompatibility() {
        return clusterCompatibility;
    }

    public void setClusterCompatibility(Long clusterCompatibility) {
        this.clusterCompatibility = clusterCompatibility;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public Long getCpuCount() {
        return cpuCount;
    }

    public void setCpuCount(Long cpuCount) {
        this.cpuCount = cpuCount;
    }

    public Map<String, String> getPorts() {
        return ports;
    }

    public void setPorts(Map<String, String> ports) {
        this.ports = ports;
    }

    public List<String> getServices() {
        return services;
    }

    public void setServices(List<String> services) {
        this.services = services;
    }

    public boolean isNodeEncryption() {
        return nodeEncryption;
    }

    public void setNodeEncryption(boolean nodeEncryption) {
        this.nodeEncryption = nodeEncryption;
    }

    public boolean isAddressFamilyOnly() {
        return addressFamilyOnly;
    }

    public void setAddressFamilyOnly(boolean addressFamilyOnly) {
        this.addressFamilyOnly = addressFamilyOnly;
    }

    public String getConfiguredHostname() {
        return configuredHostname;
    }

    public void setConfiguredHostname(String configuredHostname) {
        this.configuredHostname = configuredHostname;
    }

    public String getAddressFamily() {
        return addressFamily;
    }

    public void setAddressFamily(String addressFamily) {
        this.addressFamily = addressFamily;
    }

    public String getServerGroup() {
        return serverGroup;
    }

    public void setServerGroup(String serverGroup) {
        this.serverGroup = serverGroup;
    }

    public String getCouchApiBase() {
        return couchApiBase;
    }

    public void setCouchApiBase(String couchApiBase) {
        this.couchApiBase = couchApiBase;
    }

    public String getCouchApiBaseHTTPS() {
        return couchApiBaseHTTPS;
    }

    public void setCouchApiBaseHTTPS(String couchApiBaseHTTPS) {
        this.couchApiBaseHTTPS = couchApiBaseHTTPS;
    }

    public Long getNodeHash() {
        return nodeHash;
    }

    public void setNodeHash(Long nodeHash) {
        this.nodeHash = nodeHash;
    }

    public SystemStats getSystemStats() {
        return systemStats;
    }

    public void setSystemStats(SystemStats systemStats) {
        this.systemStats = systemStats;
    }

    public InterestingStats getInterestingStats() {
        return interestingStats;
    }

    public void setInterestingStats(InterestingStats interestingStats) {
        this.interestingStats = interestingStats;
    }

    public String getUptime() {
        return uptime;
    }

    public void setUptime(String uptime) {
        this.uptime = uptime;
    }

    public Long getMemoryTotal() {
        return memoryTotal;
    }

    public void setMemoryTotal(Long memoryTotal) {
        this.memoryTotal = memoryTotal;
    }

    public Long getMemoryFree() {
        return memoryFree;
    }

    public void setMemoryFree(Long memoryFree) {
        this.memoryFree = memoryFree;
    }

    public Long getMcdMemoryReserved() {
        return mcdMemoryReserved;
    }

    public void setMcdMemoryReserved(Long mcdMemoryReserved) {
        this.mcdMemoryReserved = mcdMemoryReserved;
    }

    public Long getMcdMemoryAllocated() {
        return mcdMemoryAllocated;
    }

    public void setMcdMemoryAllocated(Long mcdMemoryAllocated) {
        this.mcdMemoryAllocated = mcdMemoryAllocated;
    }
}
