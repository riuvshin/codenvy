/*
 *
 * CODENVY CONFIDENTIAL
 * ________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 * NOTICE: All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any. The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.analytics.metrics;

import java.util.Set;

/** @author <a href="mailto:abazko@codenvy.com">Anatoliy Bazko</a> */
public abstract class CalculatedMetric extends AbstractMetric {

    protected final Metric basedMetric;

    protected CalculatedMetric(String metricName, String basedMetric) {
        super(metricName);
        this.basedMetric = MetricFactory.getMetric(basedMetric);
    }

    protected CalculatedMetric(MetricType metricType, MetricType basedMetric) {
        this(metricType.name(), basedMetric.name());
    }

    /** {@inheritDoc} */
    @Override
    public Set<Parameters> getParams() {
        return basedMetric.getParams();
    }
}
