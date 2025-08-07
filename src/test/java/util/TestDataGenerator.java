package util;

import api.model.request.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.UUID;

import static common.PropertiesReader.getProperty;

public class TestDataGenerator {
    private static final Logger logger = LoggerFactory.getLogger(TestDataGenerator.class);
    private static final Random random = new Random();

    public static Player generateValidPlayer() {
        return generateValidPlayer("user");
    }

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

    public static Player generatePlayerWithInvalidAgeYoung() {
        Player player = generateValidPlayer();
        player.setAge(Integer.parseInt(getProperty("test.user.min.age")) - 1);
        return player;
    }

    public static Player generatePlayerWithInvalidAgeOld() {
        Player player = generateValidPlayer();
        player.setAge(Integer.parseInt(getProperty("test.user.max.age")) + 1);
        return player;
    }

    public static Player generatePlayerWithInvalidGender() {
        Player player = generateValidPlayer();
        player.setGender("invalid_gender");
        return player;
    }

    public static Player generatePlayerWithInvalidPasswordShort() {
        Player player = generateValidPlayer();
        player.setPassword("abc123"); // Less than 7 characters
        return player;
    }

    public static Player generatePlayerWithInvalidPasswordLong() {
        Player player = generateValidPlayer();
        player.setPassword("abcdefghijklmnop123456789"); // More than 15 characters
        return player;
    }

    public static Player generatePlayerWithInvalidPasswordNoNumbers() {
        Player player = generateValidPlayer();
        player.setPassword("abcdefg"); // No numbers
        return player;
    }

    public static Player generatePlayerWithInvalidPasswordNoLetters() {
        Player player = generateValidPlayer();
        player.setPassword("1234567"); // No letters
        return player;
    }

    public static Player generatePlayerWithInvalidRole() {
        Player player = generateValidPlayer();
        player.setRole("invalid_role");
        return player;
    }

    public static Player generatePlayerWithDuplicateLogin(String existingLogin) {
        Player player = generateValidPlayer();
        player.setLogin(existingLogin);
        return player;
    }

    public static Player generatePlayerWithNullFields() {
        Player player = new Player();
        player.setAge(25);
        player.setGender("male");
        return player;
    }

    private static int generateRandomAge() {
        var maxAge = Integer.parseInt(getProperty("test.user.max.age"));
        var minAge = Integer.parseInt(getProperty("test.user.min.age"));


        return random.nextInt(maxAge - minAge + 1) + minAge;
    }

    private static String generateRandomGender() {
        return random.nextBoolean() ? "male" : "female";
    }

    private static String generateValidPassword() {
        String letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String numbers = "0123456789";

        int maxPasswordLength = Integer.parseInt(getProperty("test.password.max.length"));
        int minPasswordLength = Integer.parseInt(getProperty("test.password.min.length"));

        int length = random.nextInt(maxPasswordLength - minPasswordLength + 1) + minPasswordLength;

        StringBuilder password = new StringBuilder();

        password.append(letters.charAt(random.nextInt(letters.length())));
        password.append(numbers.charAt(random.nextInt(numbers.length())));

        String allChars = letters + numbers;
        for (int i = 2; i < length; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        char[] passwordArray = password.toString().toCharArray();
        for (int i = passwordArray.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[j];
            passwordArray[j] = temp;
        }
        return new String(passwordArray);
    }

}
