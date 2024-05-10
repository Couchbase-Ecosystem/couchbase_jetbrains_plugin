package com.couchbase.intellij.tree.iq;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class CapellaOrganization {
    private String id;
    private String name;
    private String description;
    private String website;
    private Map<String, Object> preferences;

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

        public boolean getIsTermsAcceptedForOrg() {
            return isTermsAcceptedForOrg;
        }
    }
}
