package com.dstx.astra.sdk;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;

import com.datastax.oss.driver.shaded.guava.common.base.Strings;
import com.dstx.astra.sdk.AstraClient.AstraClientBuilder;

/**
 * Mutualize logic to initialize the client.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public abstract class AbstractAstraIntegrationTest {
    
    public static final String ANSI_RESET           = "\u001B[0m";
    public static final String ANSI_GREEN           = "\u001B[32m";
    public static final String ANSI_YELLOW          = "\u001B[33m";
    
    // Client initialized based on environment variables
    public static AstraClient client;
    
    // Read Configuration from properties
    public static Optional<String> cloudRegion  = getPropertyFromEnv(AstraClient.ASTRA_DB_REGION);
    public static Optional<String> dbId         = getPropertyFromEnv(AstraClient.ASTRA_DB_ID);
    public static Optional<String> clientId     = getPropertyFromEnv(AstraClient.ASTRA_DB_CLIENT_ID);
    public static Optional<String> clientSecret = getPropertyFromEnv(AstraClient.ASTRA_DB_CLIENT_SECRET);
    public static Optional<String> appToken     = getPropertyFromEnv(AstraClient.ASTRA_DB_APPLICATION_TOKEN);
    
    @BeforeAll
    public static void init_astra_client_with_env_var() {
        AstraClientBuilder clientBuilder = AstraClient.builder();
        if (cloudRegion.isPresent()) {
            clientBuilder.cloudProviderRegion(cloudRegion.get());
        }
        if (dbId.isPresent()) {
            clientBuilder.databaseId(dbId.get());
        }
        if (clientId.isPresent()) {
            clientBuilder.clientId(clientId.get());
        }
        if (clientSecret.isPresent()) {
            clientBuilder.clientSecret(clientSecret.get());
        }
        if (appToken.isPresent()) {
            clientBuilder.appToken(appToken.get());
        }
        client = clientBuilder.build();
    }
    
    protected static Optional<String> getPropertyFromEnv(String varname) {
        String value = null;
        if (!Strings.isNullOrEmpty(System.getenv(varname))) {
            value = System.getenv(varname);
        }
        if (!Strings.isNullOrEmpty(System.getProperty(varname))) {
            value = System.getProperty(varname);
        }
        return Optional.ofNullable(value);
    }
    
    /**
     * Multiple operations are async and 
     * we need to wait until a condition is reached.
     */
    protected static void waitForSeconds(int s) {
        try {Thread.sleep(s * 1000);} catch (InterruptedException e) {}
    }

}