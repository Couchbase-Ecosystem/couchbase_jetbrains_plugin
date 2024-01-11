package com.couchbase.intellij.tree.iq.core;

import com.couchbase.intellij.tree.iq.CapellaApiMethods;
import com.couchbase.intellij.workbench.Log;
import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.CredentialAttributesKt;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.openapi.util.Pass;

import java.util.Optional;

public class IQCredentials {

    private String login;
    private String password;

    private CapellaAuth auth;

    public IQCredentials(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public IQCredentials() {

    }

    public void store() {
        PasswordSafe.getInstance().set(getCredentialsAttributes(), new Credentials(login, password));
        if (auth != null) {
            PasswordSafe.getInstance().set(getJwtAttributes(), new Credentials(auth.getTenant(), auth.getJwt()));
        }
    }

    public Optional<Credentials> getCredentials() {
        return Optional.ofNullable(PasswordSafe.getInstance().get(getCredentialsAttributes()));
    }

    public Optional<Credentials> getJwtCredentials() {
        return Optional.ofNullable(PasswordSafe.getInstance().get(getJwtAttributes()));
    }

    private static CredentialAttributes getCredentialsAttributes() {
        return new CredentialAttributes(
                CredentialAttributesKt.generateServiceName("cbplugin", "iq")
        );
    }

    private static CredentialAttributes getJwtAttributes() {
        return new CredentialAttributes(
                CredentialAttributesKt.generateServiceName("cbplugin", "iq-token")
        );
    }

    public String getLogin() {
        if (login == null) {
            login = getCredentials().map(Credentials::getUserName).orElse(null);
        }
        return login;
    }

    public String getPassword() {
        if (password == null) {
            password = getCredentials().map(Credentials::getPasswordAsString).orElse(null);
        }
        return password;
    }

    /**
     * Loads tenant id and JWT token from credential storage and validates them by querying org list
     * @return true on success, false otherwise
     */
    public boolean checkAuthStatus() {
        if (auth == null) {
            getJwtCredentials().ifPresent(creds -> {
                CapellaAuth tempAuth = new CapellaAuth();
                tempAuth.setTenant(creds.getUserName());
                tempAuth.setJwt(creds.getPasswordAsString());
                try {
                    CapellaApiMethods.loadOrganizations(tempAuth);
                    auth = tempAuth;
                } catch (Exception e) {
                    Log.debug("Failed to restore capella session", e);
                }
            });
        }

        return auth != null;
    }

    public boolean doLogin() {
        try {
            auth = CapellaApiMethods.login(getLogin(), getPassword());
            return auth != null;
        } catch (Exception e) {
            Log.error("Failed to authenticate with Capella", e);
            return false;
        }
    }

    public CapellaAuth getAuth() {
        return auth;
    }

    public void clear() {
        PasswordSafe.getInstance().set(getCredentialsAttributes(), null);
        login = null;
        password = null;
        auth = null;
    }
}
