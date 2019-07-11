package com.m3.skinnyrest.internal;

import com.m3.skinnyrest.Realm;
import com.m3.skinnyrest.Region;
import com.m3.skinnyrest.Service;

/**
 * EndpointBuilder provides a wrapper to construct the appropriate endpoint for a service.
 * The service may override the endpoint template, but
 * if not, a default template will be used.
 */
public final class EndpointBuilder {
    public static final String DEFAULT_ENDPOINT_TEMPLATE =
            "https://{serviceEndpointPrefix}.{region}.{secondLevelDomain}";

    public static String createEndpoint(Service service, String regionId, Realm realm) {
        final String endpointTemplateToUse;
        if (service.getServiceEndpointTemplate() != null && !service.getServiceEndpointTemplate().isBlank()) {
            endpointTemplateToUse = service.getServiceEndpointTemplate();
        } else {
            endpointTemplateToUse = DEFAULT_ENDPOINT_TEMPLATE;
        }

        return DefaultEndpointConfiguration.builder(endpointTemplateToUse)
                .regionId(regionId)
                .serviceEndpointPrefix(service.getServiceEndpointPrefix())
                .secondLevelDomain(realm.getSecondLevelDomain())
                .build();
    }

    public static String createEndpoint(Service service, Region region) {
        return createEndpoint(service, region.getRegionId(), region.getRealm());
    }

    private EndpointBuilder() { }
}
