package com.couchbase.intellij.tree.iq;

import java.util.ArrayList;

public class CapellaOrganizationList {

    private ArrayList<Entry> data;

    public void setData(ArrayList<Entry> data) {
        this.data = data;
    }

    public ArrayList<Entry> getData() {
        return data;
    }

    public static class Entry {
        private CapellaOrganization data;

        public void setData(CapellaOrganization data) {
            this.data = data;
        }

        public CapellaOrganization getData() {
            return data;
        }
    }
}
