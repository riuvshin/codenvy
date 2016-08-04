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

import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * @author Mykola Morhun
 */
@Listeners(MockitoTestNGListener.class)
public class AwsInitialAuthConfigTest {

    private static final String AWS_ID                = "123456789012";
    private static final String AWS_REGION            = "ua-north-1";
    private static final String AWS_ACCESS_KEY_ID     = "ABCDEFGHIJKLMNOPQRST";
    private static final String AWS_SECRET_ACCESS_KEY = "vERYverY+veRy+VeRySEcRETkEYfOrACCEss4YOu";

    private static final String AWS_ECR = AWS_ID + ".dkr.ecr." + AWS_REGION + ".amazonaws.com";

    private AwsInitialAuthConfig awsInitialAuthConfig;

    @Test
    public void shouldConfigureAwsCredentials() {
        awsInitialAuthConfig = new AwsInitialAuthConfig(AWS_ID, AWS_REGION, AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY);

        assertEquals(awsInitialAuthConfig.getAwsAccountId(), AWS_ID);
        assertEquals(awsInitialAuthConfig.getRegion(), AWS_REGION);
        assertEquals(awsInitialAuthConfig.getAccessKeyId(), AWS_ACCESS_KEY_ID);
        assertEquals(awsInitialAuthConfig.getSecretAccessKey(), AWS_SECRET_ACCESS_KEY);
        assertEquals(awsInitialAuthConfig.getEcr(), AWS_ECR);
    }

    @Test
    public void shouldConfigureEmptyCredentials() {
        awsInitialAuthConfig = new AwsInitialAuthConfig(null, null, null, null);

        assertNull(awsInitialAuthConfig.getAwsAccountId());
        assertNull(awsInitialAuthConfig.getRegion());
        assertNull(awsInitialAuthConfig.getAccessKeyId());
        assertNull(awsInitialAuthConfig.getSecretAccessKey());
        assertNull(awsInitialAuthConfig.getEcr());
    }

    @Test
    public void shouldConfigureEmptyCredentialsWithEmptyStrings() {
        awsInitialAuthConfig = new AwsInitialAuthConfig("", "", "", "");

        assertNull(awsInitialAuthConfig.getAwsAccountId());
        assertNull(awsInitialAuthConfig.getRegion());
        assertNull(awsInitialAuthConfig.getAccessKeyId());
        assertNull(awsInitialAuthConfig.getSecretAccessKey());
        assertNull(awsInitialAuthConfig.getEcr());
    }

    @Test
    public void shouldConfigureEmptyCredentialsWhenGivenNullsAndEmptyStrings() {
        awsInitialAuthConfig = new AwsInitialAuthConfig(null, "", null, "");

        assertNull(awsInitialAuthConfig.getAwsAccountId());
        assertNull(awsInitialAuthConfig.getRegion());
        assertNull(awsInitialAuthConfig.getAccessKeyId());
        assertNull(awsInitialAuthConfig.getSecretAccessKey());
        assertNull(awsInitialAuthConfig.getEcr());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfAwsIdIsNull() {
        awsInitialAuthConfig = new AwsInitialAuthConfig(null, AWS_REGION, AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfAwsRegionIsNull() {
        awsInitialAuthConfig = new AwsInitialAuthConfig(AWS_ID, null, AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfAwsAccessKeyIsNull() {
        awsInitialAuthConfig = new AwsInitialAuthConfig(AWS_ID, AWS_REGION, null, AWS_SECRET_ACCESS_KEY);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfAwsSecretKeyIsNull() {
        awsInitialAuthConfig = new AwsInitialAuthConfig(AWS_ID, AWS_REGION, AWS_ACCESS_KEY_ID, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfAwsIdIsEmpty() {
        awsInitialAuthConfig = new AwsInitialAuthConfig("", AWS_REGION, AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfAwsRegionIsEmpty() {
        awsInitialAuthConfig = new AwsInitialAuthConfig(AWS_ID, "", AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfAwsAccessKeyIsEmpty() {
        awsInitialAuthConfig = new AwsInitialAuthConfig(AWS_ID, AWS_REGION, "", AWS_SECRET_ACCESS_KEY);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfAwsSecretKeyIsEmpty() {
        awsInitialAuthConfig = new AwsInitialAuthConfig(AWS_ID, AWS_REGION, AWS_ACCESS_KEY_ID, "");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfAwsAccountIdHasWrongLength() {
        awsInitialAuthConfig = new AwsInitialAuthConfig(AWS_ID + '1', AWS_REGION, AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY);
    }

}
