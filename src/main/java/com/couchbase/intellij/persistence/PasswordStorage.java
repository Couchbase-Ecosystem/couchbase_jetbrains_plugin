package com.couchbase.intellij.persistence;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.CredentialAttributesKt;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;

public class PasswordStorage {

    public static void savePassword(String key, String password) {
        CredentialAttributes credentialAttributes =
        createCredentialAttributes(key); // see previous sample
        Credentials credentials = new Credentials(key, password);
        PasswordSafe.getInstance().set(credentialAttributes, credentials);
    }

    public static String getPassword(String key) {
        CredentialAttributes credentialAttributes = createCredentialAttributes(key);

        Credentials credentials = PasswordSafe.getInstance().get(credentialAttributes);
        if (credentials != null) {
            return credentials.getPasswordAsString();
        }
        return null;
    }

    private static CredentialAttributes createCredentialAttributes(String key) {
        return new CredentialAttributes(
                CredentialAttributesKt.generateServiceName("cbplugin", key)
        );
    }
}
