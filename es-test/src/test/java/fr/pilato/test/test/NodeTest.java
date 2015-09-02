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
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.IndexNotFoundException;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.security.CodeSource;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class NodeTest {

    @Test
    public void testJodaLibVersion() {
        CodeSource codeSource = new org.joda.time.DateTime().getClass().getProtectionDomain().getCodeSource();
        assertThat(codeSource.getLocation().getFile(), containsString("joda-time-2.1.jar"));

        codeSource = new fr.pilato.thirdparty.joda.time.DateTime().getClass().getProtectionDomain().getCodeSource();
        assertThat(codeSource.getLocation().getFile(), containsString("es-shaded-1.0-SNAPSHOT.jar"));
    }

    @Test
    public void testShadedClient() {
        TransportClient client = TransportClient.builder()
                .settings(Settings.builder()
                                .put("path.home", ".")
                                .put("shield.user", "dadoonet:azerty")
                                .put("plugin.types", "org.elasticsearch.shield.ShieldPlugin")

                )
                .build();
        client.addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress("localhost", 9300)));
        ClusterHealthResponse health = client.admin().cluster().prepareHealth().setWaitForYellowStatus().get();
        assertThat(health.getClusterName(), is("elasticsearch"));

        try {
            client.admin().indices().prepareDelete("test").get();
        } catch (IndexNotFoundException e) {
            // That's fine
        }

        client.prepareIndex("test", "doc", "1").setSource("foo", "bar").setRefresh(true).get();
        SearchResponse searchResponse = client.prepareSearch("test").get();
        assertThat(searchResponse.getHits().getTotalHits(), is(1L));
    }
}
