package api;

import api.client.ResponseWrapper;
import api.error.ErrorBody;
import api.model.request.Player;
import api.model.response.PlayerResponse;
import api.requests.PlayerApiClient;
import common.PropertiesReader;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;
import util.TestDataGenerator;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;

public class PlayerControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(PlayerControllerTest.class);
    private PlayerApiClient apiClient;
    private List<Integer> createdPlayerIds;

    @BeforeMethod
    public void setUp() {
        logger.info("Setting up PlayerControllerTest");
        apiClient = new PlayerApiClient();
        createdPlayerIds = new CopyOnWriteArrayList<>();
    }

    @AfterMethod
    public void tearDown() {
        logger.info("Cleaning up created players");
        // Clean up created players to avoid test data pollution
        for (Integer playerId : createdPlayerIds) {
            try {
//                apiClient.deletePlayer(config.getSupervisorLogin(), playerId); //todo delete!
                logger.info("Cleaned up player with ID: {}", playerId);
            } catch (Exception e) {
                logger.warn("Failed to clean up player with ID: {}", playerId, e);
            }
        }
    }

    @Test(description = "Create player with valid data using supervisor and verify Response body")
    public void testCreatePlayerWithValidDataUsingSupervisorVerifyResponseBody() {
        SoftAssert softAssert = new SoftAssert();
        Player expectedPlayer = TestDataGenerator.generateValidPlayer();

        ResponseWrapper<PlayerResponse> actualPlayer = apiClient
                .createPlayer(PropertiesReader.getProperty("default.supervisor.login"), expectedPlayer);
        PlayerResponse actualPlayerResponseBody = actualPlayer.readEntity();

        actualPlayer.expectingStatusCode(200);
        //todo assert not list in rsponse , but object ?

        assertNotNull(actualPlayerResponseBody.toString(), "Response body should not be null");

        softAssert.assertEquals(actualPlayerResponseBody.getAge(), expectedPlayer.getAge(),
                "Age should be equal, but is not.");
        softAssert.assertEquals(actualPlayerResponseBody.getGender(), expectedPlayer.getGender(),
                "Gender should be equal, but is not.");
        softAssert.assertEquals(actualPlayerResponseBody.getLogin(), expectedPlayer.getLogin(),
                "Login should be equal, but is not.");
        softAssert.assertEquals(actualPlayerResponseBody.getPassword(), expectedPlayer.getPassword(),
                "Password should be equal, but is not.");
        softAssert.assertEquals(actualPlayerResponseBody.getRole(), expectedPlayer.getRole(),
                "Role should be equal, but is not.");
        softAssert.assertEquals(actualPlayerResponseBody.getScreenName(), expectedPlayer.getScreenName(),
                "ScreenName should be equal, but is not.");

        Integer playerId = actualPlayerResponseBody.getPlayerId();
        assertNotNull(playerId, "PlayerId should be not null");
        createdPlayerIds.add(playerId);

        softAssert.assertAll();
    }

    @Test(description = "Create player with valid data using supervisor")
    public void testCreatePlayerWithValidDataUsingSupervisor() {
        SoftAssert softAssert = new SoftAssert();
        Player expectedPlayer = TestDataGenerator.generateValidPlayer();

        ResponseWrapper<PlayerResponse> actualPlayer = apiClient
                .createPlayer(PropertiesReader.getProperty("default.supervisor.login"), expectedPlayer);
        PlayerResponse actualPlayerResponseBody = actualPlayer.readEntity();

        actualPlayer.expectingStatusCode(200);
        assertNotNull(actualPlayerResponseBody.toString(), "Response body should not be null");

        ResponseWrapper<PlayerResponse> getPlayerResponse = apiClient.getPlayer(actualPlayerResponseBody.getPlayerId());
        getPlayerResponse.expectingStatusCode(200);
        PlayerResponse getPlayerResponseBody = getPlayerResponse.readEntity();

        softAssert.assertEquals(getPlayerResponseBody.getAge(), expectedPlayer.getAge(),
                "Age should be equal, but is not.");
        softAssert.assertEquals(getPlayerResponseBody.getGender(), expectedPlayer.getGender(),
                "Gender should be equal, but is not.");
        softAssert.assertEquals(getPlayerResponseBody.getLogin(), expectedPlayer.getLogin(),
                "Login should be equal, but is not.");
        softAssert.assertEquals(getPlayerResponseBody.getPassword(), expectedPlayer.getPassword(),
                "Password should be equal, but is not.");
        softAssert.assertEquals(getPlayerResponseBody.getRole(), expectedPlayer.getRole(),
                "Role should be equal, but is not.");
        softAssert.assertEquals(getPlayerResponseBody.getScreenName(), expectedPlayer.getScreenName(),
                "ScreenName should be equal, but is not.");

        Integer playerId = getPlayerResponseBody.getPlayerId();
        assertNotNull(playerId, "PlayerId should be not null");
        createdPlayerIds.add(playerId);

        softAssert.assertAll();
    }

    @Test(description = "Create player with valid data using admin")
    public void testCreatePlayerWithValidDataUsingAdmin() {
        SoftAssert softAssert = new SoftAssert();
        Player expectedPlayer = TestDataGenerator.generateValidPlayer();

        ResponseWrapper<PlayerResponse> actualPlayer = apiClient
                .createPlayer(PropertiesReader.getProperty("default.admin.login"), expectedPlayer);
        PlayerResponse actualPlayerResponseBody = actualPlayer.readEntity();

        actualPlayer.expectingStatusCode(200);
        assertNotNull(actualPlayerResponseBody.toString(), "Response body should not be null");

        ResponseWrapper<PlayerResponse> getPlayerResponse = apiClient.getPlayer(actualPlayerResponseBody.getPlayerId());
        getPlayerResponse.expectingStatusCode(200);
        PlayerResponse getlayerResponseBody = getPlayerResponse.readEntity();

        softAssert.assertEquals(getlayerResponseBody.getAge(), expectedPlayer.getAge(),
                "Age should be equal, but is not.");
        softAssert.assertEquals(getlayerResponseBody.getGender(), expectedPlayer.getGender(),
                "Gender should be equal, but is not.");
        softAssert.assertEquals(getlayerResponseBody.getLogin(), expectedPlayer.getLogin(),
                "Login should be equal, but is not.");
        softAssert.assertEquals(getlayerResponseBody.getPassword(), expectedPlayer.getPassword(),
                "Password should be equal, but is not.");
        softAssert.assertEquals(getlayerResponseBody.getRole(), expectedPlayer.getRole(),
                "Role should be equal, but is not.");
        softAssert.assertEquals(getlayerResponseBody.getScreenName(), expectedPlayer.getScreenName(),
                "ScreenName should be equal, but is not.");

        Integer playerId = getlayerResponseBody.getPlayerId();
        assertNotNull(playerId, "PlayerId should be not null");
        createdPlayerIds.add(playerId);

        softAssert.assertAll();
    }

    @Test(description = "Create player with admin role")
    public void testCreatePlayerWithAdminRole() {
        Player player = TestDataGenerator.generateValidPlayer("admin");
        ResponseWrapper<PlayerResponse> actualPlayer = apiClient
                .createPlayer(PropertiesReader.getProperty("default.supervisor.login"), player);
        actualPlayer.expectingStatusCode(200);
    }

    @Test(description = "Create player with user role")
    public void testCreatePlayerWithUserRole() {
        Player player = TestDataGenerator.generateValidPlayer("user");
        ResponseWrapper<PlayerResponse> actualPlayer = apiClient
                .createPlayer(PropertiesReader.getProperty("default.supervisor.login"), player);
        actualPlayer.expectingStatusCode(200);
    }

    // Negative tests for Create Player
    /**
     * I'll assume that we expect 400 error with valid message in response body, e.g. "Player age is too young/old."
     */
    @Test(description = "Create player with invalid age (too young)")
    public void testCreatePlayerWithInvalidAgeYoung() {
        Player player = TestDataGenerator.generatePlayerWithInvalidAgeYoung();
        Response response = apiClient
                .createPlayer(PropertiesReader.getProperty("default.supervisor.login"), player)
                .getResponse();
        assertEquals(response.getStatusCode(), 400, "Expected error for invalid age");
        assertEquals(response
                .as(ErrorBody.class).getTitle(), "Player age is too young/old.", "Expected error message");
    }

    @Test(description = "Create player with invalid age (too old)")
    public void testCreatePlayerWithInvalidAgeOld() {
        Player player = TestDataGenerator.generatePlayerWithInvalidAgeOld();
        Response response = apiClient
                .createPlayer(PropertiesReader.getProperty("default.supervisor.login"), player)
                .getResponse();
        assertEquals(response.getStatusCode(), 400, "Expected error for invalid age");
        assertEquals(response
                .as(ErrorBody.class).getTitle(), "Player age is too young/old.", "Expected error message");
    }

    /**
     * I'll assume that we expect 400 error with valid message in response body, e.g. "Gender can be male/female."
     */
    @Test(description = "Create player with invalid gender")
    public void testCreatePlayerWithInvalidGender() {
        Player player = TestDataGenerator.generatePlayerWithInvalidGender();
        Response response = apiClient
                .createPlayer(PropertiesReader.getProperty("default.supervisor.login"), player)
                .getResponse();
        assertEquals(response.getStatusCode(), 400, "Expected error for invalid gender");
        assertEquals(response
                .as(ErrorBody.class).getTitle(), "Gender can be male/female.", "Expected error message");
    }

    /**
     * I'll assume that we expect 400 error with valid message in response body, e.g.
     * "Password must contain latin letters and numbers (min 7 max 15 characters)."
     */
    @Test(description = "Create player with invalid password (too short)")
    public void testCreatePlayerWithInvalidPasswordShort() {
        Player player = TestDataGenerator.generatePlayerWithInvalidPasswordShort();
        Response response = apiClient
                .createPlayer(PropertiesReader.getProperty("default.supervisor.login"), player)
                .getResponse();
        assertEquals(response.getStatusCode(), 400, "Expected error for invalid password");
        assertEquals(response
                .as(ErrorBody.class).getTitle(), "Password must contain latin letters and numbers (min 7 max 15 characters).",
                "Expected error message");
    }

    @Test(description = "Create player with invalid password (too long)")
    public void testCreatePlayerWithInvalidPasswordLong() {
        Player player = TestDataGenerator.generatePlayerWithInvalidPasswordLong();
        Response response = apiClient
                .createPlayer(PropertiesReader.getProperty("default.supervisor.login"), player)
                .getResponse();
        assertEquals(response.getStatusCode(), 400, "Expected error for invalid password");
        assertEquals(response
                .as(ErrorBody.class).getTitle(), "Password must contain latin letters and numbers (min 7 max 15 characters).",
                "Expected error message");
    }

    @Test(description = "Create player with invalid password (no numbers)")
    public void testCreatePlayerWithInvalidPasswordNoNumbers() {
        Player player = TestDataGenerator.generatePlayerWithInvalidPasswordNoNumbers();
        Response response = apiClient
                .createPlayer(PropertiesReader.getProperty("default.supervisor.login"), player)
                .getResponse();
        assertEquals(response.getStatusCode(), 400, "Expected error for invalid password");
        assertEquals(response
                        .as(ErrorBody.class).getTitle(), "Password must contain latin letters and numbers (min 7 max 15 characters).",
                "Expected error message");
    }

    @Test(description = "Create player with invalid password (no letters)")
    public void testCreatePlayerWithInvalidPasswordNoLetters() {
        Player player = TestDataGenerator.generatePlayerWithInvalidPasswordNoLetters();
        Response response = apiClient
                .createPlayer(PropertiesReader.getProperty("default.supervisor.login"), player)
                .getResponse();
        assertEquals(response.getStatusCode(), 400, "Expected error for invalid password");
        assertEquals(response
                        .as(ErrorBody.class).getTitle(), "Password must contain latin letters and numbers (min 7 max 15 characters).",
                "Expected error message");
    }

    @Test(description = "Create player with invalid role")
    public void testCreatePlayerWithInvalidRole() {
        Player player = TestDataGenerator.generatePlayerWithInvalidRole();
        Response response = apiClient
                .createPlayer(PropertiesReader.getProperty("default.supervisor.login"), player)
                .getResponse();
        assertEquals(response.getStatusCode(), 400, "Expected error for invalid role");
        assertEquals(response
                        .as(ErrorBody.class).getTitle(), "User can be created only with one role from the list: ‘admin’ or ‘user’.",
                "Expected error message");
    }

    @Test(description = "Create player with null required fields")
    public void testCreatePlayerWithNullFields() {
        Player player = TestDataGenerator.generatePlayerWithNullFields();
        Response response = apiClient
                .createPlayer(PropertiesReader.getProperty("default.supervisor.login"), player)
                .getResponse();
        assertEquals(response.getStatusCode(), 400, "Expected error for null required fields");
        assertEquals(response
                        .as(ErrorBody.class).getTitle(), "Required fields missing.",
                "Expected error message");
    }

    @Test(description = "Create player with non-existent editor")
    public void testCreatePlayerWithNonExistentEditor() {
        Player player = TestDataGenerator.generateValidPlayer();
        Response response = apiClient
                .createPlayer("nonexistent_user", player)
                .getResponse();
        assertEquals(response.getStatusCode(), 400, "Expected error for non-existent editor");
        assertEquals(response
                        .as(ErrorBody.class).getTitle(), "Non-existent editor.",
                "Expected error message");
    }

}
