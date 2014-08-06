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
package com.codenvy.analytics.metrics;

/** @author <a href="mailto:abazko@codenvy.com">Anatoliy Bazko</a> */
public enum MetricFilter {
    PARAMETERS,

    _ID,
    DATE(true),

    WS, // wsId
    WS_NAME, // name of workspace
    PERSISTENT_WS(true),

    USER, // userId
    ALIASES, // user' emails
    REGISTERED_USER(true),

    EVENT,
    DOMAIN,
    USER_COMPANY,
    USER_LAST_NAME,
    USER_FIRST_NAME,

    ACTION,
    SOURCE,

    ORG_ID,
    FACTORY,
    ENCODED_FACTORY(true),
    TIME(true),
    REFERRER,
    REPOSITORY,
    SESSION_ID,
    AFFILIATE_ID,
    PROJECT,
    ACCOUNT_ID,
    DATA_UNIVERSE(true),
    PROJECT_TYPE;

    private boolean isNumeric;

    MetricFilter(boolean isNumeric) {
        this.isNumeric = isNumeric;
    }

    MetricFilter() {
        this.isNumeric = false;
    }

    public boolean isNumericType() {
        return isNumeric;
    }
}
