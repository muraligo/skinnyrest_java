package com.m3.skinnyrest;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;

import com.m3.common.core.M3Framework;
import com.m3.common.monitor.prometheus.M3PrometheusFramework;

public class SkinnyFrameworks {
    public static ConcurrentMap<String, M3Framework> _FRAMEWORKS = new ConcurrentHashMap<String, M3Framework>();

    public static M3Framework addFramework(String name, Map<String, Object> props, Logger loggr) {
        if ("MONITOR_PROMETHEUS".equalsIgnoreCase(name)) {
        	if (!_FRAMEWORKS.containsKey("MONITOR_PROMETHEUS")) {
                M3PrometheusFramework fw = M3PrometheusFramework.getInstance(loggr);
                fw.configure(props);
                _FRAMEWORKS.put("MONITOR_PROMETHEUS", fw);
                return fw;
        	}
        }
        return null;
    }
}
