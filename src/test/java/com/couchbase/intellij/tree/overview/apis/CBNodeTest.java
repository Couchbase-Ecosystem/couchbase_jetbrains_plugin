package com.couchbase.intellij.tree.overview.apis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class CBNodeTest {

    private static final Gson GSON = new GsonBuilder().serializeNulls().create();

    private static final String NODE_JSON = """
            {
              "clusterMembership": "active",
              "recoveryType": "none",
              "status": "healthy",
              "otpNode": "ns_1@10.0.0.5",
              "thisNode": true,
              "hostname": "10.0.0.5:8091",
              "nodeUUID": "abc123",
              "clusterCompatibility": 393222,
              "version": "7.6.2-3505-enterprise",
              "os": "x86_64-pc-linux-gnu",
              "cpuCount": 8,
              "ports": {"direct": 11210, "httpsMgmt": 18091},
              "services": ["kv", "n1ql", "index"],
              "nodeEncryption": false,
              "addressFamily": "inet",
              "serverGroup": "Group 1",
              "nodeHash": 987654321,
              "systemStats": {"cpu_utilization_rate": 12.5, "mem_total": 16000, "mem_free": 4000, "swap_used": 0},
              "interestingStats": {"curr_items": 500, "ops": 3.0, "mem_used": 1200},
              "uptime": "3600",
              "memoryTotal": 17179869184,
              "memoryFree": 8589934592,
              "mcdMemoryReserved": 6000,
              "mcdMemoryAllocated": 5000
            }
            """;

    @Test
    public void parsesScalarFields() {
        CBNode node = GSON.fromJson(NODE_JSON, CBNode.class);

        Assert.assertEquals("active", node.getClusterMembership());
        Assert.assertEquals("healthy", node.getStatus());
        Assert.assertEquals("ns_1@10.0.0.5", node.getOtpNode());
        Assert.assertTrue(node.isThisNode());
        Assert.assertEquals("10.0.0.5:8091", node.getHostname());
        Assert.assertEquals(Long.valueOf(393222L), node.getClusterCompatibility());
        Assert.assertEquals("7.6.2-3505-enterprise", node.getVersion());
        Assert.assertEquals(Long.valueOf(8L), node.getCpuCount());
        Assert.assertEquals(Long.valueOf(17179869184L), node.getMemoryTotal());
        Assert.assertEquals(Long.valueOf(8589934592L), node.getMemoryFree());
        Assert.assertEquals("3600", node.getUptime());
    }

    @Test
    public void parsesCollectionsAndNestedStats() {
        CBNode node = GSON.fromJson(NODE_JSON, CBNode.class);

        List<String> services = node.getServices();
        Assert.assertEquals(List.of("kv", "n1ql", "index"), services);

        Map<String, String> ports = node.getPorts();
        Assert.assertEquals("11210", ports.get("direct"));
        Assert.assertEquals("18091", ports.get("httpsMgmt"));

        SystemStats system = node.getSystemStats();
        Assert.assertEquals(Double.valueOf(12.5), system.getCpu_utilization_rate());
        Assert.assertEquals(Long.valueOf(16000L), system.getMem_total());
        Assert.assertEquals(Long.valueOf(0L), system.getSwap_used());

        InterestingStats interesting = node.getInterestingStats();
        Assert.assertEquals(Long.valueOf(500L), interesting.getCurr_items());
        Assert.assertEquals(Double.valueOf(3.0), interesting.getOps());
    }

    @Test
    public void missingPrimitiveBooleansDefaultToFalse() {
        CBNode node = GSON.fromJson("{\"hostname\":\"10.0.0.6\"}", CBNode.class);

        Assert.assertFalse(node.isThisNode());
        Assert.assertFalse(node.isNodeEncryption());
        Assert.assertFalse(node.isAddressFamilyOnly());
        Assert.assertEquals("10.0.0.6", node.getHostname());
    }

    @Test
    public void missingObjectsAndWrapperNumbersStayNull() {
        CBNode node = GSON.fromJson("{\"hostname\":\"h\"}", CBNode.class);

        Assert.assertNull(node.getServices());
        Assert.assertNull(node.getPorts());
        Assert.assertNull(node.getSystemStats());
        Assert.assertNull(node.getInterestingStats());
        Assert.assertNull(node.getCpuCount());
        Assert.assertNull(node.getMemoryTotal());
        Assert.assertNull(node.getUptime());
    }

    @Test
    public void explicitNullsAreNullAndZeroIsKept() {
        CBNode node = GSON.fromJson("{\"cpuCount\":null,\"memoryFree\":0,\"status\":null}", CBNode.class);

        Assert.assertNull(node.getCpuCount());
        Assert.assertNull(node.getStatus());
        Assert.assertEquals(Long.valueOf(0L), node.getMemoryFree());
    }

    @Test
    public void unknownFieldsAreIgnored() {
        CBNode node = GSON.fromJson("{\"hostname\":\"h\",\"someFutureField\":{\"a\":1}}", CBNode.class);

        Assert.assertEquals("h", node.getHostname());
    }
}
