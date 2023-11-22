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

    public String[] getNames() {
        return data.stream()
                .map(CapellaOrganizationList.Entry::getData)
                .map(CapellaOrganization::getName)
                .toArray(String[]::new);
    }

    public int indexOf(CapellaOrganization organization) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getData() == organization) {
                return i;
            }
        }
        return -1;
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
