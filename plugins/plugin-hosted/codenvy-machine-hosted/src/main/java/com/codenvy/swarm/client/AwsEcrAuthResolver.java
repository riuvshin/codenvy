/*******************************************************************************
 * Copyright (c) 2012-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.swarm.client;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ecr.AmazonECRClient;
import com.amazonaws.services.ecr.model.AuthorizationData;
import com.amazonaws.services.ecr.model.GetAuthorizationTokenRequest;
import com.amazonaws.services.ecr.model.GetAuthorizationTokenResult;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.plugin.docker.client.DockerRegistryDynamicAuthResolver;
import org.eclipse.che.plugin.docker.client.dto.AuthConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.eclipse.che.dto.server.DtoFactory.newDto;

/**
 * This class provides actual auth config for AWS ECR.
 *
 * @author Mykola Morhun
 */
@Singleton
public class AwsEcrAuthResolver implements DockerRegistryDynamicAuthResolver {
    private static final Logger LOG = LoggerFactory.getLogger(DockerRegistryDynamicAuthResolver.class);

    private final AwsInitialAuthConfig awsInitialAuthConfig;

    @Inject
    public AwsEcrAuthResolver(AwsInitialAuthConfig awsInitialAuthConfig) {
        this.awsInitialAuthConfig = awsInitialAuthConfig;
    }

    /**
     * Retrieves actual auth data for Amazon ECR.
     * Returns null if no credential configured for specified registry.
     * Note, that credentials is changed every 12 hours.
     *
     * @return actual auth config for specified AWS ECR or null if no credentials configured
     */
    @Override
    public AuthConfig getDynamicXRegistryAuth(String registry) {
        if (registry == null || !registry.equals(awsInitialAuthConfig.getEcr())) {
            return null;
        }

        String authorizationToken = getAwsAuthorizationToken();
        if (authorizationToken == null) {
            return null;
        }
        String decodedAuthorizationToken = new String(Base64.getDecoder().decode(authorizationToken));
        int colonIndex = decodedAuthorizationToken.indexOf(':');
        if (colonIndex == -1) {
            LOG.warn("Cannot retrieve ECR credentials from token");
            return null;
        }

        return newDto(AuthConfig.class).withUsername(decodedAuthorizationToken.substring(0, colonIndex))
                                       .withPassword(decodedAuthorizationToken.substring(colonIndex + 1));
    }

    /**
     * Retrieves actual auth config for Amazon ECR.
     * Returns null if no AWS ECR credentials configured.
     *
     * @return actual AWS ECR auth config or empty map if ECR not configured
     */
    @Override
    public Map<String, AuthConfig> getDynamicXRegistryConfig() {
        Map<String, AuthConfig> dynamicAuthConfigs = new HashMap<>();

        AuthConfig authConfig = getDynamicXRegistryAuth(awsInitialAuthConfig.getEcr());
        if (authConfig != null) {
            dynamicAuthConfigs.put(awsInitialAuthConfig.getEcr(), authConfig);
        }

        return dynamicAuthConfigs;
    }

    private String getAwsAuthorizationToken() {
        // TODO catch all Exceptions
        AWSCredentials credentials = new BasicAWSCredentials(awsInitialAuthConfig.getAccessKeyId(),
                                                             awsInitialAuthConfig.getSecretAccessKey());
        AmazonECRClient amazonECRClient = new AmazonECRClient(credentials);
        GetAuthorizationTokenResult tokenResult = amazonECRClient.getAuthorizationToken(new GetAuthorizationTokenRequest());
        List<AuthorizationData> authData = tokenResult.getAuthorizationData();
        if (authData.isEmpty() || authData.get(0).getAuthorizationToken() == null) {
            LOG.warn("Failed to retrieve AWS ECR token");
            return null;
        }
        return authData.get(0).getAuthorizationToken();
    }

}
