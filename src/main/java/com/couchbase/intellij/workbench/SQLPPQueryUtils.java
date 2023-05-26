package com.couchbase.intellij.workbench;

public class SQLPPQueryUtils {

    public static boolean hasOrderBy(String query) {
        if(query == null){
            return false;
        }

        String[] tokens = query.split(" ");
        for(String token: tokens) {
            if("ORDER".equalsIgnoreCase(token.trim())) {
                return true;
            }
        }
        return false;
    }
}
