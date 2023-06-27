package com.couchbase.intellij.tree.overview.apis;

import java.math.BigInteger;

public class SystemStats {

    private Double cpu_utilization_rate;
    private Double cpu_stolen_rate;
    private Double swap_total;
    private Double swap_used;
    private Long mem_total;
    private Long mem_free;
    private Long mem_limit;
    private Long cpu_cores_available;
    private BigInteger allocstall;

    public Double getCpu_utilization_rate() {
        return cpu_utilization_rate;
    }

    public void setCpu_utilization_rate(Double cpu_utilization_rate) {
        this.cpu_utilization_rate = cpu_utilization_rate;
    }

    public Double getCpu_stolen_rate() {
        return cpu_stolen_rate;
    }

    public void setCpu_stolen_rate(Double cpu_stolen_rate) {
        this.cpu_stolen_rate = cpu_stolen_rate;
    }

    public Double getSwap_total() {
        return swap_total;
    }

    public void setSwap_total(Double swap_total) {
        this.swap_total = swap_total;
    }

    public Double getSwap_used() {
        return swap_used;
    }

    public void setSwap_used(Double swap_used) {
        this.swap_used = swap_used;
    }

    public Long getMem_total() {
        return mem_total;
    }

    public void setMem_total(Long mem_total) {
        this.mem_total = mem_total;
    }

    public Long getMem_free() {
        return mem_free;
    }

    public void setMem_free(Long mem_free) {
        this.mem_free = mem_free;
    }

    public Long getMem_limit() {
        return mem_limit;
    }

    public void setMem_limit(Long mem_limit) {
        this.mem_limit = mem_limit;
    }

    public Long getCpu_cores_available() {
        return cpu_cores_available;
    }

    public void setCpu_cores_available(Long cpu_cores_available) {
        this.cpu_cores_available = cpu_cores_available;
    }

    public BigInteger getAllocstall() {
        return allocstall;
    }

    public void setAllocstall(BigInteger allocstall) {
        this.allocstall = allocstall;
    }
}
