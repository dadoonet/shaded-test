/*
 * Licensed to David Pilato under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. David Pilato licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package fr.pilato.test.test;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.joda.time.DateTime;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.security.CodeSource;

public class NodeTest {

    @Test
    public void testShadedNode() {
        TransportClient client = TransportClient.builder()
                .settings(Settings.builder()
                                .put("path.home", ".")
                                .put("shield.user", "dadoonet:azerty")
                                .put("plugin.types", "org.elasticsearch.shield.ShieldPlugin")

                )
                .build();
        client.addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress("localhost", 9300)));
        ClusterHealthResponse health = client.admin().cluster().prepareHealth().get();
        System.out.println("getClusterName = " + health.getClusterName());

        CodeSource codeSource = new DateTime().getClass().getProtectionDomain().getCodeSource();
        System.out.println("codeSource = " + codeSource);
    }
}
