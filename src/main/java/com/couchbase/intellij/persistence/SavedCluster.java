package com.couchbase.intellij.persistence;

import com.couchbase.client.java.json.JsonObject;
import com.couchbase.intellij.persistence.storage.ClustersStorage;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SavedCluster {
    private String id;
    private String name;

    private String queryParams;
    private String url;
    private String username;

    private String color;

    private boolean sslEnable;

    private Boolean ldap;
    private String defaultBucket;

    private Boolean readOnly;

    private Long inferCachePeriod;

    private QueryPreferences queryPreferences;
    private String options;

    public Boolean getLDAP() {
        if (ldap == null) {
            return false;
        }
        return ldap;
    }

    public void setLDAP(Boolean LDAP) {
        ldap = LDAP;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDefaultBucket() {
        return defaultBucket;
    }

    public void setDefaultBucket(String defaultBucket) {
        this.defaultBucket = defaultBucket;
    }

    public boolean isSslEnable() {
        return sslEnable;
    }

    public void setSslEnable(boolean sslEnable) {
        this.sslEnable = sslEnable;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Boolean isReadOnly() {
        return readOnly;
    }

    @Override
    public String toString() {
        return "SavedCluster{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", username='" + username + '\'' +
                ", sslEnable=" + sslEnable +
                ", defaultBucket='" + defaultBucket + '\'' +
                ", options='" +  + '\'' +
                '}';
    }

    public JsonObject getInferCacheValue(String key) {
        Map<String, String> inferCacheValues = ClustersStorage.getInstance().getValue().getInferCache().get(getId());
        if (inferCacheValues == null) {
            inferCacheValues = new HashMap<>();
            ClustersStorage.getInstance().getValue().getInferCache().put(getId(), inferCacheValues);
        }
        if (isInferCacheValid(key)) {
            String cache = inferCacheValues.get(key);
            if (cache != null) {
                byte[] src = Base64.getDecoder().decode(cache);
                return JsonObject.fromJson(src);
            }
        }
        return null;
    }

    public void setInferCacheValue(String key, JsonObject infer) {
        Map<String, String> inferCacheValues = ClustersStorage.getInstance().getValue().getInferCache().get(getId());
        if (inferCacheValues == null) {
            inferCacheValues = new HashMap<>();
            ClustersStorage.getInstance().getValue().getInferCache().put(getId(), inferCacheValues);
        }
        Map<String, Long> inferValuesUpdateTimes = ClustersStorage.getInstance().getValue().getInferCacheUpdateTimes().get(getId());
        if (inferValuesUpdateTimes == null) {
            inferValuesUpdateTimes = new HashMap<>();
            ClustersStorage.getInstance().getValue().getInferCacheUpdateTimes().put(getId(), inferValuesUpdateTimes);
        }
        inferCacheValues.put(key, infer == null ? null : Base64.getEncoder().encodeToString(infer.toString().getBytes()));
        inferValuesUpdateTimes.put(key, System.currentTimeMillis());
    }

    public boolean isInferCacheValid(String key) {
        Map<String, Long> inferValuesUpdateTimes = ClustersStorage.getInstance().getValue().getInferCacheUpdateTimes().get(getId());
        if (inferValuesUpdateTimes == null) {
            inferValuesUpdateTimes = new HashMap<>();
            ClustersStorage.getInstance().getValue().getInferCacheUpdateTimes().put(getId(), inferValuesUpdateTimes);
        }
        if (inferCachePeriod == null || inferCachePeriod == 0) {
            inferCachePeriod = TimeUnit.DAYS.toMillis(3);
        }
        return System.currentTimeMillis() - inferValuesUpdateTimes.getOrDefault(key, 0L) < inferCachePeriod;
    }

    public Map<String, String> getInferCacheValues() {
        Map<String, String> inferCacheValues = ClustersStorage.getInstance().getValue().getInferCache().get(getId());
        if (inferCacheValues == null) {
            inferCacheValues = new HashMap<>();
            ClustersStorage.getInstance().getValue().getInferCache().put(getId(), inferCacheValues);
        }
        return inferCacheValues;
    }

    public Map<String, Long> getInferValuesUpdateTimes() {
        Map<String, Long> inferValuesUpdateTimes = ClustersStorage.getInstance().getValue().getInferCacheUpdateTimes().get(getId());
        if (inferValuesUpdateTimes == null) {
            inferValuesUpdateTimes = new HashMap<>();
            ClustersStorage.getInstance().getValue().getInferCacheUpdateTimes().put(getId(), inferValuesUpdateTimes);
        }
        return inferValuesUpdateTimes;
    }

    public Long getInferCachePeriod() {
        return inferCachePeriod == null ? 0 : inferCachePeriod;
    }

    public void setInferCachePeriod(Long inferCachePeriod) {
        this.inferCachePeriod = inferCachePeriod;
    }

    public String getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(String queryParams) {
        this.queryParams = queryParams;
    }

    public Boolean getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
    }

    public QueryPreferences getQueryPreferences() {
        if (queryPreferences == null) {
            queryPreferences = new QueryPreferences();
            queryPreferences.setQueryTimeout(600);
            queryPreferences.setSaveHistory(true);
        }
        return queryPreferences;
    }

    public void setQueryPreferences(QueryPreferences queryPreferences) {
        this.queryPreferences = queryPreferences;
    }

    public void addOption(String option) {
        options = Stream.concat(options(), Stream.of(option)).collect(Collectors.joining(","));
    }

    public void removeOption(String option) {
        if (this.options != null) {
            options = options().filter(o -> !o.equals(option)).collect(Collectors.joining(","));
        }
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public void options(Set<String> data) {
        options = data.stream().collect(Collectors.joining(","));
    }

    public Stream<String> options() {
        if (options == null) {
            return Stream.empty();
        }
        return Arrays.stream(options.split(","));
    }

    public boolean hasOption(Clusters.Options option) {
        return options().anyMatch(o -> option.name().toLowerCase().equals(o));
    }

    public void addOption(Clusters.Options option) {
        addOption(option.name().toLowerCase());
    }

    public void removeOption(Clusters.Options option) {
        removeOption(option.name().toLowerCase());
    }
}
