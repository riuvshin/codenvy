/*
 *  [2012] - [2016] Codenvy, S.A.
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
package com.codenvy.swarm.client;

import com.google.inject.Singleton;
import com.google.inject.name.Named;

import org.eclipse.che.commons.annotation.Nullable;

import javax.inject.Inject;

import static com.google.common.base.Strings.isNullOrEmpty;

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
    public AwsInitialAuthConfig(@Nullable @Named("aws.id") String awsId,
                                @Nullable @Named("aws.region") String awsRegion,
                                @Nullable @Named("aws.access-key-id") String accessKeyId,
                                @Nullable @Named("aws.secret-access-key") String secretAccessKey) {
        if (isNullOrEmpty(awsId) && isNullOrEmpty(awsRegion) && isNullOrEmpty(accessKeyId) && isNullOrEmpty(secretAccessKey)) {
            this.awsAccountId = null;
            this.region = null;
            this.accessKeyId = null;
            this.secretAccessKey = null;

            this.ecr = null;
            return;
        } else {
            if (isNullOrEmpty(awsId)) {
                throw new IllegalArgumentException("AWS Account Id is not configured");
            }
            if (isNullOrEmpty(awsRegion)) {
                throw new IllegalArgumentException("AWS Region is not configured");
            }
            if (isNullOrEmpty(accessKeyId)) {
                throw new IllegalArgumentException("AWS Access Key Id is not configured");
            }
            if (isNullOrEmpty(secretAccessKey)) {
                throw new IllegalArgumentException("AWS Secret Access Key is not configured");
            }
        }

        if (!awsId.matches("[0-9]{12}")) {
            throw new IllegalArgumentException("AWS Account Id has wrong format");
        }

        this.awsAccountId = awsId;
        this.region = awsRegion;
        this.accessKeyId = accessKeyId;
        this.secretAccessKey = secretAccessKey;

        this.ecr = awsAccountId + ".dkr.ecr." + region + ".amazonaws.com";
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
