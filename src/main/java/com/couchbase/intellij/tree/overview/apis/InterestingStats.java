package com.couchbase.intellij.tree.overview.apis;

public class InterestingStats {
    private Double cmd_get;
    private Long couch_docs_actual_disk_size;
    private Long couch_docs_data_size;
    private Long couch_spatial_data_size;
    private Long couch_spatial_disk_size;
    private Long couch_views_actual_disk_size;
    private Long couch_views_data_size;
    private Long curr_items;
    private Long curr_items_tot;
    private Long ep_bg_fetched;
    private Double get_hits;
    private Long index_data_size;
    private Long index_disk_size;
    private Long mem_used;
    private Double ops;
    private Long vb_active_num_non_resident;
    private Long vb_replica_curr_items;

    public Double getCmd_get() {
        return cmd_get;
    }

    public void setCmd_get(Double cmd_get) {
        this.cmd_get = cmd_get;
    }

    public Long getCouch_docs_actual_disk_size() {
        return couch_docs_actual_disk_size;
    }

    public void setCouch_docs_actual_disk_size(Long couch_docs_actual_disk_size) {
        this.couch_docs_actual_disk_size = couch_docs_actual_disk_size;
    }

    public Long getCouch_docs_data_size() {
        return couch_docs_data_size;
    }

    public void setCouch_docs_data_size(Long couch_docs_data_size) {
        this.couch_docs_data_size = couch_docs_data_size;
    }

    public Long getCouch_spatial_data_size() {
        return couch_spatial_data_size;
    }

    public void setCouch_spatial_data_size(Long couch_spatial_data_size) {
        this.couch_spatial_data_size = couch_spatial_data_size;
    }

    public Long getCouch_spatial_disk_size() {
        return couch_spatial_disk_size;
    }

    public void setCouch_spatial_disk_size(Long couch_spatial_disk_size) {
        this.couch_spatial_disk_size = couch_spatial_disk_size;
    }

    public Long getCouch_views_actual_disk_size() {
        return couch_views_actual_disk_size;
    }

    public void setCouch_views_actual_disk_size(Long couch_views_actual_disk_size) {
        this.couch_views_actual_disk_size = couch_views_actual_disk_size;
    }

    public Long getCouch_views_data_size() {
        return couch_views_data_size;
    }

    public void setCouch_views_data_size(Long couch_views_data_size) {
        this.couch_views_data_size = couch_views_data_size;
    }

    public Long getCurr_items() {
        return curr_items;
    }

    public void setCurr_items(Long curr_items) {
        this.curr_items = curr_items;
    }

    public Long getCurr_items_tot() {
        return curr_items_tot;
    }

    public void setCurr_items_tot(Long curr_items_tot) {
        this.curr_items_tot = curr_items_tot;
    }

    public Long getEp_bg_fetched() {
        return ep_bg_fetched;
    }

    public void setEp_bg_fetched(Long ep_bg_fetched) {
        this.ep_bg_fetched = ep_bg_fetched;
    }

    public Double getGet_hits() {
        return get_hits;
    }

    public void setGet_hits(Double get_hits) {
        this.get_hits = get_hits;
    }

    public Long getIndex_data_size() {
        return index_data_size;
    }

    public void setIndex_data_size(Long index_data_size) {
        this.index_data_size = index_data_size;
    }

    public Long getIndex_disk_size() {
        return index_disk_size;
    }

    public void setIndex_disk_size(Long index_disk_size) {
        this.index_disk_size = index_disk_size;
    }

    public Long getMem_used() {
        return mem_used;
    }

    public void setMem_used(Long mem_used) {
        this.mem_used = mem_used;
    }

    public Double getOps() {
        return ops;
    }

    public void setOps(Double ops) {
        this.ops = ops;
    }

    public Long getVb_active_num_non_resident() {
        return vb_active_num_non_resident;
    }

    public void setVb_active_num_non_resident(Long vb_active_num_non_resident) {
        this.vb_active_num_non_resident = vb_active_num_non_resident;
    }

    public Long getVb_replica_curr_items() {
        return vb_replica_curr_items;
    }

    public void setVb_replica_curr_items(Long vb_replica_curr_items) {
        this.vb_replica_curr_items = vb_replica_curr_items;
    }
}
