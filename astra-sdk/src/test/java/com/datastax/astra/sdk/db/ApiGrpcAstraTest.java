package com.datastax.astra.sdk.db;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.astra.sdk.AstraTestUtils;
import io.stargate.sdk.grpc.StargateGrpcApiClient;
import io.stargate.sdk.grpc.domain.ResultSetGrpc;
import io.stargate.sdk.test.doc.TestDocClientConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Test gRPC service against Astra.
 */
public class ApiGrpcAstraTest {

    /** Singleton grpc Client. */
    private static StargateGrpcApiClient grpClient;

    @BeforeAll
    public static void init() {
        // Default client to create DB if needed
        AstraClient client = AstraClient.builder().build();
        String dbId = AstraTestUtils.createTestDbIfNotExist(client);

        // Connect the client to the new created DB
        client = AstraClient.builder()
                .withToken(client.getToken().orElseThrow(() -> new IllegalStateException("token not found")))
                .withCqlKeyspace(TestDocClientConstants.TEST_NAMESPACE)
                .withDatabaseId(dbId)
                .withDatabaseRegion(AstraTestUtils.TEST_REGION)
                .enableGrpc()
                .build();

        grpClient = client.getStargateClient().apiGrpc();
    }

    @Test
    public void testAstra() {
        ResultSetGrpc rs = grpClient.execute("SELECT data_center from system.local");
        Assertions.assertNotNull(rs.one().getString("data_center"));
    }

}
