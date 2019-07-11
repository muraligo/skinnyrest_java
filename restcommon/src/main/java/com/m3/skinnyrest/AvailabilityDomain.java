package com.m3.skinnyrest;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public final class AvailabilityDomain implements Serializable, Comparable<AvailabilityDomain> {
    private static final long serialVersionUID = 1L;
//    private static Logger _LOG = LoggerFactory.getLogger(AvailabilityDomain.class);

    // LinkedHashMap to ensure stable ordering of registered realms
    private static final Map<String, AvailabilityDomain> KNOWN_DOMAINS = new LinkedHashMap<String, AvailabilityDomain>();

    // Test Realms
    public static final AvailabilityDomain DEV_1 = new AvailabilityDomain("dev-1", "dev1", Region.DEV);
    public static final AvailabilityDomain DEV_2 = new AvailabilityDomain("dev-2", "dev2", Region.DEV);
    public static final AvailabilityDomain DEV_3 = new AvailabilityDomain("dev-3", "dev3", Region.DEV);
    public static final AvailabilityDomain DEV2_1 = new AvailabilityDomain("dev2-1", "d2-1", Region.DEV2);
    public static final AvailabilityDomain DEV2_2 = new AvailabilityDomain("dev2-2", "d2-2", Region.DEV2);
    public static final AvailabilityDomain DEV2_3 = new AvailabilityDomain("dev2-3", "d2-3", Region.DEV2);
    public static final AvailabilityDomain DEV3_1 = new AvailabilityDomain("dev3-1", "d3-1", Region.DEV3);
    public static final AvailabilityDomain DEV3_2 = new AvailabilityDomain("dev3-2", "d3-2", Region.DEV3);
    public static final AvailabilityDomain DEV3_3 = new AvailabilityDomain("dev3-3", "d3-3", Region.DEV3);
    public static final AvailabilityDomain SEA_AD_1 = new AvailabilityDomain("sea-ad-1", "se-1", Region.SEA);
    public static final AvailabilityDomain SEA_AD_2 = new AvailabilityDomain("sea-ad-2", "se-2", Region.SEA);
    public static final AvailabilityDomain SEA_AD_3 = new AvailabilityDomain("sea-ad-3", "se-3", Region.SEA);
    public static final AvailabilityDomain INTEG_NEXT_1 = new AvailabilityDomain("integ-next-1", "in-1", Region.INTEG_NEXT);
    public static final AvailabilityDomain INTEG_STABLE_1 = new AvailabilityDomain("integ-stable-1", "is-1", Region.INTEG_STABLE);

    // OC0
    public static final AvailabilityDomain US_RENTON_1_AD_1 = new AvailabilityDomain("us-renton-1-ad-1", "rt-1", Region.US_RENTON_1);

    // OC1
    public static final AvailabilityDomain US_PHOENIX_AD_1 = new AvailabilityDomain("phx-ad-1", "px-1", Region.US_PHOENIX_1);
    public static final AvailabilityDomain US_PHOENIX_AD_2 = new AvailabilityDomain("phx-ad-2", "px-2", Region.US_PHOENIX_1);
    public static final AvailabilityDomain US_PHOENIX_AD_3 = new AvailabilityDomain("phx-ad-3", "px-3", Region.US_PHOENIX_1);
    public static final AvailabilityDomain US_ASHBURN_AD_1 = new AvailabilityDomain("iad-ad-1", "ia-1", Region.US_ASHBURN_1);
    public static final AvailabilityDomain US_ASHBURN_AD_2 = new AvailabilityDomain("iad-ad-2", "ia-2", Region.US_ASHBURN_1);
    public static final AvailabilityDomain US_ASHBURN_AD_3 = new AvailabilityDomain("iad-ad-3", "ia-3", Region.US_ASHBURN_1);
    public static final AvailabilityDomain US_ASHBURN_POP_1 = new AvailabilityDomain("iad-pop-1", "iap1", Region.US_ASHBURN_1);
    public static final AvailabilityDomain US_ASHBURN_POP_2 = new AvailabilityDomain("iad-pop-2", "iap2", Region.US_ASHBURN_1);
    public static final AvailabilityDomain EU_FRANKFURT_1_AD_1 = new AvailabilityDomain("eu-frankfurt-1-ad-1", "fr-1", Region.EU_FRANKFURT_1);
    public static final AvailabilityDomain EU_FRANKFURT_1_AD_2 = new AvailabilityDomain("eu-frankfurt-1-ad-2", "fr-2", Region.EU_FRANKFURT_1);
    public static final AvailabilityDomain EU_FRANKFURT_1_AD_3 = new AvailabilityDomain("eu-frankfurt-1-ad-3", "fr-3", Region.EU_FRANKFURT_1);
    public static final AvailabilityDomain EU_FRANKFURT_1_POP_1 = new AvailabilityDomain("eu-frankfurt-1-pop-1", "fr-4", Region.EU_FRANKFURT_1);
    public static final AvailabilityDomain UK_LONDON_1_AD_1 = new AvailabilityDomain("uk-london-1-ad-1", "ld-1", Region.UK_LONDON_1);
    public static final AvailabilityDomain UK_LONDON_1_AD_2 = new AvailabilityDomain("uk-london-1-ad-2", "ld-2", Region.UK_LONDON_1);
    public static final AvailabilityDomain UK_LONDON_1_AD_3 = new AvailabilityDomain("uk-london-1-ad-3", "ld-3", Region.UK_LONDON_1);
    public static final AvailabilityDomain UK_LONDON_1_POP_1 = new AvailabilityDomain("uk-london-1-pop-1", "ld-4", Region.UK_LONDON_1);
    public static final AvailabilityDomain UK_LONDON_1_POP_2 = new AvailabilityDomain("uk-london-1-pop-2", "ld-5", Region.UK_LONDON_1);
    public static final AvailabilityDomain CA_TORONTO_1_AD_1 = new AvailabilityDomain("ca-toronto-1-ad-1", "to-1", Region.CA_TORONTO_1);
    public static final AvailabilityDomain CA_TORONTO_1_POP_1 = new AvailabilityDomain("ca-toronto-1-pop-1", "to-4", Region.CA_TORONTO_1);
    // newer regions
    public static final AvailabilityDomain AP_TOKYO_1_AD_1 = new AvailabilityDomain("ap-tokyo-1-ad-1", "nt-1", Region.AP_TOKYO_1);
    public static final AvailabilityDomain AP_TOKYO_1_POP_1 = new AvailabilityDomain("ap-tokyo-1-pop-1", "nt-4", Region.AP_TOKYO_1);
    public static final AvailabilityDomain AP_SEOUL_1_AD_1 = new AvailabilityDomain("ap-seoul-1-ad-1", "ic-1", Region.AP_SEOUL_1);
    public static final AvailabilityDomain AP_SEOUL_1_POP_1 = new AvailabilityDomain("ap-seoul-1-pop-1", "ic-4", Region.AP_SEOUL_1);
    public static final AvailabilityDomain AP_MUMBAI_1_AD_1 = new AvailabilityDomain("ap-mumbai-1-ad-1", "bo-1", Region.AP_MUMBAI_1);
    public static final AvailabilityDomain AP_MUMBAI_1_POP_1 = new AvailabilityDomain("ap-mumbai-1-pop-1", "bo-4", Region.AP_MUMBAI_1);
    public static final AvailabilityDomain SA_SAOPAULO_1_AD_1 = new AvailabilityDomain("sa-saopaulo-1-ad-1", "gr-1", Region.SA_SAOPAULO_1);
    public static final AvailabilityDomain SA_SAOPAULO_1_POP_1 = new AvailabilityDomain("sa-saopaulo-1-pop-1", "gr-4", Region.SA_SAOPAULO_1);
    public static final AvailabilityDomain EU_ZURICH_1_AD_1 = new AvailabilityDomain("eu-zurich-1-ad-1", "zr-1", Region.EU_ZURICH_1);
    public static final AvailabilityDomain EU_ZURICH_1_POP_1 = new AvailabilityDomain("eu-zurich-1-pop-1", "zr-4", Region.EU_ZURICH_1);
    public static final AvailabilityDomain AP_SYDNEY_1_AD_1 = new AvailabilityDomain("ap-sydney-1-ad-1", "sy-1", Region.AP_SYDNEY_1);
    public static final AvailabilityDomain AP_SYDNEY_1_POP_1 = new AvailabilityDomain("ap-sydney-1-pop-1", "sy-4", Region.AP_SYDNEY_1);
    public static final AvailabilityDomain EU_AMSTERDAM_1_AD_1 = new AvailabilityDomain("eu-amsterdam-1-ad-1", "am-1", Region.EU_AMSTERDAM_1);
    public static final AvailabilityDomain EU_AMSTERDAM_1_POP_1 = new AvailabilityDomain("eu-amsterdam-1-pop-1", "am-4", Region.EU_AMSTERDAM_1);
    public static final AvailabilityDomain EU_AMSTERDAM_1_POP_2 = new AvailabilityDomain("eu-amsterdam-1-pop-2", "am-5", Region.EU_AMSTERDAM_1);
    public static final AvailabilityDomain ME_JEDDAH_1_AD_1 = new AvailabilityDomain("me-jeddah-1-ad-1", "je-1", Region.ME_JEDDAH_1);
    public static final AvailabilityDomain AP_OSAKA_1_AD_1 = new AvailabilityDomain("ap-osaka-1-ad-1", "ki-1", Region.AP_OSAKA_1);
    public static final AvailabilityDomain AP_OSAKA_1_POP_1 = new AvailabilityDomain("ap-osaka-1-pop-1", "ki-4", Region.AP_OSAKA_1);
    
    // OC2
    public static final AvailabilityDomain US_LANGLEY_1_AD_1 = new AvailabilityDomain("us-langley-1-ad-1", "lf-1", Region.US_LANGLEY_1);
    public static final AvailabilityDomain US_LANGLEY_1_POP_1 = new AvailabilityDomain("us-langley-1-pop-1", "lf-2", Region.US_LANGLEY_1);
    public static final AvailabilityDomain US_LUKE_1_AD_1 = new AvailabilityDomain("us-luke-1-ad-1", "lu-1", Region.US_LUKE_1);
    public static final AvailabilityDomain US_LUKE_1_POP_1 = new AvailabilityDomain("us-luke-1-pop-1", "lu-2", Region.US_LUKE_1);
    // OC3
    public static final AvailabilityDomain US_GOV_ASHBURN_1_AD_1 = new AvailabilityDomain("us-gov-ashburn-1-ad-1", "ri-1", Region.US_GOV_ASHBURN_1);
    public static final AvailabilityDomain US_GOV_ASHBURN_1_POP_1 = new AvailabilityDomain("us-gov-ashburn-1-pop-1", "ri-2", Region.US_GOV_ASHBURN_1);
    public static final AvailabilityDomain US_GOV_CHICAGO_1_AD_1 = new AvailabilityDomain("us-gov-chicago-1-ad-1", "pi-1", Region.US_GOV_CHICAGO_1);
    public static final AvailabilityDomain US_GOV_PHOENIX_1_AD_1 = new AvailabilityDomain("us-gov-phoenix-1-ad-1", "tu-1", Region.US_GOV_PHOENIX_1);
    // OC4
    public static final AvailabilityDomain UK_GOV_LONDON_1_AD_1 = new AvailabilityDomain("uk-gov-london-1-ad-1", "lt-1", Region.UK_GOV_LONDON_1);

    // Reserved AD for region-wide entities. AD code is 0000
    // It is set up with no Region
    // This value is here to ensure no AD is created with the reserved AD code.
    public static final AvailabilityDomain NO_AD = new AvailabilityDomain("RESERVED_NO_AD", new String(new byte[] {0,0,0,0}, StandardCharsets.UTF_8), null);

    private final String name;
    private final Region region;
    private Optional<String> oldcode;

    private AvailabilityDomain(String adname, String adCode, Region region) {
        name = adname;
        this.region = region;
        if (adCode != null) oldcode = Optional.<String>of(adCode);

        synchronized (KNOWN_DOMAINS) {
            // The field name is named after the regionId, but follows enum naming convention.
            // For backwards compatibility, we keep track of the enum-named field.
            KNOWN_DOMAINS.put(adname.toUpperCase().replaceAll("-", "_"), this);
        }
    }

    public String getName() { return name; }
    public Region getRegion() { return region; }
    public String getIdCode() {
        if (oldcode == null) {
            oldcode = Optional.<String>empty();
        }
        return oldcode.orElse(name);
    }

    /**
     * Examples:
     * "phx-ad-3" -&gt; "ad3"
     * "integ-stable-1" -&gt; "ad1"
     * 
     * @return the AD name in the number format ad[1-9][0-9]*
     */
    public String getAdNumberName() {
        String base = (getName().contains("-pop-")) ? "pop" : "ad";
        String[] parts = getName().split("-");
        return base + parts[parts.length - 1];
    }

    public static AvailabilityDomain[] values() {
        synchronized (KNOWN_DOMAINS) {
            return KNOWN_DOMAINS.values().toArray(new AvailabilityDomain[0]);
        }
    }

    /**
     * Returns the Availability Domain object matching the specified name. The name must
     * match exactly. (Extraneous whitespace characters are not permitted.)
     */
    public static AvailabilityDomain valueOf(String name) throws IllegalArgumentException {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Unknown region");
        }
        final AvailabilityDomain region;
        synchronized (KNOWN_DOMAINS) {
            region = KNOWN_DOMAINS.get(name);
        }
        if (region == null) {
            throw new IllegalArgumentException("Unknown region " + name);
        }
        return region;
    }

    /**
     * Register a new Availability Domain. Used to allow the SDK to be forward compatible with unreleased regions.
     */
    public static AvailabilityDomain register(String adname, Region region) {
        if (adname == null || adname.isBlank()) {
            throw new IllegalArgumentException("Unknown availability domain");
        }
        if (region == null) {
            throw new IllegalArgumentException("Unknown region for availability domain");
        }
        adname = adname.toLowerCase(Locale.US);
        synchronized (KNOWN_DOMAINS) {
            for (AvailabilityDomain ad : AvailabilityDomain.values()) {
                if (ad.name.equals(adname)) {
                    if (!ad.getRegion().equals(region)) {
                        throw new IllegalArgumentException(
                                "AvailabilityDomain : "
                                        + adname
                                        + " is already registered with "
                                        + ad.getRegion()
                                        + ". It cannot be re-registered with a different region");
                    }
                    return ad;
                }
            }

            return new AvailabilityDomain(adname, null, region);
        }
    }

    @Override
    public int compareTo(AvailabilityDomain o) {
        int result = name.compareTo(o.name);
        if (result == 0) {
            result = region.compareTo(o.getRegion());
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AvailabilityDomain)) return false;
        AvailabilityDomain other = (AvailabilityDomain)o;
        boolean issame = name.equalsIgnoreCase(other.getName());
        if (issame) {
            issame = region.equals(other.getRegion());
        }
        return issame;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((region == null) ? 0 : region.hashCode());
        return result;
    }

    @Override
    // For backward compatibility maintain the enum toString behavior
    public String toString() {
        return getName().toUpperCase().replaceAll("-", "_");
    }
}
