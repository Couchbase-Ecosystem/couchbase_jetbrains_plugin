package com.couchbase.intellij.tree.overview.apis;

public class AutoCompactionSettings {

    private Boolean parallelDBAndViewCompaction;
    private Long magmaFragmentationPercentage;
    private Fragmentation databaseFragmentationThreshold;
    private Fragmentation viewFragmentationThreshold;
    private String indexCompactionMode;
    private Fragmentation indexFragmentationThreshold;

    public Boolean getParallelDBAndViewCompaction() {
        return parallelDBAndViewCompaction;
    }

    public void setParallelDBAndViewCompaction(Boolean parallelDBAndViewCompaction) {
        this.parallelDBAndViewCompaction = parallelDBAndViewCompaction;
    }

    public Long getMagmaFragmentationPercentage() {
        return magmaFragmentationPercentage;
    }

    public void setMagmaFragmentationPercentage(Long magmaFragmentationPercentage) {
        this.magmaFragmentationPercentage = magmaFragmentationPercentage;
    }

    public Fragmentation getDatabaseFragmentationThreshold() {
        return databaseFragmentationThreshold;
    }

    public void setDatabaseFragmentationThreshold(Fragmentation databaseFragmentationThreshold) {
        this.databaseFragmentationThreshold = databaseFragmentationThreshold;
    }

    public Fragmentation getViewFragmentationThreshold() {
        return viewFragmentationThreshold;
    }

    public void setViewFragmentationThreshold(Fragmentation viewFragmentationThreshold) {
        this.viewFragmentationThreshold = viewFragmentationThreshold;
    }

    public String getIndexCompactionMode() {
        return indexCompactionMode;
    }

    public void setIndexCompactionMode(String indexCompactionMode) {
        this.indexCompactionMode = indexCompactionMode;
    }

    public Fragmentation getIndexFragmentationThreshold() {
        return indexFragmentationThreshold;
    }

    public void setIndexFragmentationThreshold(Fragmentation indexFragmentationThreshold) {
        this.indexFragmentationThreshold = indexFragmentationThreshold;
    }
}
