package com.m3.skinnyrest;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public final class Realm implements Serializable, Comparable<Realm> {
    private static final long serialVersionUID = 1L;
    // LinkedHashMap to ensure stable ordering of registered realms
    private static final Map<String, Realm> KNOWN_REALMS = new LinkedHashMap<String, Realm>();

    public static final Realm OC1 = new Realm("oc1", "oraclecloud.com");
    public static final Realm OC2 = new Realm("oc2", "oraclegovcloud.com");
    public static final Realm OC3 = new Realm("oc3", "oraclegovcloud.com");
    public static final Realm OC0 = new Realm("oc0", ""); // TODO find the domain
    public static final Realm OC4 = new Realm("oc4", "oraclegovcloud.com"); // TODO find the domain
    public static final Realm DEV = new Realm("dev", ""); // TODO find the domain
    public static final Realm DESKTOP = new Realm("desktop", "localhost"); // TODO find the domain
    public static final Realm INTEG_NEXT = new Realm("integ-next", ""); // TODO find the domain
    public static final Realm INTEG_STABLE = new Realm("integ-stable", ""); // TODO find the domain
    public static final Realm REGION1 = new Realm("region1", ""); // TODO find the domain

    private final String realmId;
    private final String secondLevelDomain;

    private Realm(String realmId, String secondLevelDomain) {
        this.realmId = realmId;
        this.secondLevelDomain = secondLevelDomain;

        synchronized (KNOWN_REALMS) {
            // The field name is named after the regionId, but follows enum naming convention.
            // For backwards compatibility, we keep track of the enum-named field.
            KNOWN_REALMS.put(realmId.toUpperCase().replaceAll("-", "_"), this);
        }
    }

    public String getRealmId() { return realmId; }
    public String getSecondLevelDomain() { return secondLevelDomain; }

    public static Realm[] values() {
        synchronized (KNOWN_REALMS) {
            return KNOWN_REALMS.values().toArray(new Realm[0]);
        }
    }

    /**
     * Returns the Realm object matching the specified name. The name must
     * match exactly. (Extraneous whitespace characters are not permitted.)
     */
    public static Realm valueOf(String name) throws IllegalArgumentException {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Unknown realm");
        }
        final Realm realm;
        synchronized (KNOWN_REALMS) {
            realm = KNOWN_REALMS.get(name);
        }
        if (realm == null) {
            throw new IllegalArgumentException("Unknown realm " + name);
        }
        return realm;
    }

    /**
     * Register a new Realm. Used to allow the SDK to be forward compatible with unreleased realms.
     */
    public static Realm register(String realmId, String secondLevelDomain) {
        if (realmId == null || realmId.isBlank()) {
            throw new IllegalArgumentException("Unknown realm");
        }
        if (secondLevelDomain == null || secondLevelDomain.isBlank()) {
            throw new IllegalArgumentException("Unknown 2-level domain for realm");
        }
        realmId = realmId.toLowerCase(Locale.US);
        secondLevelDomain = secondLevelDomain.toLowerCase(Locale.US);
        synchronized (KNOWN_REALMS) {
            for (Realm realm : Realm.values()) {
                if (realm.realmId.equals(realmId)) {
                    if (!realm.secondLevelDomain.equals(secondLevelDomain)) {
                        throw new IllegalArgumentException(
                                "RealmId : "
                                        + realmId
                                        + " is already registered with "
                                        + realm.getSecondLevelDomain()
                                        + ". It cannot be re-registered with a different secondLevelDomain");
                    }
                    return realm;
                }
            }

            return new Realm(realmId, secondLevelDomain);
        }
    }

    @Override
    public int compareTo(Realm o) {
        return realmId.compareTo(o.realmId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Realm)) return false;
        Realm other = (Realm)o;
        return realmId.equalsIgnoreCase(other.getRealmId());
    }

    @Override
    public int hashCode() {
        return realmId.hashCode();
    }

    @Override
    // For backward compatibility maintain the enum toString behavior
    public String toString() {
        return getRealmId().toUpperCase().replaceAll("-", "_");
    }

}
