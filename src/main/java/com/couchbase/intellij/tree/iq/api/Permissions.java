package com.couchbase.intellij.tree.iq.api;

public class Permissions {

    private Permission create;
    private Permission read;
    private Permission update;
    private Permission delete;

    public Permission getCreate() {
        return create;
    }

    public void setCreate(Permission create) {
        this.create = create;
    }

    public Permission getRead() {
        return read;
    }

    public void setRead(Permission read) {
        this.read = read;
    }

    public Permission getUpdate() {
        return update;
    }

    public void setUpdate(Permission update) {
        this.update = update;
    }

    public Permission getDelete() {
        return delete;
    }

    public void setDelete(Permission delete) {
        this.delete = delete;
    }
}
