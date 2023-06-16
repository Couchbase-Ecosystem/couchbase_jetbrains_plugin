package com.couchbase.intellij.tree.overview.apis;

import java.util.List;

public class ServerOverview {

    private String name;
    private List<CBNode> nodes;
    private List<BucketName> bucketNames;
    private String rebalanceStatus;
    private AutoCompactionSettings autoCompactionSettings;
    private Boolean balanced;
    private Long memoryQuota;
    private Long indexMemoryQuota;
    private Long ftsMemoryQuota;
    private Long cbasMemoryQuota;
    private Long eventingMemoryQuota;
    private StorageTotals storageTotals;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CBNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<CBNode> nodes) {
        this.nodes = nodes;
    }

    public List<BucketName> getBucketNames() {
        return bucketNames;
    }

    public void setBucketNames(List<BucketName> bucketNames) {
        this.bucketNames = bucketNames;
    }

    public String getRebalanceStatus() {
        return rebalanceStatus;
    }

    public void setRebalanceStatus(String rebalanceStatus) {
        this.rebalanceStatus = rebalanceStatus;
    }

    public AutoCompactionSettings getAutoCompactionSettings() {
        return autoCompactionSettings;
    }

    public void setAutoCompactionSettings(AutoCompactionSettings autoCompactionSettings) {
        this.autoCompactionSettings = autoCompactionSettings;
    }

    public Boolean getBalanced() {
        return balanced;
    }

    public void setBalanced(Boolean balanced) {
        this.balanced = balanced;
    }

    public Long getMemoryQuota() {
        return memoryQuota;
    }

    public void setMemoryQuota(Long memoryQuota) {
        this.memoryQuota = memoryQuota;
    }

    public Long getIndexMemoryQuota() {
        return indexMemoryQuota;
    }

    public void setIndexMemoryQuota(Long indexMemoryQuota) {
        this.indexMemoryQuota = indexMemoryQuota;
    }

    public Long getFtsMemoryQuota() {
        return ftsMemoryQuota;
    }

    public void setFtsMemoryQuota(Long ftsMemoryQuota) {
        this.ftsMemoryQuota = ftsMemoryQuota;
    }

    public Long getCbasMemoryQuota() {
        return cbasMemoryQuota;
    }

    public void setCbasMemoryQuota(Long cbasMemoryQuota) {
        this.cbasMemoryQuota = cbasMemoryQuota;
    }

    public Long getEventingMemoryQuota() {
        return eventingMemoryQuota;
    }

    public void setEventingMemoryQuota(Long eventingMemoryQuota) {
        this.eventingMemoryQuota = eventingMemoryQuota;
    }

    public StorageTotals getStorageTotals() {
        return storageTotals;
    }

    public void setStorageTotals(StorageTotals storageTotals) {
        this.storageTotals = storageTotals;
    }
}
