package api.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * Configuration class for API client settings.
 * Immutable design with builder pattern for thread safety.
 */
public final class Configuration {
    
    private static final Logger LOGGER = LogManager.getLogger(Configuration.class);
    
    private final String servicePath;
    private final String contentType;
    private final Map<String, String> headers;

    private Configuration(final String servicePath, final String contentType, final Map<String, String> headers) {
        this.servicePath = servicePath;
        this.contentType = contentType;
        this.headers = headers != null ? Collections.unmodifiableMap(headers) : Collections.emptyMap();
        LOGGER.debug("Configuration created: servicePath={}, contentType={}, headersCount={}", 
                    servicePath, contentType, this.headers.size());
    }

    public String getServicePath() {
        return servicePath;
    }

    public String getContentType() {
        return contentType;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Configuration)) return false;
        Configuration that = (Configuration) o;
        return Objects.equals(getServicePath(), that.getServicePath()) &&
                Objects.equals(getContentType(), that.getContentType()) &&
                Objects.equals(getHeaders(), that.getHeaders());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServicePath(), getContentType(), getHeaders());
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "servicePath='" + servicePath + '\'' +
                ", contentType='" + contentType + '\'' +
                ", headers=" + headers +
                '}';
    }

    /**
     * Builder class for Configuration.
     */
    public static final class Builder {
        private String servicePath;
        private String contentType;
        private Map<String, String> headers;

        public Builder servicePath(final String servicePath) {
            this.servicePath = servicePath;
            return this;
        }

        public Builder contentType(final String contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder headers(final Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public Configuration build() {
            return new Configuration(servicePath, contentType, headers);
        }
    }

    /**
     * Creates a new builder instance.
     *
     * @return a new Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }
}
