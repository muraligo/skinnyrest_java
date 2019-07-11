package com.m3.skinnyrest.sample;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m3.skinnyrest.rest.RestResourceDetail;
import com.m3.skinnyrest.rest.RestUtil;
import com.m3.skinnyrest.util.Helper;
import com.sun.net.httpserver.HttpServer;

public class SkinnyRestSampleService {
    private static final Logger _LOG = LoggerFactory.getLogger(SkinnyRestSampleService.class);

    private static final String DEFAULTENV = "dev";

    private static String _envname = null;

    private int _minThreads;
    private int _maxThreads;
    private int _adminMinThreads;
    private int _adminMaxThreads; // Should be > 1.5 times the number of cores
    private int _shutdownGracePeriod;
    private String _rootPath;
//    private List<ConnectorConfig> _applicationConnectors = new ArrayList<ConnectorConfig>();
//    private List<ConnectorConfig> _adminConnectors = new ArrayList<ConnectorConfig>();
//    private LogConfig _requestLog;

    private ConcurrentMap<String, RestResourceDetail> _RESOURCES = null;

    public static void main(String[] args) {
        String argenvvalue = null;
        String argconfigpthstr = null;
        for (int ix = 0; ix < args.length; ix++) {
            if (args[ix].startsWith("--environment")) {
                argenvvalue = fetchArgumentValue("--environment", args[ix]);
            } else if (args[ix].startsWith("--config")) {
                argconfigpthstr = fetchArgumentValue("--config", args[ix]);
            } else {
                _LOG.error("RBWS: ERROR invalid argument [" + args[ix] + " at " + ix + "].");
            }
        }
        // TODO Ensure arguments exist and are valid paths
        if (argconfigpthstr == null || argconfigpthstr.isEmpty()) {
            _LOG.error("RBWS: Empty or invalid configuration path parameter");
            System.exit(-1);
        }
        _envname = (argenvvalue != null) ? argenvvalue.trim().toLowerCase() : DEFAULTENV;

        _LOG.info("Configuring SkinnyRestSampleService for environment [" + _envname + "] from source [" + argconfigpthstr + "]");

        SkinnyRestSampleService svc = new SkinnyRestSampleService();
        svc.readConfigs(argconfigpthstr);

        _LOG.info("Starting RBWS ...");

        HttpServer server = null;
        try {
            InetSocketAddress isa = new InetSocketAddress("localhost", 8085); // TODO later svc._applicationConnectors.get(0).getPort());
//            InetSocketAddress isa = new InetSocketAddress(rbws._applicationConnectors.get(0).getPort());
            _LOG.debug("Address is [" + isa.getAddress().getHostAddress() + "]/[" + isa.getPort() + "]");
            server = HttpServer.create(isa, 0); // 2nd arg is backlog
        } catch (IOException ioe1) {
            _LOG.error("Error creating HTTP server listening on port [" + 
//                            svc._applicationConnectors.get(0).getPort() // TODO later
                            "8085"
                            + "]. Exiting", ioe1);
            System.exit(1);
        }
        final List<HttpServer> srvlst = Collections.singletonList(server); // for shutdown hook

//        HttpContext doesitworkContext = server.createContext("/check");
//        doesitworkContext.setHandler(SkinnyRestSampleService::handleDoesItWork);
        svc.registerResources(server);

        Runtime.getRuntime().addShutdownHook(new Thread() { 
            public void run() { 
                _LOG.info("RBWS: Shutdown Hook is running !");
                // TODO Handle grace period
                srvlst.get(0).stop(0);
            }
        });

        _LOG.info("... RBWS started.");
        server.start();
    }

    private void registerResources(HttpServer server) {
        Class<?>[] resourceClasses = { StoreResource.class };
        _RESOURCES = RestUtil.registerRestResources(server, resourceClasses);
    }

	private void readConfigs(String configPath) {
        // no separate env specific and app specific config files; use configPath as full path
        Map<String, Object> configraw = Helper.parseAndLoadYamlAbs(_LOG, configPath);
        if (configraw == null || configraw.isEmpty()) {
            System.err.println("Rbws: ERROR in parsing or EMPTY configuration");
            System.exit(1);
        }
        readEnvironmentSpecificConfigs(configraw);
    }

    @SuppressWarnings("unchecked")
    private void readEnvironmentSpecificConfigs(Map<String, Object> configraw) {
        if (configraw.containsKey("server")) {
            Map<String, Object> serverdetails = (Map<String, Object>)configraw.get("server");
//            _promport = (Integer)serverdetails.get("metricsport");
//            if (_promport < 0) {
//                _promport = 9550;
//            }
//            _initialdelay = (Integer)serverdetails.get("initialdelay");
//            if (_initialdelay < 0) {
//                _initialdelay = 10;
//            }
//            _pollinterval = (Integer)serverdetails.get("pollinterval");
//            if (_pollinterval < 0) {
//                _pollinterval = 120;
//            }
//            _oauth2url = (String)serverdetails.get("oauth2url");
            _minThreads = (Integer)serverdetails.get("minThreads");
            if (_minThreads < 0) {
                _minThreads = 1;
            }
            _maxThreads = (Integer)serverdetails.get("maxThreads");
            if (_maxThreads < 0) {
                _maxThreads = 10;
            }
            _adminMinThreads = (Integer)serverdetails.get("adminMinThreads");
            if (_adminMinThreads < 0) {
                _adminMinThreads = 10;
            }
            _adminMaxThreads = (Integer)serverdetails.get("adminMaxThreads");
            if (_adminMaxThreads < 0) {
                _adminMaxThreads = 12;
            }
            _shutdownGracePeriod = (Integer)serverdetails.get("shutdownGracePeriod");
            if (_shutdownGracePeriod < 0) {
                _shutdownGracePeriod = 30;
            }
            // TODO Hardcode ports for now
//            List<Map<String, Object>> appconobj = (List<Map<String, Object>>)configraw.get("applicationConnectors");
//            for (Map<String, Object> conobj : appconobj) {
//                ConnectorConfig cc = new ConnectorConfig();
//                cc.setType((String)conobj.get("type"));
//                cc.setPort((Integer)conobj.get("port"), 19000);
//                _applicationConnectors.add(cc);
//            }
//            List<Map<String, Object>> admconobj = (List<Map<String, Object>>)configraw.get("adminConnectors");
//            for (Map<String, Object> conobj : admconobj) {
//                ConnectorConfig cc = new ConnectorConfig();
//                cc.setType((String)conobj.get("type"));
//                cc.setPort((Integer)conobj.get("port"), 19001);
//                _adminConnectors.add(cc);
//            }
            _rootPath = (String)serverdetails.get("rootPath");
            // TODO Handle flexible logging later
//            if (serverdetails.containsKey("requestLog")) {
//                _requestLog = readLogConfig((Map<String, Object>)configraw.get("requestLog"));
//            }
        }
    }

    private static String fetchArgumentValue(String argname, String argraw) {
        String argvalue = null;
        int valix = argraw.indexOf("=");
        if (valix > 0) {
            valix++;
            argvalue = argraw.substring(valix);
        }
        return argvalue;
    }

}
