package com.m3.skinnyrest.internal;

public final class DefaultEndpointConfiguration {
    private static final String SERVICE_ENDPOINT_PREFIX_TEMPLATE = "{serviceEndpointPrefix}";
    private static final String REGION_ID_TEMPLATE =
            "{region}"; // not regionId for backwards compatibility
    private static final String SECOND_LEVEL_DOMAIN_TEMPLATE = "{secondLevelDomain}";

    // the endpoint template that will be used.
    private final String endpointTemplate;

    private String serviceEndpointPrefix;
    private String regionId;
    private String secondLevelDomain;

    public static DefaultEndpointConfiguration builder(String endpointTemplate) {
        if (endpointTemplate == null || endpointTemplate.isBlank())
            throw new IllegalArgumentException("Invalid template provided for EndpointConfiguration");
        return new DefaultEndpointConfiguration(endpointTemplate);
    }

    public DefaultEndpointConfiguration serviceEndpointPrefix(String serviceEndpointPrefix) {
        this.serviceEndpointPrefix = serviceEndpointPrefix;
        return this;
    }

    public DefaultEndpointConfiguration regionId(String regionId) {
        this.regionId = regionId;
        return this;
    }

    public DefaultEndpointConfiguration secondLevelDomain(String secondLevelDomain) {
        this.secondLevelDomain = secondLevelDomain;
        return this;
    }

    public String build() {
        String endpoint = endpointTemplate;
        if (serviceEndpointPrefix != null)
            endpoint = endpoint.replace(SERVICE_ENDPOINT_PREFIX_TEMPLATE, serviceEndpointPrefix);
        if (regionId != null)
            endpoint = endpoint.replace(REGION_ID_TEMPLATE, regionId);
        if (secondLevelDomain != null)
            endpoint = endpoint.replace(SECOND_LEVEL_DOMAIN_TEMPLATE, secondLevelDomain);
        return endpoint;
    }

    private DefaultEndpointConfiguration(String thetemplate) {
        endpointTemplate = thetemplate;
    }
}
