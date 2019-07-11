package com.m3.skinnyrest;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m3.skinnyrest.internal.EndpointBuilder;

public final class Region implements Serializable, Comparable<Region> {
    private static final long serialVersionUID = 1L;
    private static Logger _LOG = LoggerFactory.getLogger(Region.class);

    // LinkedHashMap to ensure stable ordering of registered realms
    private static final Map<String, Region> KNOWN_REGIONS = new LinkedHashMap<String, Region>();
    private static final Map<String, Map<Region, String>> SERVICE_TO_REGION_ENDPOINTS =
            new HashMap<String, Map<Region, String>>();

    // Test Realms
    public static final Region DEV = new Region("dev", "local", Realm.DEV);
    public static final Region DEV2 = new Region("dev2", "local", Realm.DEV);
    public static final Region DEV3 = new Region("dev3", "local", Realm.DEV);
    public static final Region SEA = new Region("sea", "r1", Realm.REGION1);
    public static final Region INTEG_NEXT = new Region("integ-next", "r0", Realm.INTEG_NEXT);
    public static final Region INTEG_STABLE = new Region("integ-stable", "r0", Realm.INTEG_STABLE);

    // OC0
    public static final Region US_RENTON_1 = new Region("us-renton-1", Realm.OC0);

    // OC1
    public static final Region CA_TORONTO_1 = new Region("ca-toronto-1", Realm.OC1);
    // regionCode for FRA shouldn't be needed, but left for backwards compatibility
    public static final Region EU_FRANKFURT_1 = new Region("eu-frankfurt-1", "fra", Realm.OC1);
    // regionCode for LHR shouldn't be needed, but left for backwards compatibility
    public static final Region UK_LONDON_1 = new Region("uk-london-1", "lhr", Realm.OC1);
    public static final Region US_ASHBURN_1 = new Region("us-ashburn-1", "iad", Realm.OC1);
    public static final Region US_PHOENIX_1 = new Region("us-phoenix-1", "phx", Realm.OC1);
    // newer regions
    public static final Region AP_TOKYO_1 = new Region("ap-tokyo-1", Realm.OC1);
    public static final Region AP_SEOUL_1 = new Region("ap-seoul-1", Realm.OC1);
    public static final Region AP_MUMBAI_1 = new Region("ap-mumbai-1", Realm.OC1);
    public static final Region SA_SAOPAULO_1 = new Region("sa-saopaulo-1", Realm.OC1);
    public static final Region EU_ZURICH_1 = new Region("eu-zurich-1", Realm.OC1);
    public static final Region AP_SYDNEY_1 = new Region("ap-sydney-1", Realm.OC1);
    public static final Region EU_AMSTERDAM_1 = new Region("eu-amsterdam-1", Realm.OC1);
    public static final Region ME_JEDDAH_1 = new Region("me-jeddah-1", Realm.OC1);
    public static final Region AP_OSAKA_1 = new Region("ap-osaka-1", Realm.OC1);
    
    // OC2
    public static final Region US_LANGLEY_1 = new Region("us-langley-1", Realm.OC2);
    public static final Region US_LUKE_1 = new Region("us-luke-1", Realm.OC2);
    // OC3
    public static final Region US_GOV_ASHBURN_1 = new Region("us-gov-ashburn-1", Realm.OC3);
    public static final Region US_GOV_CHICAGO_1 = new Region("us-gov-chicago-1", Realm.OC3);
    public static final Region US_GOV_PHOENIX_1 = new Region("us-gov-phoenix-1", Realm.OC3);
    // OC4
    public static final Region UK_GOV_LONDON_1 = new Region("uk-gov-london-1", Realm.OC4);

    private final String regionId;
    private final Realm realm;
    private Optional<String> oldcode;

    private Region(String regionId, Realm realm) {
        this.regionId = regionId;
        this.realm = realm;

        synchronized (KNOWN_REGIONS) {
            // The field name is named after the regionId, but follows enum naming convention.
            // For backwards compatibility, we keep track of the enum-named field.
            KNOWN_REGIONS.put(regionId.toUpperCase().replaceAll("-", "_"), this);
        }
    }

    private Region(String regionId, String regionCode, Realm realm) {
        this(regionId, realm);
        oldcode = Optional.<String>of(regionCode);
    }

    public String getRegionId() { return regionId; }
    public Realm getRealm() { return realm; }
    public String getRegionCode() {
        if (oldcode == null) {
            oldcode = Optional.<String>empty();
        }
        return oldcode.orElse(regionId);
    }

    /**
     * Resolves a service name to its endpoint in the region, if available.
     */
    public Optional<String> getEndpoint(Service service) {
        synchronized (SERVICE_TO_REGION_ENDPOINTS) {
            if (!SERVICE_TO_REGION_ENDPOINTS.containsKey(service.getServiceName())) {
                HashMap<Region, String> endpoints = new HashMap<>();
                endpoints.put(this, formatDefaultRegionEndpoint(service, this));

                SERVICE_TO_REGION_ENDPOINTS.put(service.getServiceName(), endpoints);
                _LOG.info("Loaded service '{}' endpoint mappings: {}",
                            service.getServiceName(), endpoints);
            }

            final Map<Region, String> endpoints =
                    SERVICE_TO_REGION_ENDPOINTS.get(service.getServiceName());
            if (!endpoints.containsKey(this)) {
                endpoints.put(this, formatDefaultRegionEndpoint(service, this));
                _LOG.info("Loaded service '{}' endpoint mappings: {}",
                            service.getServiceName(), endpoints);
            }
            String endpoint = SERVICE_TO_REGION_ENDPOINTS.get(service.getServiceName()).get(this);
            return Optional.ofNullable(endpoint);
        }
    }

    public static Region[] values() {
        synchronized (KNOWN_REGIONS) {
            return KNOWN_REGIONS.values().toArray(new Region[0]);
        }
    }

    /**
     * Returns the Region object matching the specified name. The name must
     * match exactly. (Extraneous whitespace characters are not permitted.)
     */
    public static Region valueOf(String name) throws IllegalArgumentException {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Unknown region");
        }
        final Region region;
        synchronized (KNOWN_REGIONS) {
            region = KNOWN_REGIONS.get(name);
        }
        if (region == null) {
            throw new IllegalArgumentException("Unknown region " + name);
        }
        return region;
    }

    public static String formatDefaultRegionEndpoint(Service service, Region region) {
        return EndpointBuilder.createEndpoint(service, region);
    }

    /**
     * Register a new Region. Used to allow the SDK to be forward compatible with unreleased regions.
     */
    public static Region register(String regionId, Realm realm) {
        if (regionId == null || regionId.isBlank()) {
            throw new IllegalArgumentException("Unknown region");
        }
        if (realm == null) {
            throw new IllegalArgumentException("Unknown realm for region");
        }
        regionId = regionId.toLowerCase(Locale.US);
        synchronized (KNOWN_REGIONS) {
            for (Region region : Region.values()) {
                if (region.regionId.equals(regionId)) {
                    if (!region.getRealm().equals(realm)) {
                        throw new IllegalArgumentException(
                                "RegionId : "
                                        + regionId
                                        + " is already registered with "
                                        + region.getRealm()
                                        + ". It cannot be re-registered with a different realm");
                    }
                    return region;
                }
            }

            return new Region(regionId, realm);
        }
    }

    @Override
    public int compareTo(Region o) {
        int result = regionId.compareTo(o.regionId);
        if (result == 0) {
            result = realm.compareTo(o.getRealm());
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Region)) return false;
        Region other = (Region)o;
        boolean issame = regionId.equalsIgnoreCase(other.getRegionId());
        if (issame) {
            issame = realm.equals(other.getRealm());
        }
        return issame;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((regionId == null) ? 0 : regionId.hashCode());
        result = prime * result + ((realm == null) ? 0 : realm.hashCode());
        return result;
    }

    @Override
    // For backward compatibility maintain the enum toString behavior
    public String toString() {
        return getRegionId().toUpperCase().replaceAll("-", "_");
    }
}
