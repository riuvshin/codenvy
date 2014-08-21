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
package com.codenvy.factory.workspace;

import com.codenvy.api.core.ApiException;
import com.codenvy.api.core.notification.EventService;
import com.codenvy.api.core.notification.EventSubscriber;
import com.codenvy.api.core.rest.HttpJsonHelper;
import com.codenvy.api.core.rest.shared.dto.Link;
import com.codenvy.api.event.workspace.CreateWorkspaceEvent;
import com.codenvy.api.factory.FactoryBuilder;
import com.codenvy.api.factory.dto.Factory;
import com.codenvy.api.workspace.server.dao.Workspace;
import com.codenvy.api.workspace.server.dao.WorkspaceDao;
import com.codenvy.commons.lang.Pair;
import com.codenvy.dto.server.DtoFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Set attributes to temporary workspaces that make runner/builder use custom resources for factory
 *
 * @author Alexander Garagatyi
 */
@Singleton
public class FactoryWorkspaceResourceProvider implements EventSubscriber<CreateWorkspaceEvent> {
    private static final Logger  LOG                            = LoggerFactory.getLogger(FactoryWorkspaceResourceProvider.class);
    private static final String  RUNNER_LIFETIME                = "factory.runner.lifetime";
    private static final String  RUNNER_RAM                     = "factory.runner.ram";
    private static final String  BUILDER_EXECUTION_TIME         = "factory.builder.execution_time";
    private static final String  TRACKED_RUNNER_LIFETIME        = "factory.tracked.runner.lifetime";
    private static final String  TRACKED_RUNNER_RAM             = "factory.tracked.runner.ram";
    private static final String  TRACKED_BUILDER_EXECUTION_TIME = "factory.tracked.builder.execution_time";
    private static final Pattern ID_PATTERN                     = Pattern.compile("(?:\\?)id=([^&].*)");

    private final String runnerLifetime;
    private final String runnerRam;
    private final String builderExecutionTime;
    private final String trackedRunnerRam;
    private final String trackedRunnerLifetime;
    private final String trackedBuilderExecutionTime;
    private final String apiEndpoint;

    private final WorkspaceDao   workspaceDao;
    private final EventService   eventService;
    private final FactoryBuilder factoryBuilder;

    @Inject
    public FactoryWorkspaceResourceProvider(@Named(TRACKED_RUNNER_RAM) String trackedRunnerRam,
                                            @Named(TRACKED_RUNNER_LIFETIME) String trackedRunnerLifetime,
                                            @Named(TRACKED_BUILDER_EXECUTION_TIME) String trackedBuilderExecutionTime,
                                            @Nullable @Named(RUNNER_LIFETIME) String runnerLifetime,
                                            @Nullable @Named(RUNNER_RAM) String runnerRam,
                                            @Nullable @Named(BUILDER_EXECUTION_TIME) String builderExecutionTime,
                                            @Named("api.endpoint") String apiEndpoint,
                                            WorkspaceDao workspaceDao,
                                            EventService eventService,
                                            FactoryBuilder factoryBuilder) {
        this.trackedRunnerRam = trackedRunnerRam;
        this.trackedRunnerLifetime = trackedRunnerLifetime;
        this.trackedBuilderExecutionTime = trackedBuilderExecutionTime;
        this.runnerLifetime = runnerLifetime;
        this.runnerRam = runnerRam;
        this.builderExecutionTime = builderExecutionTime;
        this.apiEndpoint = apiEndpoint;
        this.workspaceDao = workspaceDao;
        this.eventService = eventService;
        this.factoryBuilder = factoryBuilder;
    }

    @PostConstruct
    public void subscribe() {
        eventService.subscribe(this);
    }

    @PreDestroy
    public void unsubscribe() {
        eventService.unsubscribe(this);
    }

    @Override
    public void onEvent(CreateWorkspaceEvent event) {
        if (event.isTemporary()) {
            try {
                final Workspace workspace = workspaceDao.getById(event.getWorkspaceId());
                final Map<String, String> attributes = workspace.getAttributes();

                String factoryUrl = attributes.get("factoryUrl");
                if (factoryUrl != null) {
                    factoryUrl = URLDecoder.decode(factoryUrl, "UTF-8");
                    final Matcher matcher = ID_PATTERN.matcher(factoryUrl);
                    Factory factory;
                    if (matcher.find()) {
                        Link factoryObjectLink =
                                DtoFactory.getInstance().createDto(Link.class).withMethod("GET")
                                          .withHref(UriBuilder.fromUri(apiEndpoint).path("factory/" + matcher.group(1)).build().toString());
                        factory = HttpJsonHelper.request(Factory.class, factoryObjectLink, Pair.of("validate", false));
                    } else {
                        factory = factoryBuilder.buildNonEncoded(URI.create(factoryUrl));
                    }

                    if (factory.getOrgid() != null) {
                        // factory workspace with subscription
                        attributes.put("codenvy:runner_lifetime", trackedRunnerLifetime);
                        attributes.put("codenvy:builder_execution_time", trackedBuilderExecutionTime);
                        attributes.put("codenvy:runner_ram", trackedRunnerRam);

                        workspaceDao.update(workspace.withAttributes(attributes));
                        return;
                    }
                }

                // common factory workspace
                setIfValuePresents(attributes, "codenvy:runner_lifetime", runnerLifetime);
                setIfValuePresents(attributes, "codenvy:runner_ram", runnerRam);
                setIfValuePresents(attributes, "codenvy:builder_execution_time", builderExecutionTime);

                workspaceDao.update(workspace.withAttributes(attributes));
            } catch (ApiException | IOException e) {
                LOG.error(e.getLocalizedMessage(), e);
            }
        }
    }

    private void setIfValuePresents(Map<String, String> attributes, String key, String value) {
        if (value != null && !value.isEmpty()) {
            attributes.put(key, value);
        }
    }
}