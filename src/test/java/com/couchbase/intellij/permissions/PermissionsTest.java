package com.couchbase.intellij.permissions;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;


public class PermissionsTest {

    @Test
    public void myTest() throws Exception {
        String response = getCode("capella_read_only.json");

        Assertions.assertTrue(response != null);
        //Assertions.assertFalse( canWrite())
        //Assertions.assertFalse( isAdmin())
    }


    private String getCode(String fileName) throws Exception {
        return new String(PermissionsTest.class.getClassLoader().getResourceAsStream("permissions_responses/" + fileName).readAllBytes());
    }
}
