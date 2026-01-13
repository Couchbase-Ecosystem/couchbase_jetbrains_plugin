package com.couchbase.intellij.tree.overview.apis;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class NSLookup {

    public static List<String> getServerURL(String url) {
        if (url.contains("cloud.couchbase.com")) {
            List<String> servers = lookupCapellaServers(url);
            if (!servers.isEmpty()) {
                return servers;
            }
        }
        return List.of(url.replace("couchbases://", "").replace("couchbase://", ""));
    }

    public static List<String> lookupCapellaServers(String serverURL) {
        ArrayList<String> servers = new ArrayList<>();
        Hashtable<String, String> env = new Hashtable<>();
        env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
        env.put("java.naming.provider.url", "dns:");

        String url = serverURL.replace("couchbases://", "");

        try {
            DirContext ctx = new InitialDirContext(env);
            Attributes attrs = ctx.getAttributes("_couchbases._tcp." + url, new String[]{"SRV"});
            for (int i = 0; i < attrs.get("SRV").size(); i++) {
                String[] records = attrs.get("SRV").get(i).toString().split(" ");
                String record = records[records.length - 1];
                record = record.replace("cloud.couchbase.com.", "cloud.couchbase.com");
                servers.add(record);
            }

        } catch (NamingException e) {
            System.out.println("Error performing lookup");
            e.printStackTrace();
        }
        return servers;
    }
}
