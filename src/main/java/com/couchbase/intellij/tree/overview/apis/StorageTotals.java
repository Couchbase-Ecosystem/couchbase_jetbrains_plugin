package com.couchbase.intellij.tree.overview.apis;

public class StorageTotals {

    private RAM ram;
    private HDD hdd;

    public RAM getRam() {
        return ram;
    }

    public void setRam(RAM ram) {
        this.ram = ram;
    }

    public HDD getHdd() {
        return hdd;
    }

    public void setHdd(HDD hdd) {
        this.hdd = hdd;
    }
}
