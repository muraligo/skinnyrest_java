package com.m3.skinnyrest;

public interface Service {
    /** A unique service name. Must not be null */
    String getServiceName();

    /** The service endpoint prefix that will be used, ex "iaas" for
     *  "https://iaas.us-phoenix-1.oraclecloud.com". */
    String getServiceEndpointPrefix();

    /**
     * The service endpoint template that will be used, ex
     * "{serviceEndpointPrefix}.{region}.service.oci.oraclecloud.com".
     * <p>
     * This overrides the template used in {@link DefaultEndpointConfiguration}, but
     * can still use the same variables.
     */
    String getServiceEndpointTemplate();
}
