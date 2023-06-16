package com.couchbase.intellij.tree.overview.apis;

public class BasicBucketStats {

    private Double quotaPercentUsed;
    private Double opsPerSec;
    private Long diskFetches;
    private Long itemCount;
    private Long diskUsed;
    private Long dataUsed;
    private Long memUsed;
    private Integer vbActiveNumNonResident;

    public Double getQuotaPercentUsed() {
        return quotaPercentUsed;
    }

    public void setQuotaPercentUsed(Double quotaPercentUsed) {
        this.quotaPercentUsed = quotaPercentUsed;
    }

    public Double getOpsPerSec() {
        return opsPerSec;
    }

    public void setOpsPerSec(Double opsPerSec) {
        this.opsPerSec = opsPerSec;
    }

    public Long getDiskFetches() {
        return diskFetches;
    }

    public void setDiskFetches(Long diskFetches) {
        this.diskFetches = diskFetches;
    }

    public Long getItemCount() {
        return itemCount;
    }

    public void setItemCount(Long itemCount) {
        this.itemCount = itemCount;
    }

    public Long getDiskUsed() {
        return diskUsed;
    }

    public void setDiskUsed(Long diskUsed) {
        this.diskUsed = diskUsed;
    }

    public Long getDataUsed() {
        return dataUsed;
    }

    public void setDataUsed(Long dataUsed) {
        this.dataUsed = dataUsed;
    }

    public Long getMemUsed() {
        return memUsed;
    }

    public void setMemUsed(Long memUsed) {
        this.memUsed = memUsed;
    }

    public Integer getVbActiveNumNonResident() {
        return vbActiveNumNonResident;
    }

    public void setVbActiveNumNonResident(Integer vbActiveNumNonResident) {
        this.vbActiveNumNonResident = vbActiveNumNonResident;
    }
}
