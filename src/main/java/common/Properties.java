package common;

/**
 * Configuration property constants for the test framework.
 * All constants are public static final for immutable access.
 */
public final class Properties {
    
    // Application URLs
    public static final String BASE_URL = "base.url";
    public static final String SWAGGER_URL = "swagger.url";
    
    // Test Configuration
    public static final String TEST_THREAD_COUNT = "test.thread.count";
    public static final String TEST_TIMEOUT = "test.timeout";
    public static final String TEST_RETRY_COUNT = "test.retry.count";
    
    // Default Users
    public static final String DEFAULT_SUPERVISOR_LOGIN = "default.supervisor.login";
    public static final String DEFAULT_ADMIN_LOGIN = "default.admin.login";
    
    // Test Data Configuration
    public static final String TEST_USER_MIN_AGE = "test.user.min.age";
    public static final String TEST_USER_MAX_AGE = "test.user.max.age";
    public static final String TEST_PASSWORD_MIN_LENGTH = "test.password.min.length";
    public static final String TEST_PASSWORD_MAX_LENGTH = "test.password.max.length";
    
    // Logging Configuration
    public static final String LOGGING_LEVEL = "logging.level";
    public static final String LOGGING_PATTERN = "logging.pattern";
    
    // Allure Configuration
    public static final String ALLURE_RESULTS_DIRECTORY = "allure.results.directory";
    public static final String ALLURE_REPORT_DIRECTORY = "allure.report.directory";
    
    private Properties() {
        // Private constructor to prevent instantiation
    }
}

