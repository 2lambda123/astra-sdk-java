/*
 * Copyright DataStax, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datastax.stargate.sdk.test;


import org.junit.jupiter.api.Test;

import com.datastax.stargate.sdk.StargateClient;

public class K8ssandraVmTest {
    
    @Test
    public void connectivity() {
        StargateClient stargate = StargateClient.builder()
                .username("k8ssandra-superuser")
                .password("JxzrPOnvDGqfEOQ0EySQ")
                .endPointAuth("http://wksc272755.cedrick-ajug.datastaxtraining.com:8081")
                .endPointRest("http://wksc272755.cedrick-ajug.datastaxtraining.com:8082")
                .endPointGraphQL("http://wksc272755.cedrick-ajug.datastaxtraining.com:8080")
                .disableCQL()
                .build();
        
        stargate.apiRest().keyspaceNames().forEach(System.out::println);
    }

}