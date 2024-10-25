package com.serch.server.admin.services.projections;

/**
 * Represents a projection for metrics in the system.
 * This interface defines the structure for metric data that includes a header
 * and a count, allowing for the retrieval of summarized information related
 * to specific metrics.
 */
public interface MetricProjection {

    /**
     * Retrieves the header associated with the metric.
     * This header typically serves as a label or identifier that describes
     * the type or category of the metric being represented.
     *
     * @return An {@link Integer} representing the header of the metric.
     *         The specific meaning of the header value should be defined
     *         in the context where this metric is used. It may correspond
     *         to an identifier or a classification key for metrics.
     */
    Integer getHeader();

    /**
     * Retrieves the count associated with the metric.
     * This count represents the aggregated value of the metric, such as
     * the total number of occurrences or the sum of specific values within
     * the defined scope.
     *
     * @return A {@link Long} representing the count of the metric.
     *         This value provides the quantitative measure of the metric
     *         and can be used for analysis, reporting, or visualization.
     */
    Long getCount();
}