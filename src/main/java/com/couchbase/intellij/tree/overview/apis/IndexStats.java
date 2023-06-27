package com.couchbase.intellij.tree.overview.apis;

public class IndexStats {

    private String indexer_state;
    private Long memory_quota;
    private Long memory_total_storage;
    private Long memory_used;
    private Long total_indexer_gc_pause_ns;
    private String avg_drain_rate;
    private String avg_item_size;
    private String avg_scan_latency;
    private String cache_hit_percent;
    private String cache_hits;
    private String cache_misses;
    private Long data_size;
    private Long disk_size;
    private Double frag_percent;
    private String initial_build_progress;
    private String items_count;
    private String last_known_scan_time;
    private String num_docs_indexed;
    private String num_docs_pending;
    private String num_docs_queued;
    private String num_items_flushed;
    private String num_pending_requests;
    private String num_requests;
    private String num_rows_returned;
    private String num_scan_errors;
    private String num_scan_timeouts;
    private String recs_in_mem;
    private String recs_on_disk;
    private Double resident_percent;
    private Long scan_bytes_read;
    private String total_scan_duration;
    private String avg_array_length;
    private String docid_count;

    public String getIndexer_state() {
        return indexer_state;
    }

    public void setIndexer_state(String indexer_state) {
        this.indexer_state = indexer_state;
    }

    public Long getMemory_quota() {
        return memory_quota;
    }

    public void setMemory_quota(Long memory_quota) {
        this.memory_quota = memory_quota;
    }

    public Long getMemory_total_storage() {
        return memory_total_storage;
    }

    public void setMemory_total_storage(Long memory_total_storage) {
        this.memory_total_storage = memory_total_storage;
    }

    public Long getMemory_used() {
        return memory_used;
    }

    public void setMemory_used(Long memory_used) {
        this.memory_used = memory_used;
    }

    public Long getTotal_indexer_gc_pause_ns() {
        return total_indexer_gc_pause_ns;
    }

    public void setTotal_indexer_gc_pause_ns(Long total_indexer_gc_pause_ns) {
        this.total_indexer_gc_pause_ns = total_indexer_gc_pause_ns;
    }

    public String getAvg_drain_rate() {
        return avg_drain_rate;
    }

    public void setAvg_drain_rate(String avg_drain_rate) {
        this.avg_drain_rate = avg_drain_rate;
    }

    public String getAvg_item_size() {
        return avg_item_size;
    }

    public void setAvg_item_size(String avg_item_size) {
        this.avg_item_size = avg_item_size;
    }

    public String getAvg_scan_latency() {
        return avg_scan_latency;
    }

    public void setAvg_scan_latency(String avg_scan_latency) {
        this.avg_scan_latency = avg_scan_latency;
    }

    public String getCache_hit_percent() {
        return cache_hit_percent;
    }

    public void setCache_hit_percent(String cache_hit_percent) {
        this.cache_hit_percent = cache_hit_percent;
    }

    public String getCache_hits() {
        return cache_hits;
    }

    public void setCache_hits(String cache_hits) {
        this.cache_hits = cache_hits;
    }

    public String getCache_misses() {
        return cache_misses;
    }

    public void setCache_misses(String cache_misses) {
        this.cache_misses = cache_misses;
    }

    public Long getData_size() {
        return data_size;
    }

    public void setData_size(Long data_size) {
        this.data_size = data_size;
    }

    public Long getDisk_size() {
        return disk_size;
    }

    public void setDisk_size(Long disk_size) {
        this.disk_size = disk_size;
    }

    public Double getFrag_percent() {
        return frag_percent;
    }

    public void setFrag_percent(Double frag_percent) {
        this.frag_percent = frag_percent;
    }

    public String getInitial_build_progress() {
        return initial_build_progress;
    }

    public void setInitial_build_progress(String initial_build_progress) {
        this.initial_build_progress = initial_build_progress;
    }

    public String getItems_count() {
        return items_count;
    }

    public void setItems_count(String items_count) {
        this.items_count = items_count;
    }

    public String getLast_known_scan_time() {
        return last_known_scan_time;
    }

    public void setLast_known_scan_time(String last_known_scan_time) {
        this.last_known_scan_time = last_known_scan_time;
    }


    public String getNum_docs_indexed() {
        return num_docs_indexed;
    }

    public void setNum_docs_indexed(String num_docs_indexed) {
        this.num_docs_indexed = num_docs_indexed;
    }

    public String getNum_docs_pending() {
        return num_docs_pending;
    }

    public void setNum_docs_pending(String num_docs_pending) {
        this.num_docs_pending = num_docs_pending;
    }

    public String getNum_docs_queued() {
        return num_docs_queued;
    }

    public void setNum_docs_queued(String num_docs_queued) {
        this.num_docs_queued = num_docs_queued;
    }

    public String getNum_items_flushed() {
        return num_items_flushed;
    }

    public void setNum_items_flushed(String num_items_flushed) {
        this.num_items_flushed = num_items_flushed;
    }

    public String getNum_pending_requests() {
        return num_pending_requests;
    }

    public void setNum_pending_requests(String num_pending_requests) {
        this.num_pending_requests = num_pending_requests;
    }

    public String getNum_requests() {
        return num_requests;
    }

    public void setNum_requests(String num_requests) {
        this.num_requests = num_requests;
    }

    public String getNum_rows_returned() {
        return num_rows_returned;
    }

    public void setNum_rows_returned(String num_rows_returned) {
        this.num_rows_returned = num_rows_returned;
    }

    public String getNum_scan_errors() {
        return num_scan_errors;
    }

    public void setNum_scan_errors(String num_scan_errors) {
        this.num_scan_errors = num_scan_errors;
    }

    public String getNum_scan_timeouts() {
        return num_scan_timeouts;
    }

    public void setNum_scan_timeouts(String num_scan_timeouts) {
        this.num_scan_timeouts = num_scan_timeouts;
    }

    public String getRecs_in_mem() {
        return recs_in_mem;
    }

    public void setRecs_in_mem(String recs_in_mem) {
        this.recs_in_mem = recs_in_mem;
    }

    public String getRecs_on_disk() {
        return recs_on_disk;
    }

    public void setRecs_on_disk(String recs_on_disk) {
        this.recs_on_disk = recs_on_disk;
    }

    public Double getResident_percent() {
        return resident_percent;
    }

    public void setResident_percent(Double resident_percent) {
        this.resident_percent = resident_percent;
    }

    public Long getScan_bytes_read() {
        return scan_bytes_read;
    }

    public void setScan_bytes_read(Long scan_bytes_read) {
        this.scan_bytes_read = scan_bytes_read;
    }

    public String getTotal_scan_duration() {
        return total_scan_duration;
    }

    public void setTotal_scan_duration(String total_scan_duration) {
        this.total_scan_duration = total_scan_duration;
    }

    public String getAvg_array_length() {
        return avg_array_length;
    }

    public void setAvg_array_length(String avg_array_length) {
        this.avg_array_length = avg_array_length;
    }

    public String getDocid_count() {
        return docid_count;
    }

    public void setDocid_count(String docid_count) {
        this.docid_count = docid_count;
    }

    @Override
    public String toString() {
        return "IndexStats{" +
                "indexer_state='" + indexer_state + '\'' +
                ", memory_quota=" + memory_quota +
                ", memory_total_storage=" + memory_total_storage +
                ", memory_used=" + memory_used +
                ", total_indexer_gc_pause_ns=" + total_indexer_gc_pause_ns +
                ", avg_drain_rate='" + avg_drain_rate + '\'' +
                ", avg_item_size='" + avg_item_size + '\'' +
                ", avg_scan_latency='" + avg_scan_latency + '\'' +
                ", cache_hit_percent='" + cache_hit_percent + '\'' +
                ", cache_hits='" + cache_hits + '\'' +
                ", cache_misses='" + cache_misses + '\'' +
                ", data_size=" + data_size +
                ", disk_size=" + disk_size +
                ", frag_percent=" + frag_percent +
                ", initial_build_progress='" + initial_build_progress + '\'' +
                ", items_count='" + items_count + '\'' +
                ", last_known_scan_time='" + last_known_scan_time + '\'' +
                ", num_docs_indexed='" + num_docs_indexed + '\'' +
                ", num_docs_pending='" + num_docs_pending + '\'' +
                ", num_docs_queued='" + num_docs_queued + '\'' +
                ", num_items_flushed='" + num_items_flushed + '\'' +
                ", num_pending_requests='" + num_pending_requests + '\'' +
                ", num_requests='" + num_requests + '\'' +
                ", num_rows_returned='" + num_rows_returned + '\'' +
                ", num_scan_errors='" + num_scan_errors + '\'' +
                ", num_scan_timeouts='" + num_scan_timeouts + '\'' +
                ", recs_in_mem='" + recs_in_mem + '\'' +
                ", recs_on_disk='" + recs_on_disk + '\'' +
                ", resident_percent=" + resident_percent +
                ", scan_bytes_read=" + scan_bytes_read +
                ", total_scan_duration='" + total_scan_duration + '\'' +
                ", avg_array_length='" + avg_array_length + '\'' +
                ", docid_count='" + docid_count + '\'' +
                '}';
    }
}


