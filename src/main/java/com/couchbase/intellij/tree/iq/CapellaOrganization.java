package com.couchbase.intellij.tree.iq;

import lombok.Data;

import java.util.List;

@Data
public class CapellaOrganization {
    private String id;
    private String name;

    private IQ iq;

    @Data
    public static class IQ {
        private boolean enabled;
        private Other other;
    }

    @Data
    public static class Other {
        private boolean isTermsAcceptedForOrg;

        public Other() {
        }

        public void setIsTermsAcceptedForOrg(boolean value) {
            isTermsAcceptedForOrg = value;
        }

        public boolean isTermsAcceptedForOrg() {
            return isTermsAcceptedForOrg;
        }
    }
}
