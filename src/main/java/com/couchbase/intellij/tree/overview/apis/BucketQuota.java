package com.couchbase.intellij.tree.overview.apis;

public class BucketQuota {

    private Long ram;
    private Long rawRAM;

    public Long getRam() {
        return ram;
    }

    public void setRam(Long ram) {
        this.ram = ram;
    }

    public Long getRawRAM() {
        return rawRAM;
    }

    public void setRawRAM(Long rawRAM) {
        this.rawRAM = rawRAM;
    }
}
