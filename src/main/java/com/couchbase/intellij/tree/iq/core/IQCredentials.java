package com.couchbase.intellij.tree.iq.core;

import cn.hutool.core.lang.Opt;
import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.CredentialAttributesKt;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;

import java.util.Optional;

public class IQCredentials {

    private String login;
    private String password;

    private CapellaAuthResponse auth;

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

    public int checkAuthStatus() {
        return 500;
    }

    public CapellaAuthResponse getAuth() {
return null;
    }
}
