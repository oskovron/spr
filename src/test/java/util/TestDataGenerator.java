package util;

import api.model.Player;
import common.PropertiesReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.UUID;

import static common.PropertiesReader.getProperty;

public class TestDataGenerator {
    private static final Logger logger = LoggerFactory.getLogger(TestDataGenerator.class);
    private static final Random random = new Random();

    /**
     * Generate a valid player with random data
     */
    public static Player generateValidPlayer() {
        return generateValidPlayer("user");
    }

    /**
     * Generate a valid player with specified role
     */
    public static Player generateValidPlayer(String role) {
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);

        Player player = new Player();
        player.setAge(generateRandomAge());
        player.setGender(generateRandomGender());
        player.setLogin("testuser_" + uniqueId);
        player.setPassword(generateValidPassword());
        player.setRole(role);
        player.setScreenName("screen_" + uniqueId);

        logger.debug("Generated valid player: {}", player);
        return player;
    }

    /**
     * Generate a player with invalid age (too young)
     */
    public static Player generatePlayerWithInvalidAgeYoung() {
        Player player = generateValidPlayer();
        player.setAge(Integer.parseInt(getProperty("test.user.min.age")) - 1);
        return player;
    }

    /**
     * Generate a player with invalid age (too old)
     */
    public static Player generatePlayerWithInvalidAgeOld() {
        Player player = generateValidPlayer();
        player.setAge(Integer.parseInt(getProperty("test.user.max.age")) + 1);
        return player;
    }

    /**
     * Generate a player with invalid gender
     */
    public static Player generatePlayerWithInvalidGender() {
        Player player = generateValidPlayer();
        player.setGender("invalid_gender");
        return player;
    }

    /**
     * Generate a player with invalid password (too short)
     */
    public static Player generatePlayerWithInvalidPasswordShort() {
        Player player = generateValidPlayer();
        player.setPassword("abc123"); // Less than 7 characters
        return player;
    }

    /**
     * Generate a player with invalid password (too long)
     */
    public static Player generatePlayerWithInvalidPasswordLong() {
        Player player = generateValidPlayer();
        player.setPassword("abcdefghijklmnop123456789"); // More than 15 characters
        return player;
    }

    /**
     * Generate a player with invalid password (no numbers)
     */
    public static Player generatePlayerWithInvalidPasswordNoNumbers() {
        Player player = generateValidPlayer();
        player.setPassword("abcdefg"); // No numbers
        return player;
    }

    /**
     * Generate a player with invalid password (no letters)
     */
    public static Player generatePlayerWithInvalidPasswordNoLetters() {
        Player player = generateValidPlayer();
        player.setPassword("1234567"); // No letters
        return player;
    }

    /**
     * Generate a player with invalid role
     */
    public static Player generatePlayerWithInvalidRole() {
        Player player = generateValidPlayer();
        player.setRole("invalid_role");
        return player;
    }

    /**
     * Generate a player with duplicate login
     */
    public static Player generatePlayerWithDuplicateLogin(String existingLogin) {
        Player player = generateValidPlayer();
        player.setLogin(existingLogin);
        return player;
    }

    /**
     * Generate a player with duplicate screen name
     */
    public static Player generatePlayerWithDuplicateScreenName(String existingScreenName) {
        Player player = generateValidPlayer();
        player.setScreenName(existingScreenName);
        return player;
    }

    /**
     * Generate a player with null required fields
     */
    public static Player generatePlayerWithNullFields() {
        Player player = new Player();
        // Only set some fields, leave others null
        player.setAge(25);
        player.setGender("male");
        // login, password, role, screenName are null
        return player;
    }

    /**
     * Generate a random valid age
     */
    private static int generateRandomAge() {
        var maxAge = Integer.parseInt(getProperty("test.user.max.age"));
        var minAge = Integer.parseInt(getProperty("test.user.min.age"));


        return random.nextInt(maxAge - minAge + 1) + minAge;
    }

    /**
     * Generate a random valid gender
     */
    private static String generateRandomGender() {
        return random.nextBoolean() ? "male" : "female";
    }

    /**
     * Generate a valid password
     */
    private static String generateValidPassword() {
        String letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String numbers = "0123456789";

        int maxPasswordLength = Integer.parseInt(getProperty("test.password.max.length"));
        int minPasswordLength = Integer.parseInt(getProperty("test.password.min.length"));

        int length = random.nextInt(maxPasswordLength - minPasswordLength + 1) + minPasswordLength;

        StringBuilder password = new StringBuilder();

        // Ensure at least one letter and one number
        password.append(letters.charAt(random.nextInt(letters.length())));
        password.append(numbers.charAt(random.nextInt(numbers.length())));

        // Fill the rest randomly
        String allChars = letters + numbers;
        for (int i = 2; i < length; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        // Shuffle the password
        char[] passwordArray = password.toString().toCharArray();
        for (int i = passwordArray.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[j];
            passwordArray[j] = temp;
        }

        return new String(passwordArray);
    }

    /**
     * Generate a unique identifier
     */
    public static String generateUniqueId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
