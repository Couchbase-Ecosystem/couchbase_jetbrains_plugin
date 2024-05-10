package com.couchbase.intellij.tree.iq;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CapellaOrganizationList {

    private List<Entry> data;

    public void setData(List<Entry> data) {
        this.data = data;
    }

    public List<Entry> getData() {
        return data;
    }

    public CapellaOrganizationList getOnlyIqEnabledOrgs() {
        CapellaOrganizationList filteredList = new CapellaOrganizationList();
        filteredList.setData(
                data.stream()
                        .filter(Objects::nonNull)
                        .filter(org -> org.getData() != null)
                        .filter(org -> org.getData().getIq() != null)
                        .filter(org -> org.getData().getIq().isEnabled())
                        .filter(org -> org.getData().getIq().getOther() != null)
                        .filter(org -> org.getData().getIq().getOther().getIsTermsAcceptedForOrg())
                        .collect(Collectors.toList())
        );
        return filteredList;
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
