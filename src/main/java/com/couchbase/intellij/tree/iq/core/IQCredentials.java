package com.couchbase.intellij.tree.iq.core;

import com.couchbase.intellij.tree.iq.CapellaApiMethods;
import com.couchbase.intellij.workbench.Log;
import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.CredentialAttributesKt;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;

import java.util.Optional;

public class IQCredentials {

    private String login;
    private String password;

    private CapellaAuth auth;
    private CapellaAuth authResponse;

    public IQCredentials(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public IQCredentials() {

    }

    public void store() {
        PasswordSafe.getInstance().set(getCredentialsAttributes(), new Credentials(login, password));
    }

    public Optional<Credentials> getCredentials() {
        return Optional.ofNullable(PasswordSafe.getInstance().get(getCredentialsAttributes()));
    }

    private static CredentialAttributes getCredentialsAttributes() {
        return new CredentialAttributes(
                CredentialAttributesKt.generateServiceName("cbplugin", "iq")
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

    public boolean checkAuthStatus() {
        try {
            authResponse = CapellaApiMethods.login(getLogin(), getPassword());
            return authResponse != null;
        } catch (Exception e) {
            Log.error("Failed to authenticate with Capella", e);
            return false;
        }
    }

    public CapellaAuth getAuth() {
        return authResponse;
    }
}
