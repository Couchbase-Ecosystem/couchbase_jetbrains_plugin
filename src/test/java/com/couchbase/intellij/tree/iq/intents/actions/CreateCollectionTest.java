package com.couchbase.intellij.tree.iq.intents.actions;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.testutil.TestActiveCluster;
import com.couchbase.intellij.tree.iq.intents.AbstractIQTest;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

public class CreateCollectionTest extends AbstractIQTest {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Cluster mockCluster = Mockito.mock(Cluster.class);

        ActiveCluster.setInstance(new TestActiveCluster(mockCluster));
    }

    @Test
    public void test() throws Exception {
        send("create a collection named 'test'", response -> {
            JsonObject jsonResponse = getJson(response);
            assertNotNull(jsonResponse);
            List<JsonObject> intents = getIntents(response, CreateCollection.class);
            assertSize(1, intents);
            CreateCollection action = new CreateCollection();
            String prompt = action.fire(getProject(), null, null, jsonResponse, intents.get(0));
            assertNotNull(prompt);
            assertTrue(prompt.startsWith("ask the user in which bucket and scope"));
            send(prompt, true, response2 -> {
                assertNotJson(response2);
                assertTrue(getResponse(response2).contains("bucket"));
                assertTrue(getResponse(response2).contains("scope"));
                send("travel-sample bucket and scope inventoery", response3 -> {
                    JsonObject jsonResponse2 = getJson(response3);
                    assertNotNull(jsonResponse2);
                    List<JsonObject> intents2 = getIntents(response, CreateCollection.class);
                    assertSize(1, intents2);
                });
            });
        });
    }

}