package util;

import api.model.request.Player;
import net.datafaker.Faker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Locale;

import static common.PropertiesReader.getIntProperty;
import static common.Properties.TEST_USER_MIN_AGE;
import static common.Properties.TEST_USER_MAX_AGE;
import static common.Properties.TEST_PASSWORD_MIN_LENGTH;
import static common.Properties.TEST_PASSWORD_MAX_LENGTH;

/**
 * Test data generator using Faker library for realistic test data.
 * Thread-safe implementation with proper logging.
 */
public final class TestDataGenerator {
    
    private static final Logger LOGGER = LogManager.getLogger(TestDataGenerator.class);
    private static final ThreadLocal<Faker> fakerThreadLocal = ThreadLocal.withInitial(() -> new Faker(Locale.ENGLISH));
    
    private TestDataGenerator() {
        // Private constructor to prevent instantiation
    }

    /**
     * Generates a valid player with default "user" role.
     *
     * @return a valid Player object
     */
    public static Player generateValidPlayer() {
        return generateValidPlayer("user");
    }

    /**
     * Generates a valid player with specified role.
     *
     * @param role the role for the player
     * @return a valid Player object
     */
    public static Player generateValidPlayer(final String role) {
        Faker faker = fakerThreadLocal.get();
        
        Player player = new Player();
        player.setAge(generateRandomAge());
        player.setGender(generateRandomGender());
        player.setLogin("testuser_" + faker.internet().userAgent());
        player.setPassword(generateValidPassword());
        player.setRole(role);
        player.setScreenName("screen_" + faker.name().fullName());

        LOGGER.debug("Generated valid player with role {}: {}", role, player);
        return player;
    }

    /**
     * Generates a player with invalid age (too young).
     *
     * @return a Player object with invalid age
     */
    public static Player generatePlayerWithInvalidAgeYoung() {
        Player player = generateValidPlayer();
        int minAge = getIntProperty(TEST_USER_MIN_AGE, 16);
        player.setAge(minAge - 1);
        LOGGER.debug("Generated player with invalid young age: {}", player.getAge());
        return player;
    }

    /**
     * Generates a player with invalid age (too old).
     *
     * @return a Player object with invalid age
     */
    public static Player generatePlayerWithInvalidAgeOld() {
        Player player = generateValidPlayer();
        int maxAge = getIntProperty(TEST_USER_MAX_AGE, 60);
        player.setAge(maxAge + 1);
        LOGGER.debug("Generated player with invalid old age: {}", player.getAge());
        return player;
    }

    /**
     * Generates a player with invalid gender.
     *
     * @return a Player object with invalid gender
     */
    public static Player generatePlayerWithInvalidGender() {
        Player player = generateValidPlayer();
        player.setGender("invalid_gender");
        LOGGER.debug("Generated player with invalid gender: {}", player.getGender());
        return player;
    }

    /**
     * Generates a player with invalid password (too short).
     *
     * @return a Player object with invalid password
     */
    public static Player generatePlayerWithInvalidPasswordShort() {
        Player player = generateValidPlayer();
        int minLength = getIntProperty(TEST_PASSWORD_MIN_LENGTH, 7);
        player.setPassword("abc" + (minLength - 4)); // Less than minimum length
        LOGGER.debug("Generated player with invalid short password: {}", player.getPassword());
        return player;
    }

    /**
     * Generates a player with invalid password (too long).
     *
     * @return a Player object with invalid password
     */
    public static Player generatePlayerWithInvalidPasswordLong() {
        Player player = generateValidPlayer();
        int maxLength = getIntProperty(TEST_PASSWORD_MAX_LENGTH, 15);
        player.setPassword("abcdefghijklmnop" + (maxLength + 5)); // More than maximum length
        LOGGER.debug("Generated player with invalid long password: {}", player.getPassword());
        return player;
    }

    /**
     * Generates a player with invalid password (no numbers).
     *
     * @return a Player object with invalid password
     */
    public static Player generatePlayerWithInvalidPasswordNoNumbers() {
        Player player = generateValidPlayer();
        player.setPassword("abcdefg"); // No numbers
        LOGGER.debug("Generated player with invalid password (no numbers): {}", player.getPassword());
        return player;
    }

    /**
     * Generates a player with invalid password (no letters).
     *
     * @return a Player object with invalid password
     */
    public static Player generatePlayerWithInvalidPasswordNoLetters() {
        Player player = generateValidPlayer();
        player.setPassword("1234567"); // No letters
        LOGGER.debug("Generated player with invalid password (no letters): {}", player.getPassword());
        return player;
    }

    /**
     * Generates a player with invalid role.
     *
     * @return a Player object with invalid role
     */
    public static Player generatePlayerWithInvalidRole() {
        Player player = generateValidPlayer();
        player.setRole("invalid_role");
        LOGGER.debug("Generated player with invalid role: {}", player.getRole());
        return player;
    }

    /**
     * Generates a player with duplicate login.
     *
     * @param existingLogin the existing login to duplicate
     * @return a Player object with duplicate login
     */
    public static Player generatePlayerWithDuplicateLogin(final String existingLogin) {
        Player player = generateValidPlayer();
        player.setLogin(existingLogin);
        LOGGER.debug("Generated player with duplicate login: {}", player.getLogin());
        return player;
    }

    /**
     * Generates a player with null required fields.
     *
     * @return a Player object with null fields
     */
    public static Player generatePlayerWithNullFields() {
        Player player = new Player();
        player.setAge(25);
        player.setGender("male");
        // login, password, role, screenName are null
        LOGGER.debug("Generated player with null fields: {}", player);
        return player;
    }

    /**
     * Generates a random age within the configured range.
     *
     * @return a random age
     */
    private static int generateRandomAge() {
        int maxAge = getIntProperty(TEST_USER_MAX_AGE, 60);
        int minAge = getIntProperty(TEST_USER_MIN_AGE, 16);
        
        Faker faker = fakerThreadLocal.get();
        return faker.number().numberBetween(minAge, maxAge + 1);
    }

    /**
     * Generates a random gender.
     *
     * @return a random gender ("male" or "female")
     */
    private static String generateRandomGender() {
        Faker faker = fakerThreadLocal.get();
        return faker.options().option("male", "female");
    }

    /**
     * Generates a valid password meeting the requirements.
     *
     * @return a valid password
     */
    private static String generateValidPassword() {
        Faker faker = fakerThreadLocal.get();
        
        int maxPasswordLength = getIntProperty(TEST_PASSWORD_MAX_LENGTH, 15);
        int minPasswordLength = getIntProperty(TEST_PASSWORD_MIN_LENGTH, 7);
        
        int length = faker.number().numberBetween(minPasswordLength, maxPasswordLength + 1);
        
        // Ensure password contains both letters and numbers
        String letters = faker.regexify("[a-zA-Z]{" + (length - 2) + "}");
        String numbers = faker.regexify("[0-9]{2}");
        
        // Shuffle the password
        String password = letters + numbers;
        return faker.regexify(password);
    }
}
