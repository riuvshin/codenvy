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

import com.google.inject.Singleton;
import com.google.inject.name.Named;

import javax.inject.Inject;

/**
 * Keeps auth configurations for AWS Elastic Container Registry.
 * Credential might be configured in .properties files.
 *
 * @author Mykola Morhun
 */
@Singleton
public class AwsInitialAuthConfig {

    private final String awsAccountId;
    private final String region;
    private final String accessKeyId;
    private final String secretAccessKey;
    private final String ecr;

    @Inject
    public AwsInitialAuthConfig(@Named("aws.id") String awsId,
                                @Named("aws.region") String awsRegion,
                                @Named("aws.access-key-id") String accessKeyId,
                                @Named("aws.secret-access-key") String secretAccessKey) {

        this.awsAccountId = awsId;
        this.region = awsRegion;
        this.accessKeyId = accessKeyId;
        this.secretAccessKey = secretAccessKey;

        if (awsId == null && awsRegion == null && accessKeyId == null && secretAccessKey == null) {
            ecr = null;
            return;
        } else {
            if (awsId == null) {
                throw new IllegalArgumentException("AWS Account Id is not configured");
            }
            if (awsRegion == null) {
                throw new IllegalArgumentException("AWS Region is not configured");
            }
            if (accessKeyId == null) {
                throw new IllegalArgumentException("AWS Access Key Id is not configured");
            }
            if (secretAccessKey == null) {
                throw new IllegalArgumentException("AWS Secret Access Key is not configured");
            }
        }

        if (!awsId.matches("[0-9]{12}")) {
            throw new IllegalArgumentException("AWS Account Id has wrong format");
        }

        ecr = awsAccountId + ".dkr.ecr." + region + ".amazonaws.com";
    }

    public String getAwsAccountId() {
        return awsAccountId;
    }

    public String getRegion() {
        return region;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public String getSecretAccessKey() {
        return secretAccessKey;
    }

    public String getEcr() {
        return ecr;
    }

}
