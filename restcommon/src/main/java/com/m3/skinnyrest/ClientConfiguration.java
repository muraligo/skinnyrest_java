package com.m3.skinnyrest;

public class ClientConfiguration {
    private static final int CONNECTION_TIMEOUT_MILLIS = 10000;
    private static final int READ_TIMEOUT_MILLIS = 60000;
    private static final int MAX_ASYNC_THREADS = 50;

    /** The max time to wait for a connection, in millis.
     *  Default is CONNECTION_TIMEOUT_MILLIS (10000). */
    private final int connectionTimeoutMillis;
    /** The max time to wait for data, in millis.
     *  Default is READ_TIMEOUT_MILLIS (60000). */
    private final int readTimeoutMillis;
    /** The max number of async threads to use.
     *  Default is MAX_ASYNC_THREADS (50). */
    private final int maxAsyncThreads;

    public int getConnectionTimeoutMillis() { return connectionTimeoutMillis; }
    public int getReadTimeoutMillis() { return readTimeoutMillis; }
    public int getMaxAsyncThreads() { return maxAsyncThreads; }

    @Override
    public String toString() {
        return "ClientConfiguration(connect_timeout=" + getConnectionTimeoutMillis() + 
                "ms, read_timeout=" + getReadTimeoutMillis() + "ms, async_threads=" +
                getMaxAsyncThreads();
    }

    // Explicit @Builder on constructor so we can enforce default values.
    private ClientConfiguration(Integer connectionTimeoutMillis,
            Integer readTimeoutMillis, Integer maxAsyncThreads,
            Boolean disableDataBufferingOnUpload) {
        this.connectionTimeoutMillis =
                getOrDefault(connectionTimeoutMillis, CONNECTION_TIMEOUT_MILLIS);
        this.readTimeoutMillis = getOrDefault(readTimeoutMillis, READ_TIMEOUT_MILLIS);
        this.maxAsyncThreads = getOrDefault(maxAsyncThreads, MAX_ASYNC_THREADS);
    }

    private static <T> T getOrDefault(T value, T defaultValue) {
        return (value == null) ? defaultValue : value;
    }

    public final class Builder {
        private Integer _connectionTimeoutMillis = null;
        private Integer _readTimeoutMillis = null;
        private Integer _maxAsyncThreads = null;

        public void connectionTimeoutMillis(Integer value) {
            _connectionTimeoutMillis = value;
        }

        public void readTimeoutMillis(Integer value) {
            _readTimeoutMillis = value;
        }

        public void maxAsyncThreads(Integer value) {
            _maxAsyncThreads = value;
        }

        public ClientConfiguration build() {
            return new ClientConfiguration(_connectionTimeoutMillis, 
                    _readTimeoutMillis, _maxAsyncThreads, true);
        }
    }
}
