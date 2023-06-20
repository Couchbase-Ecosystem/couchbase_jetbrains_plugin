package com.couchbase.intellij.tree.overview.apis;

import java.util.List;

public class BucketOverview {

    private String name;
    private String nodeLocator;
    private String bucketType;
    private String storageBackend;
    private String uuid;
    private String uri;
    private String streamingUri;
    private String bucketCapabilitiesVer;
    private List<String> bucketCapabilities;
    private Boolean autoCompactionSettings;
    private Boolean replicaIndex;
    private Integer replicaNumber;
    private Integer threadsNumber;
    private String evictionPolicy;
    private String durabilityMinLevel;
    private Boolean pitrEnabled;
    private Long pitrGranularity;
    private Long pitrMaxHistoryAge;
    private String conflictResolutionType;
    private Long maxTTL;
    private String compressionMode;
    private BucketQuota quota;
    private BasicBucketStats basicStats;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNodeLocator() {
        return nodeLocator;
    }

    public void setNodeLocator(String nodeLocator) {
        this.nodeLocator = nodeLocator;
    }

    public String getBucketType() {
        return bucketType;
    }

    public void setBucketType(String bucketType) {
        this.bucketType = bucketType;
    }

    public String getStorageBackend() {
        return storageBackend;
    }

    public void setStorageBackend(String storageBackend) {
        this.storageBackend = storageBackend;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getStreamingUri() {
        return streamingUri;
    }

    public void setStreamingUri(String streamingUri) {
        this.streamingUri = streamingUri;
    }

    public String getBucketCapabilitiesVer() {
        return bucketCapabilitiesVer;
    }

    public void setBucketCapabilitiesVer(String bucketCapabilitiesVer) {
        this.bucketCapabilitiesVer = bucketCapabilitiesVer;
    }

    public List<String> getBucketCapabilities() {
        return bucketCapabilities;
    }

    public void setBucketCapabilities(List<String> bucketCapabilities) {
        this.bucketCapabilities = bucketCapabilities;
    }

    public Boolean getAutoCompactionSettings() {
        return autoCompactionSettings;
    }

    public void setAutoCompactionSettings(Boolean autoCompactionSettings) {
        this.autoCompactionSettings = autoCompactionSettings;
    }

    public Boolean getReplicaIndex() {
        return replicaIndex;
    }

    public void setReplicaIndex(Boolean replicaIndex) {
        this.replicaIndex = replicaIndex;
    }

    public Integer getReplicaNumber() {
        return replicaNumber;
    }

    public void setReplicaNumber(Integer replicaNumber) {
        this.replicaNumber = replicaNumber;
    }

    public Integer getThreadsNumber() {
        return threadsNumber;
    }

    public void setThreadsNumber(Integer threadsNumber) {
        this.threadsNumber = threadsNumber;
    }

    public String getEvictionPolicy() {
        return evictionPolicy;
    }

    public void setEvictionPolicy(String evictionPolicy) {
        this.evictionPolicy = evictionPolicy;
    }

    public String getDurabilityMinLevel() {
        return durabilityMinLevel;
    }

    public void setDurabilityMinLevel(String durabilityMinLevel) {
        this.durabilityMinLevel = durabilityMinLevel;
    }

    public Boolean getPitrEnabled() {
        return pitrEnabled;
    }

    public void setPitrEnabled(Boolean pitrEnabled) {
        this.pitrEnabled = pitrEnabled;
    }

    public Long getPitrGranularity() {
        return pitrGranularity;
    }

    public void setPitrGranularity(Long pitrGranularity) {
        this.pitrGranularity = pitrGranularity;
    }

    public Long getPitrMaxHistoryAge() {
        return pitrMaxHistoryAge;
    }

    public void setPitrMaxHistoryAge(Long pitrMaxHistoryAge) {
        this.pitrMaxHistoryAge = pitrMaxHistoryAge;
    }

    public String getConflictResolutionType() {
        return conflictResolutionType;
    }

    public void setConflictResolutionType(String conflictResolutionType) {
        this.conflictResolutionType = conflictResolutionType;
    }

    public Long getMaxTTL() {
        return maxTTL;
    }

    public void setMaxTTL(Long maxTTL) {
        this.maxTTL = maxTTL;
    }

    public String getCompressionMode() {
        return compressionMode;
    }

    public void setCompressionMode(String compressionMode) {
        this.compressionMode = compressionMode;
    }

    public BucketQuota getQuota() {
        return quota;
    }

    public void setQuota(BucketQuota quota) {
        this.quota = quota;
    }

    public BasicBucketStats getBasicStats() {
        return basicStats;
    }

    public void setBasicStats(BasicBucketStats basicStats) {
        this.basicStats = basicStats;
    }
}
