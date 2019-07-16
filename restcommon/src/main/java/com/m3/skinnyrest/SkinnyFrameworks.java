package com.m3.skinnyrest;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;

import com.m3.common.core.M3Framework;
import com.m3.common.monitor.prometheus.M3PrometheusFramework;

public class SkinnyFrameworks {
    public static ConcurrentMap<String, ConcurrentMap<String, M3Framework>> _FRAMEWORKS = new ConcurrentHashMap<String, ConcurrentMap<String, M3Framework>>();

    public static M3Framework addFramework(String name, Map<String, Object> props, Logger loggr) {
        if ("MONITOR.PROMETHEUS".equalsIgnoreCase(name)) {
            ConcurrentMap<String, M3Framework> monfws = _FRAMEWORKS.get("MONITOR");
            if (monfws == null) {
                monfws = new ConcurrentHashMap<String, M3Framework>();
                _FRAMEWORKS.put("MONITOR", monfws);
            }
            M3Framework fw = monfws.get("PROMETHEUS");
        	if (fw == null || !(fw instanceof M3PrometheusFramework)) {
        		if (fw != null) { // different framework, warn and replace
        		    loggr.warn("Adding Monitoring framework of Prometheus but found a different registered framework. Removing old framework and replacing.");
        		    monfws.remove("PROMETHEUS");
        		}
                M3PrometheusFramework fw2 = M3PrometheusFramework.getInstance(loggr);
                fw2.configure(props);
                monfws.put("PROMETHEUS", fw2);
                fw = fw2;
        	}
            return fw;
        }
        return null;
    }

    public static M3Framework addFrameworkFromConfig(String basename, Map<String, Object> config, Logger loggr) {
        if ("MONITOR".equalsIgnoreCase(basename)) {
            String mechstr = (String)config.get("mechanism");
            if (mechstr == null || mechstr.isBlank() || !"PROMETHEUS".equalsIgnoreCase(mechstr.strip())) {
                // TODO ENHANCE raise an error; ignore monitoring? or exit?
            }
            int promport = (Integer)config.get("metricsport");
            if (promport < 0) {
            	promport = 9550;
            }
            // for now assume endpoint is /metrics
            Map<String, Object> props = new HashMap<String, Object>();
            props.put("metricsport", promport);
            return SkinnyFrameworks.addFramework("MONITOR.PROMETHEUS", props, loggr);
        }
        return null;
    }
}
