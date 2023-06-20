package com.couchbase.intellij.tree.overview.apis;

public class RAM {

    private Long total;
    private Long quotaTotal;
    private Long quotaUsed;
    private Long used;
    private Long usedByData;
    private Long quotaUsedPerNode;
    private Long quotaTotalPerNode;

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getQuotaTotal() {
        return quotaTotal;
    }

    public void setQuotaTotal(Long quotaTotal) {
        this.quotaTotal = quotaTotal;
    }

    public Long getQuotaUsed() {
        return quotaUsed;
    }

    public void setQuotaUsed(Long quotaUsed) {
        this.quotaUsed = quotaUsed;
    }

    public Long getUsed() {
        return used;
    }

    public void setUsed(Long used) {
        this.used = used;
    }

    public Long getUsedByData() {
        return usedByData;
    }

    public void setUsedByData(Long usedByData) {
        this.usedByData = usedByData;
    }

    public Long getQuotaUsedPerNode() {
        return quotaUsedPerNode;
    }

    public void setQuotaUsedPerNode(Long quotaUsedPerNode) {
        this.quotaUsedPerNode = quotaUsedPerNode;
    }

    public Long getQuotaTotalPerNode() {
        return quotaTotalPerNode;
    }

    public void setQuotaTotalPerNode(Long quotaTotalPerNode) {
        this.quotaTotalPerNode = quotaTotalPerNode;
    }
}
