package com.m3.skinnyrest.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.slf4j.Logger;
import org.yaml.snakeyaml.Yaml;

public final class Helper {
    // configPath here is assumed to be relative or absolute to the ROOT
    public static Map<String, Object> parseAndLoadYamlAbs(Logger log, String configPath) {
        Yaml yaml = new Yaml();
        log.debug("Loading config from [" + configPath + "]");
        Map<String, Object> result = null;
        InputStream inputStream = null;
        try (FileInputStream fs = new FileInputStream(configPath)) {
            if (fs != null) {
                inputStream = fs;
                result = yaml.load(inputStream);
            }
        } catch (IOException ioe) {
            log.error( "ERROR Loading config from [" + configPath + "]. Unable to locate or read file.");
        }
        return result;
    }

    /** Attempts to expand paths that may contain unix-style home shorthand. */
    public static String expandUserHome(final String path) {
        // If the home (~) shortcut is used, then attempt to determine correct path.
        // Otherwise, leave as is to allow users to always be able to specify a path
        // without the SDK modifying it.
        if (path.startsWith("~/") || path.startsWith("~\\")) {
            return System.getProperty("user.home")
                    + correctPath(IS_OS_WINDOWS, path.substring(1));
        } else {
            return path;
        }
    }

    // Handle the case where somebody is copying the config file
    // between platforms (or copying examples without changing values)
    static String correctPath(boolean isWindows, String path) {
        if (isWindows) {
            // https://msdn.microsoft.com/en-us/library/aa365247
            // forward slash is reserved, assume its not supposed to
            // be there and replace with back slash
            path = path.replace('/', '\\');
        }
        // back slash is not a reserved character on other platforms,
        // so do not attempt to modify it
        return path;
    }

    // Operating system checks
    // -----------------------------------------------------------------------
    /**
     * Defaults to {@code null} if the runtime does not have security access 
     * to read this property or the property does not exist.
     */
    public static final String OS_ARCH = getSystemProperty("os.arch");
    public static final String OS_NAME = getSystemProperty("os.name");
    public static final String OS_VERSION = getSystemProperty("os.version");
    private static final String OS_NAME_WINDOWS_PREFIX = "Windows";


    // These MUST be declared after those above as they depend on the
    // values being set up
    // OS names from http://www.vamphq.com/os.html
    // Selected ones included - please advise dev@commons.apache.org
    // if you want another added or a mistake corrected

    public static final boolean IS_OS_LINUX = getOsMatchesName("Linux") || getOsMatchesName("LINUX");
    public static final boolean IS_OS_FREE_BSD = getOsMatchesName("FreeBSD");
    public static final boolean IS_OS_OPEN_BSD = getOsMatchesName("OpenBSD");
    public static final boolean IS_OS_NET_BSD = getOsMatchesName("NetBSD");
    public static final boolean IS_OS_SOLARIS = getOsMatchesName("Solaris");

    public static final boolean IS_OS_MAC = getOsMatchesName("Mac");
    public static final boolean IS_OS_MAC_OSX = getOsMatchesName("Mac OS X");
    public static final boolean IS_OS_MAC_OSX_MAVERICKS = getOsMatches("Mac OS X", "10.9");
    public static final boolean IS_OS_MAC_OSX_YOSEMITE = getOsMatches("Mac OS X", "10.10");
    public static final boolean IS_OS_MAC_OSX_EL_CAPITAN = getOsMatches("Mac OS X", "10.11");
    // Probably irrelevant
//    public static final boolean IS_OS_MAC_OSX_CHEETAH = getOsMatches("Mac OS X", "10.0");
//    public static final boolean IS_OS_MAC_OSX_PUMA = getOsMatches("Mac OS X", "10.1");
//    public static final boolean IS_OS_MAC_OSX_JAGUAR = getOsMatches("Mac OS X", "10.2");
//    public static final boolean IS_OS_MAC_OSX_PANTHER = getOsMatches("Mac OS X", "10.3");
//    public static final boolean IS_OS_MAC_OSX_TIGER = getOsMatches("Mac OS X", "10.4");
//    public static final boolean IS_OS_MAC_OSX_LEOPARD = getOsMatches("Mac OS X", "10.5");
//    public static final boolean IS_OS_MAC_OSX_SNOW_LEOPARD = getOsMatches("Mac OS X", "10.6");
//    public static final boolean IS_OS_MAC_OSX_LION = getOsMatches("Mac OS X", "10.7");
//    public static final boolean IS_OS_MAC_OSX_MOUNTAIN_LION = getOsMatches("Mac OS X", "10.8");

    public static final boolean IS_OS_UNIX = IS_OS_LINUX || IS_OS_MAC_OSX
            || IS_OS_SOLARIS || IS_OS_FREE_BSD || IS_OS_OPEN_BSD || IS_OS_NET_BSD
//            || IS_OS_SUN_OS || IS_OS_AIX || IS_OS_HP_UX || IS_OS_IRIX
            ;

    // Probably irrelevant
//    public static final boolean IS_OS_OS2 = getOsMatchesName("OS/2");
//    public static final boolean IS_OS_AIX = getOsMatchesName("AIX");
//    public static final boolean IS_OS_HP_UX = getOsMatchesName("HP-UX");
//    public static final boolean IS_OS_400 = getOsMatchesName("OS/400");
//    public static final boolean IS_OS_IRIX = getOsMatchesName("Irix");
//    public static final boolean IS_OS_SUN_OS = getOsMatchesName("SunOS");

    public static final boolean IS_OS_WINDOWS = getOsMatchesName(OS_NAME_WINDOWS_PREFIX);
    public static final boolean IS_OS_WINDOWS_8 = getOsMatchesName(OS_NAME_WINDOWS_PREFIX + " 8");
    public static final boolean IS_OS_WINDOWS_10 = getOsMatchesName(OS_NAME_WINDOWS_PREFIX + " 10");
    public static final boolean IS_OS_WINDOWS_XP = getOsMatchesName(OS_NAME_WINDOWS_PREFIX + " XP");

    // -----------------------------------------------------------------------
    // Probably irrelevant
//    public static final boolean IS_OS_WINDOWS_95 = getOsMatchesName(OS_NAME_WINDOWS_PREFIX + " 95");
//    public static final boolean IS_OS_WINDOWS_98 = getOsMatchesName(OS_NAME_WINDOWS_PREFIX + " 98");
//    public static final boolean IS_OS_WINDOWS_ME = getOsMatchesName(OS_NAME_WINDOWS_PREFIX + " Me");
//    public static final boolean IS_OS_WINDOWS_VISTA = getOsMatchesName(OS_NAME_WINDOWS_PREFIX + " Vista");
//    public static final boolean IS_OS_WINDOWS_2000 = getOsMatchesName(OS_NAME_WINDOWS_PREFIX + " 2000");
//    public static final boolean IS_OS_WINDOWS_2003 = getOsMatchesName(OS_NAME_WINDOWS_PREFIX + " 2003");
//    public static final boolean IS_OS_WINDOWS_2008 = getOsMatchesName(OS_NAME_WINDOWS_PREFIX + " Server 2008");
//    public static final boolean IS_OS_WINDOWS_2012 = getOsMatchesName(OS_NAME_WINDOWS_PREFIX + " Server 2012");
//    public static final boolean IS_OS_WINDOWS_NT = getOsMatchesName(OS_NAME_WINDOWS_PREFIX + " NT");
//    public static final boolean IS_OS_WINDOWS_7 = getOsMatchesName(OS_NAME_WINDOWS_PREFIX + " 7");

    // Values on a z/OS system I tested (Gary Gregory - 2016-03-12)
    // os.arch = s390x
    // os.encoding = ISO8859_1
    // os.name = z/OS
    // os.version = 02.02.00
    public static final boolean IS_OS_ZOS = getOsMatchesName("z/OS");

    // -----------------------------------------------------------------------
    private static boolean getOsMatches(final String osNamePrefix, final String osVersionPrefix) {
        return isOSMatch(OS_NAME, OS_VERSION, osNamePrefix, osVersionPrefix);
    }

    private static boolean getOsMatchesName(final String osNamePrefix) {
        return isOSNameMatch(OS_NAME, osNamePrefix);
    }

    private static boolean isOSMatch(final String osName, final String osVersion, final String osNamePrefix, final String osVersionPrefix) {
        if (osName == null || osVersion == null) return false;
        return isOSNameMatch(osName, osNamePrefix) && isOSVersionMatch(osVersion, osVersionPrefix);
    }

    private static boolean isOSNameMatch(final String osName, final String osNamePrefix) {
        if (osName == null) return false;
        return osName.startsWith(osNamePrefix);
    }

    private static boolean isOSVersionMatch(final String osVersion, final String osVersionPrefix) {
        if (osVersion == null || osVersion.isEmpty()) return false;
        // Compare parts of the version string instead of using String.startsWith(String) because otherwise
        // osVersionPrefix 10.1 would also match osVersion 10.10
        final String[] versionPrefixParts = osVersionPrefix.split("\\.");
        final String[] versionParts = osVersion.split("\\.");
        for (int i = 0; i < Math.min(versionPrefixParts.length, versionParts.length); i++) {
            if (!versionPrefixParts[i].equals(versionParts[i])) {
                return false;
            }
        }
        return true;
    }

    private static String getSystemProperty(final String property) {
        try {
            return System.getProperty(property);
        } catch (final SecurityException ex) {
            // we are not allowed to look at this property
            // System.err.println("Caught a SecurityException reading the system property '" + property
            // + "'; the SystemUtils property value will default to null.");
            return null;
        }
    }

    public static String getEnvironmentVariable(final String name, final String defaultValue) {
        try {
            final String value = System.getenv(name);
            return value == null ? defaultValue : value;
        } catch (final SecurityException ex) {
            // we are not allowed to look at this property
            // System.err.println("Caught a SecurityException reading the environment variable '" + name + "'.");
            return defaultValue;
        }
    }

    private Helper() { }
}
