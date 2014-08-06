/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2014] Codenvy, S.A.
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.analytics.pig.scripts;

import com.codenvy.analytics.BaseTest;
import com.codenvy.analytics.datamodel.ListValueData;
import com.codenvy.analytics.datamodel.MapValueData;
import com.codenvy.analytics.metrics.*;
import com.codenvy.analytics.pig.scripts.util.Event;
import com.codenvy.analytics.pig.scripts.util.LogGenerator;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;

/**
 * @author Alexander Reshetnyak
 */
public class TestProductUsageFactoryReferrers extends BaseTest {

    @BeforeClass
    public void init() throws Exception {
        List<Event> events = new ArrayList<>();

        events.add(Event.Builder.createUserCreatedEvent("uid1", "user1@gmail.com", "user1@gmail.com")
                                .withDate("2013-02-10").withTime("10:00:00,000").build());
        events.add(Event.Builder.createUserCreatedEvent("uid2", "anonymoususer_1", "anonymoususer_1")
                                .withDate("2013-02-10").withTime("10:00:00,000").build());

        events.add(Event.Builder.createSessionFactoryStartedEvent("id1", "tmp-1", "user1@gmail.com", "true", "brType")
                                .withDate("2013-02-10").withTime("10:00:00").build());
        events.add(Event.Builder.createSessionFactoryStoppedEvent("id1", "tmp-1", "user1@gmail.com")
                                .withDate("2013-02-10").withTime("10:05:00").build());

        events.add(Event.Builder.createSessionFactoryStartedEvent("id2", "tmp-2", "user1@gmail.com", "true", "brType")
                                .withDate("2013-02-10").withTime("10:20:00").build());
        events.add(Event.Builder.createSessionFactoryStoppedEvent("id2", "tmp-2", "user1@gmail.com")
                                .withDate("2013-02-10").withTime("10:30:00").build());

        events.add(Event.Builder.createSessionFactoryStartedEvent("id3", "tmp-3", "anonymoususer_1", "false", "brType")
                                .withDate("2013-02-10").withTime("11:00:00").build());
        events.add(Event.Builder.createSessionFactoryStoppedEvent("id3", "tmp-3", "anonymoususer_1")
                                .withDate("2013-02-10").withTime("11:15:00").build());

        events.add(Event.Builder.createSessionFactoryStartedEvent("id4", "tmp-4", "anonymoususer_1", "false", "brType")
                                .withDate("2013-02-10").withTime("11:20:00").build());
        events.add(Event.Builder.createSessionFactoryStoppedEvent("id4", "tmp-4", "anonymoususer_1")
                                .withDate("2013-02-10").withTime("11:30:00").build());


        events.add(Event.Builder.createFactoryProjectImportedEvent("tmp-1", "user1@gmail.com", "project", "type")
                                .withDate("2013-02-10").withTime("10:05:00").build());

        events.add(
                Event.Builder
                        .createFactoryUrlAcceptedEvent("tmp-1", "factoryUrl0", "http://referrer1", "org1", "affiliate1")
                        .withDate("2013-02-10").withTime("11:00:00").build());
        events.add(
                Event.Builder
                        .createFactoryUrlAcceptedEvent("tmp-2", "factoryUrl1", "http://referrer2", "org2", "affiliate1")
                        .withDate("2013-02-10").withTime("11:00:01").build());
        events.add(
                Event.Builder
                        .createFactoryUrlAcceptedEvent("tmp-3", "factoryUrl1", "http://referrer2", "org3", "affiliate2")
                        .withDate("2013-02-10").withTime("11:00:02").build());
        events.add(
                Event.Builder
                        .createFactoryUrlAcceptedEvent("tmp-4", "factoryUrl0", "http://referrer3", "org4", "affiliate2")
                        .withDate("2013-02-10").withTime("11:00:03").build());

        events.add(Event.Builder.createTenantCreatedEvent("tmp-1", "user1@gmail.com")
                                .withDate("2013-02-10").withTime("12:00:00").build());
        events.add(Event.Builder.createTenantCreatedEvent("tmp-2", "user1@gmail.com")
                                .withDate("2013-02-10").withTime("12:01:00").build());

        // run event for session #1
        events.add(Event.Builder.createRunStartedEvent("user1@gmail.com", "tmp-1", "project", "type", "id1")
                                .withDate("2013-02-10").withTime("10:03:00").build());

        events.add(Event.Builder.createProjectDeployedEvent("user1@gmail.com", "tmp-1", "session", "project", "type",
                                                            "local")
                                .withDate("2013-02-10")
                                .withTime("10:04:00")
                                .build());

        events.add(Event.Builder.createProjectBuiltEvent("user1@gmail.com", "tmp-1", "session", "project", "type")
                                .withDate("2013-02-10")
                                .withTime("10:04:00")
                                .build());


        // create user
        events.add(Event.Builder.createUserAddedToWsEvent("", "", "", "tmp-3", "anonymoususer_1", "website")
                                .withDate("2013-02-10").build());

        events.add(Event.Builder.createUserChangedNameEvent("anonymoususer_1", "user4@gmail.com").withDate("2013-02-10")
                                .build());

        events.add(Event.Builder.createUserCreatedEvent("user-id2", "user4@gmail.com",  "user4@gmail.com").withDate("2013-02-10").build());


        File log = LogGenerator.generateLog(events);

        Context.Builder builder = new Context.Builder();
        builder.put(Parameters.FROM_DATE, "20130210");
        builder.put(Parameters.TO_DATE, "20130210");
        builder.put(Parameters.LOG, log.getAbsolutePath());

        builder.putAll(scriptsManager.getScript(ScriptType.USERS_PROFILES, MetricType.USERS_PROFILES_LIST).getParamsAsMap());
        pigServer.execute(ScriptType.USERS_PROFILES, builder.build());

        builder.putAll(scriptsManager.getScript(ScriptType.ACCEPTED_FACTORIES, MetricType.FACTORIES_ACCEPTED_LIST).getParamsAsMap());
        pigServer.execute(ScriptType.ACCEPTED_FACTORIES, builder.build());

        builder.putAll(
                scriptsManager.getScript(ScriptType.PRODUCT_USAGE_FACTORY_SESSIONS, MetricType.PRODUCT_USAGE_FACTORY_SESSIONS_LIST).getParamsAsMap());
        pigServer.execute(ScriptType.PRODUCT_USAGE_FACTORY_SESSIONS, builder.build());
    }

    @Test
    public void testReferers() throws Exception {
        Context.Builder builder = new Context.Builder();
        builder.put(Parameters.FROM_DATE, "20130210");
        builder.put(Parameters.TO_DATE, "20130210");

        Metric metric = MetricFactory.getMetric(MetricType.REFERRERS_COUNT_TO_SPECIFIC_FACTORY);
        ListValueData lvd = (ListValueData)metric.getValue(builder.build());

        MapValueData vd = (MapValueData)lvd.getAll().get(0);
        assertEquals(vd.getAll().get("factory").getAsString(), "factoryUrl0");
        assertEquals(vd.getAll().get("unique_referrers_count").getAsString(), "2");

        vd = (MapValueData)lvd.getAll().get(1);
        assertEquals(vd.getAll().get("factory").getAsString(), "factoryUrl1");
        assertEquals(vd.getAll().get("unique_referrers_count").getAsString(), "1");
    }
}