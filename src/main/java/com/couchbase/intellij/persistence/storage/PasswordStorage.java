package com.couchbase.intellij.persistence.storage;

import com.couchbase.intellij.persistence.SavedCluster;
import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.CredentialAttributesKt;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;

public class PasswordStorage {

    public static void savePassword(SavedCluster sc, String password) {
        CredentialAttributes credentialAttributes = createCredentialAttributes(sc.getUsername() + ":" + sc.getName());
        Credentials credentials = new Credentials(sc.getUsername() + ":" + sc.getName(), password);
        PasswordSafe.getInstance().set(credentialAttributes, credentials);
    }

    public static String getPassword(SavedCluster sc) {
        CredentialAttributes credentialAttributes = createCredentialAttributes(sc.getUsername() + ":" + sc.getName());

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
