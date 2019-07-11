package com.m3.skinnyrest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m3.skinnyrest.util.Helper;

public class CloudConfigFileReader {
    private static Logger _LOG = LoggerFactory.getLogger(CloudConfigFileReader.class);

    public static final String DEFAULT_CONFIG_PATH = "~/.oci/config";
    private static final String DEFAULT_PROFILE_NAME = "DEFAULT";

    public static CloudConfigFile parseDefault() throws IOException {
        return parseDefault(null);
    }

    public static CloudConfigFile parseDefault(String profile) throws IOException {
        File defaultFile = new File(Helper.expandUserHome(DEFAULT_CONFIG_PATH));

        if (defaultFile.exists() && defaultFile.isFile()) {
            _LOG.debug("Loading config file from: {}", defaultFile);
            return parse(defaultFile.getAbsolutePath(), profile);
        } else {
            throw new IOException(
                    String.format("Can't load the default config from '%s' because it does not exist or it is not a file.",
                            defaultFile.getAbsolutePath()));
        }
    }

    public static CloudConfigFile parse(String configurationFilePath) throws IOException {
        return parse(configurationFilePath, null);
    }

    public static CloudConfigFile parse(String configurationFilePath, String profile)
            throws IOException {
        return parse(new FileInputStream(new File(Helper.expandUserHome(configurationFilePath))), profile);
    }

    public static CloudConfigFile parse(InputStream configurationStream, String profile)
            throws IOException {
        return parse(configurationStream, profile, StandardCharsets.UTF_8);
    }

    public static CloudConfigFile parse(InputStream configurationStream, String profile, Charset charset)
            throws IOException {
        if (charset == null) {
            throw new IllegalArgumentException("Charset for parsing file is required");
        }
        final CloudConfigAccumulator accumulator = new CloudConfigAccumulator();
        try (final BufferedReader reader =
                new BufferedReader(new InputStreamReader(configurationStream, charset))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                accumulator.accept(line);
            }
        }
        if (!accumulator.foundDefaultProfile) {
            _LOG.info("No DEFAULT profile was specified in the configuration");
        }
        if (profile != null && !accumulator.configurationsByProfile.containsKey(profile)) {
            throw new IllegalArgumentException(
                    "No profile named " + profile + " exists in the configuration file");
        }

        return new CloudConfigFile(accumulator, profile);
    }

    private CloudConfigFileReader() {}


    /** ConfigFile represents a simple lookup mechanism for a OCI config file. */
    public static final class CloudConfigFile {
        private final CloudConfigAccumulator accumulator;
        private final String profile;

        /**
         * Gets the value associated with a given key. The value returned will
         * be the one for the selected profile (if available), else the value in
         * the DEFAULT profile (if specified), else null.
         *
         * @param key
         *            The key to look up.
         * @return The value, or null if it didn't exist.
         */
        public String get(String key) {
            if (profile != null
                    && (accumulator.configurationsByProfile.get(profile).containsKey(key))) {
                return accumulator.configurationsByProfile.get(profile).get(key);
            }
            return accumulator.foundDefaultProfile
                    ? accumulator.configurationsByProfile.get(DEFAULT_PROFILE_NAME).get(key)
                    : null;
        }

        private CloudConfigFile(CloudConfigAccumulator accr, String profile) {
            if (accr == null) {
                throw new IllegalArgumentException("Must specify required arguments");
            }
            accumulator = accr;
            this.profile = profile;
        }
    }

    private static final class CloudConfigAccumulator {
        final Map<String, Map<String, String>> configurationsByProfile = new HashMap<>();
        private String currentProfile = null;
        private boolean foundDefaultProfile = false;

        private void accept(String line) {
            final String trimmedLine = line.trim();
            // no blank lines
            if (trimmedLine.isEmpty()) return;
            // skip comments
            if (trimmedLine.charAt(0) == '#') return;

            if (trimmedLine.charAt(0) == '['
                    && trimmedLine.charAt(trimmedLine.length() - 1) == ']') {
                currentProfile = trimmedLine.substring(1, trimmedLine.length() - 1).trim();
                if (currentProfile.isEmpty())
                    throw new IllegalStateException("Cannot have empty profile name: " + line);
                if (currentProfile.equals(DEFAULT_PROFILE_NAME))
                    foundDefaultProfile = true;
                if (!configurationsByProfile.containsKey(currentProfile)) {
                    configurationsByProfile.put(currentProfile, new HashMap<String, String>());
                }
                return;
            }

            final int splitIndex = trimmedLine.indexOf('=');
            if (splitIndex == -1)
                throw new IllegalStateException("Found line within profile with no key-value pair: " + line);
            final String key = trimmedLine.substring(0, splitIndex).trim();
            final String value = trimmedLine.substring(splitIndex + 1).trim();
            if (key.isEmpty())
                throw new IllegalStateException("Found line with no key: " + line);

            if (currentProfile == null) {
                throw new IllegalStateException(
                        "Config parse error, attempted to read configuration without specifying a profile: "
                                + line);
            }
            configurationsByProfile.get(currentProfile).put(key, value);
        }
    }
}
