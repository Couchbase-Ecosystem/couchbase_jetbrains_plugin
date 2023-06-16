package com.couchbase.intellij.tree.overview.apis;

public class HDD {

    private Long total;
    private Long quotaTotal;
    private Long used;
    private Long usedByData;
    private Long free;

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

    public Long getFree() {
        return free;
    }

    public void setFree(Long free) {
        this.free = free;
    }
}
