package com.couchbase.intellij.tree.iq.api;

import java.util.Map;

public class Resource {

    private Map<String, Permission> apiKey;
    private Map<String, Permission> billing;
    private Map<String, Permission> cloud;
    private Map<String, Permission> event;
    private Map<String, Permission> permission;
    private Map<String, Permission> project;
    private Map<String, Permission> realm;
    private Map<String, Permission> supportTicket;
    private Map<String, Permission> team;
    private Map<String, Permission> trials;
    private Map<String, Permission> user;

    public Map<String, Permission> getApiKey() {
        return apiKey;
    }

    public void setApiKey(Map<String, Permission> apiKey) {
        this.apiKey = apiKey;
    }

    public Map<String, Permission> getBilling() {
        return billing;
    }

    public void setBilling(Map<String, Permission> billing) {
        this.billing = billing;
    }

    public Map<String, Permission> getCloud() {
        return cloud;
    }

    public void setCloud(Map<String, Permission> cloud) {
        this.cloud = cloud;
    }

    public Map<String, Permission> getEvent() {
        return event;
    }

    public void setEvent(Map<String, Permission> event) {
        this.event = event;
    }

    public Map<String, Permission> getPermission() {
        return permission;
    }

    public void setPermission(Map<String, Permission> permission) {
        this.permission = permission;
    }

    public Map<String, Permission> getProject() {
        return project;
    }

    public void setProject(Map<String, Permission> project) {
        this.project = project;
    }

    public Map<String, Permission> getRealm() {
        return realm;
    }

    public void setRealm(Map<String, Permission> realm) {
        this.realm = realm;
    }

    public Map<String, Permission> getSupportTicket() {
        return supportTicket;
    }

    public void setSupportTicket(Map<String, Permission> supportTicket) {
        this.supportTicket = supportTicket;
    }

    public Map<String, Permission> getTeam() {
        return team;
    }

    public void setTeam(Map<String, Permission> team) {
        this.team = team;
    }

    public Map<String, Permission> getTrials() {
        return trials;
    }

    public void setTrials(Map<String, Permission> trials) {
        this.trials = trials;
    }

    public Map<String, Permission> getUser() {
        return user;
    }

    public void setUser(Map<String, Permission> user) {
        this.user = user;
    }
}
