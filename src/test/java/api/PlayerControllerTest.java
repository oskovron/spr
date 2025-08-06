package api;

import api.client.ResponseWrapper;
import api.error.ErrorBody;
import api.error.NoSuchUserBody;
import api.model.request.Player;
import api.model.response.PlayerResponse;
import api.model.response.PlayersResponse;
import api.requests.PlayerApiClient;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import util.TestDataGenerator;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static common.PropertiesReader.getProperty;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.testng.Assert.assertEquals;
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
                .createPlayer(getProperty("default.supervisor.login"), expectedPlayer);
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
                .createPlayer(getProperty("default.supervisor.login"), expectedPlayer);
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
                .createPlayer(getProperty("default.admin.login"), expectedPlayer);
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

    @Test(description = "Create player with admin role")
    public void testCreatePlayerWithAdminRole() {
        Player player = TestDataGenerator.generateValidPlayer("admin");
        ResponseWrapper<PlayerResponse> actualPlayer = apiClient
                .createPlayer(getProperty("default.supervisor.login"), player);
        createdPlayerIds.add(actualPlayer.readEntity().getPlayerId());
        actualPlayer.expectingStatusCode(200);
    }

    @Test(description = "Create player with user role")
    public void testCreatePlayerWithUserRole() {
        Player player = TestDataGenerator.generateValidPlayer("user");
        ResponseWrapper<PlayerResponse> actualPlayer = apiClient
                .createPlayer(getProperty("default.supervisor.login"), player);
        createdPlayerIds.add(actualPlayer.readEntity().getPlayerId());
        actualPlayer.expectingStatusCode(200);
    }

    /**
     * I'll assume that we expect 400 error with valid message in response body, e.g. "Player age is too young/old."
     */
    @Test(description = "Create player with invalid age (too young)")
    public void testCreatePlayerWithInvalidAgeYoung() {
        Player player = TestDataGenerator.generatePlayerWithInvalidAgeYoung();
        Response response = apiClient
                .createPlayer(getProperty("default.supervisor.login"), player)
                .getResponse();
        assertEquals(response.getStatusCode(), 400, "Expected error for invalid age");
        assertEquals(response
                .as(ErrorBody.class).getTitle(), "Player age is too young/old.", "Expected error message");
    }

    @Test(description = "Create player with invalid age (too old)")
    public void testCreatePlayerWithInvalidAgeOld() {
        Player player = TestDataGenerator.generatePlayerWithInvalidAgeOld();
        Response response = apiClient
                .createPlayer(getProperty("default.supervisor.login"), player)
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
                .createPlayer(getProperty("default.supervisor.login"), player)
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
                .createPlayer(getProperty("default.supervisor.login"), player)
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
                .createPlayer(getProperty("default.supervisor.login"), player)
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
                .createPlayer(getProperty("default.supervisor.login"), player)
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
                .createPlayer(getProperty("default.supervisor.login"), player)
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
                .createPlayer(getProperty("default.supervisor.login"), player)
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
                .createPlayer(getProperty("default.supervisor.login"), player)
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

    @Test(description = "Create player with user editor")
    public void testCreatePlayerWithNonSuperAdminOrAdminEditor() {
        Player player = TestDataGenerator.generateValidPlayer();
        Response response = apiClient
                .createPlayer("user", player)
                .getResponse();
        assertEquals(response.getStatusCode(), 403, "Expected error for non SuperAdmin or admin editor");
        assertEquals(response
                        .as(ErrorBody.class).getTitle(), "Only those with role ‘supervisor’ or ‘admin’ can create users.",
                "Expected error message");
    }

    @Test(description = "Get player by valid ID using POST method")
    public void testGetPlayerByValidId() {
        // First create a player
        Player player = TestDataGenerator.generateValidPlayer();
        ResponseWrapper<PlayerResponse> actualPlayer = apiClient
                .createPlayer(getProperty("default.supervisor.login"), player);
        actualPlayer.expectingStatusCode(200);

        // Extract player ID from response
        Integer playerId = actualPlayer.readEntity().getPlayerId();
        assertNotNull(playerId, "PlayerId should be not null");
        createdPlayerIds.add(playerId);

        // Get the player
        ResponseWrapper<PlayerResponse> getPlayerResponse = apiClient.getPlayer(playerId);
        getPlayerResponse.expectingStatusCode(200);

        assertEquals(getPlayerResponse.getResponse().getStatusCode(), 200, "Expected status code 200");
        assertNotNull(getPlayerResponse.readEntity().toString(), "Response body should not be null");
    }

    @Test(description = "Get all players")
    public void testGetAllPlayers() {
        ResponseWrapper<PlayersResponse> allPlayers = apiClient.getAllPlayers();
        allPlayers.expectingStatusCode(200);
        assertNotNull(allPlayers.readEntity().toString(), "Response body should not be null");
        allPlayers.getResponse()
                .then()
                .assertThat()
                .body(matchesJsonSchemaInClasspath("schemas/players-schema.json"));
    }

    @Test(description = "Get player by non-existent ID")
    public void testGetPlayerByNonExistentId() {
        ResponseWrapper<PlayerResponse> getPlayerResponse = apiClient.getPlayer(999999);
        getPlayerResponse.expectingStatusCode(200);
        String responseMessage = getPlayerResponse.getResponse().as(NoSuchUserBody.class)
                .getTitle();
        assertEquals(responseMessage, "User does not exist.");
    }

    @Test(description = "Get player with null playerId")
    public void testGetPlayerWithNullId() {
        ResponseWrapper<PlayerResponse> getPlayerResponse = apiClient.getPlayer(null);
        getPlayerResponse.expectingStatusCode(400);
    }

    @Test(description = "Update player age")
    public void testUpdatePlayerAge() {
        SoftAssert softAssert = new SoftAssert();
        Player player = TestDataGenerator.generateValidPlayer();

        ResponseWrapper<PlayerResponse> actualPlayer = apiClient
                .createPlayer(getProperty("default.supervisor.login"), player);
        actualPlayer.expectingStatusCode(200);
        Integer playerId = actualPlayer.readEntity().getPlayerId();
        assertNotNull(playerId, "PlayerId should be not null");
        createdPlayerIds.add(playerId);
        ResponseWrapper<PlayerResponse> getPlayerResponse = apiClient.getPlayer(playerId);
        getPlayerResponse.expectingStatusCode(200);
        PlayerResponse getPlayerResponseBody = getPlayerResponse.readEntity();

        Player updateData = new Player();
        updateData.setAge(30);

        ResponseWrapper<PlayerResponse> updatedPlayerResponse =
                apiClient.updatePlayer(getProperty("default.supervisor.login"), playerId, updateData);
        updatedPlayerResponse.expectingStatusCode(200);

        ResponseWrapper<PlayerResponse> getPlayerAfterUpdateResponse = apiClient.getPlayer(playerId);
        getPlayerAfterUpdateResponse.expectingStatusCode(200);
        PlayerResponse getPlayerAfterUpdateResponseBody = getPlayerAfterUpdateResponse.readEntity();

        softAssert.assertEquals(getPlayerAfterUpdateResponseBody.getAge(), updatedPlayerResponse.readEntity().getAge(),
                "Age should be updated, but is not.");
        softAssert.assertEquals(getPlayerAfterUpdateResponseBody.getGender(), getPlayerResponseBody.getGender(),
                "Gender should be equal, but is not.");
        softAssert.assertEquals(getPlayerAfterUpdateResponseBody.getLogin(), getPlayerResponseBody.getLogin(),
                "Login should be equal, but is not.");
        softAssert.assertEquals(getPlayerAfterUpdateResponseBody.getPassword(), getPlayerResponseBody.getPassword(),
                "Password should be equal, but is not.");
        softAssert.assertEquals(getPlayerAfterUpdateResponseBody.getRole(), getPlayerResponseBody.getRole(),
                "Role should be equal, but is not.");
        softAssert.assertEquals(getPlayerAfterUpdateResponseBody.getScreenName(), getPlayerResponseBody.getScreenName(),
                "ScreenName should be equal, but is not.");

        softAssert.assertAll();
    }

    @Test(description = "Update player gender")
    public void testUpdatePlayerGender() {
        SoftAssert softAssert = new SoftAssert();
        Player player = TestDataGenerator.generateValidPlayer();

        ResponseWrapper<PlayerResponse> actualPlayer = apiClient
                .createPlayer(getProperty("default.supervisor.login"), player);
        actualPlayer.expectingStatusCode(200);
        Integer playerId = actualPlayer.readEntity().getPlayerId();
        assertNotNull(playerId, "PlayerId should be not null");
        createdPlayerIds.add(playerId);
        ResponseWrapper<PlayerResponse> getPlayerResponse = apiClient.getPlayer(playerId);
        getPlayerResponse.expectingStatusCode(200);
        PlayerResponse getPlayerResponseBody = getPlayerResponse.readEntity();

        Player updateData = new Player();
        updateData.setGender("female");

        ResponseWrapper<PlayerResponse> updatedPlayerResponse =
                apiClient.updatePlayer(getProperty("default.supervisor.login"), playerId, updateData);
        updatedPlayerResponse.expectingStatusCode(200);

        ResponseWrapper<PlayerResponse> getPlayerAfterUpdateResponse = apiClient.getPlayer(playerId);
        getPlayerAfterUpdateResponse.expectingStatusCode(200);
        PlayerResponse getPlayerAfterUpdateResponseBody = getPlayerAfterUpdateResponse.readEntity();

        softAssert.assertEquals(getPlayerAfterUpdateResponseBody.getAge(), getPlayerResponseBody.getAge(),
                "Age should be equal, but is not.");
        softAssert.assertEquals(getPlayerAfterUpdateResponseBody.getGender(), updatedPlayerResponse.readEntity().getGender(),
                "Gender should be updated, but is not.");
        softAssert.assertEquals(getPlayerAfterUpdateResponseBody.getLogin(), getPlayerResponseBody.getLogin(),
                "Login should be equal, but is not.");
        softAssert.assertEquals(getPlayerAfterUpdateResponseBody.getPassword(), getPlayerResponseBody.getPassword(),
                "Password should be equal, but is not.");
        softAssert.assertEquals(getPlayerAfterUpdateResponseBody.getRole(), getPlayerResponseBody.getRole(),
                "Role should be equal, but is not.");
        softAssert.assertEquals(getPlayerAfterUpdateResponseBody.getScreenName(), getPlayerResponseBody.getScreenName(),
                "ScreenName should be equal, but is not.");

        softAssert.assertAll();
    }

    @Test(description = "Update player with invalid age")
    public void testUpdatePlayerWithInvalidAge() {
        Player player = TestDataGenerator.generateValidPlayer();
        ResponseWrapper<PlayerResponse> actualPlayer = apiClient
                .createPlayer(getProperty("default.supervisor.login"), player);
        actualPlayer.expectingStatusCode(200);
        Integer playerId = actualPlayer.readEntity().getPlayerId();
        assertNotNull(playerId, "PlayerId should be not null");
        createdPlayerIds.add(playerId);
        ResponseWrapper<PlayerResponse> getPlayerResponse = apiClient.getPlayer(playerId);
        getPlayerResponse.expectingStatusCode(200);
        Player updateData = new Player();
        updateData.setAge(15);

        ResponseWrapper<PlayerResponse> updatedPlayerResponse = apiClient.updatePlayer(getProperty("default.supervisor.login"), playerId, updateData);
        updatedPlayerResponse.expectingStatusCode(403);
        ErrorBody errorBody = updatedPlayerResponse.getResponse().as(ErrorBody.class);
        assertEquals(errorBody.getTitle(), "User should be older than 16 and younger than 60 years old.");
    }

    @Test(description = "Update non-existent player")
    public void testUpdateNonExistentPlayer() {
        Player updateData = new Player();
        updateData.setAge(25);

        ResponseWrapper<PlayerResponse> updatePlayerResponse =
                apiClient.updatePlayer(getProperty("default.supervisor.login"), 999999, updateData);
        String responseMessage = updatePlayerResponse.getResponse().as(NoSuchUserBody.class)
                .getTitle();
        assertEquals(responseMessage, "User does not exist.");
    }

    @Test(description = "Update player with non-existent editor")
    public void testUpdatePlayerWithNonExistentEditor() {
        SoftAssert softAssert = new SoftAssert();
        Player player = TestDataGenerator.generateValidPlayer();
        ResponseWrapper<PlayerResponse> actualPlayer = apiClient
                .createPlayer(getProperty("default.supervisor.login"), player);
        actualPlayer.expectingStatusCode(200);
        Integer playerId = actualPlayer.readEntity().getPlayerId();
        assertNotNull(playerId, "PlayerId should be not null");
        createdPlayerIds.add(playerId);
        ResponseWrapper<PlayerResponse> getPlayerResponse = apiClient.getPlayer(playerId);
        getPlayerResponse.expectingStatusCode(200);
        PlayerResponse getPlayerResponseBody = getPlayerResponse.readEntity();

        Player updateData = new Player();
        updateData.setAge(25);

        ResponseWrapper<PlayerResponse> updatedPlayerResponse =
                apiClient.updatePlayer("nonexistent_editor", playerId, updateData);
        updatedPlayerResponse.expectingStatusCode(403);
    }

    @Test(description = "Delete player with supervisor")
    public void testDeletePlayerWithSupervisor() {
        Player player = TestDataGenerator.generateValidPlayer();
        ResponseWrapper<PlayerResponse> actualPlayer = apiClient
                .createPlayer(getProperty("default.supervisor.login"), player);
        actualPlayer.expectingStatusCode(200);
        Integer playerId = actualPlayer.readEntity().getPlayerId();
        assertNotNull(playerId, "PlayerId should be not null");
//        createdPlayerIds.add(playerId);
//        ResponseWrapper<PlayerResponse> getPlayerResponse = apiClient.getPlayer(playerId);
//        getPlayerResponse.expectingStatusCode(200);


        // Delete the player
        Response response = apiClient.deletePlayer(getProperty("default.supervisor.login"), playerId);

        assertEquals(response.getStatusCode(), 204, "Expected status code 204");

        //todo потім гетнути!
    }
//
//    @Test(description = "Delete player with admin")
//    public void testDeletePlayerWithAdmin() {
//        // First create a player
//        Player player = TestDataGenerator.generateValidPlayer();
//        Response createResponse = apiClient.createPlayer(config.getSupervisorLogin(), player);
//        Assert.assertEquals(createResponse.getStatusCode(), 200, "Player creation should succeed");
//
//        // Extract player ID from response
//        String responseBody = createResponse.getBody().asString();
//        int playerId = Integer.parseInt(responseBody.replaceAll("[^0-9]", ""));
//
//        // Delete the player
//        Response response = apiClient.deletePlayer(config.getAdminLogin(), playerId);
//
//        Assert.assertEquals(response.getStatusCode(), 200, "Expected status code 200");
//    }
//

//    @Test(description = "Delete non-existent player")
//    public void testDeleteNonExistentPlayer() {
//        Response response = apiClient.deletePlayer(config.getSupervisorLogin(), 999999);
//
//        Assert.assertNotEquals(response.getStatusCode(), 200, "Expected error for non-existent player");
//    }
//
//    @Test(description = "Delete player with non-existent editor")
//    public void testDeletePlayerWithNonExistentEditor() {
//        // First create a player
//        Player player = TestDataGenerator.generateValidPlayer();
//        Response createResponse = apiClient.createPlayer(config.getSupervisorLogin(), player);
//        Assert.assertEquals(createResponse.getStatusCode(), 200, "Player creation should succeed");
//
//        // Extract player ID from response
//        String responseBody = createResponse.getBody().asString();
//        int playerId = Integer.parseInt(responseBody.replaceAll("[^0-9]", ""));
//        threadLocalPlayerIds.get().add(playerId);
//
//        // Try to delete with non-existent editor
//        Response response = apiClient.deletePlayer("nonexistent_editor", playerId);
//
//        Assert.assertNotEquals(response.getStatusCode(), 200, "Expected error for non-existent editor");
//    }
//
//    // ==================== CRITICAL BUG TESTS ====================
//
//    @Test(description = "Test duplicate login constraint")

//    public void testDuplicateLoginConstraint() {
//        // Create first player
//        Player player1 = TestDataGenerator.generateValidPlayer();
//        Response createResponse1 = apiClient.createPlayer(config.getSupervisorLogin(), player1);
//        Assert.assertEquals(createResponse1.getStatusCode(), 200, "First player creation should succeed");
//
//        // Extract player ID from response
//        String responseBody1 = createResponse1.getBody().asString();
//        int playerId1 = Integer.parseInt(responseBody1.replaceAll("[^0-9]", ""));
//        threadLocalPlayerIds.get().add(playerId1);
//
//        // Try to create second player with same login
//        Player player2 = TestDataGenerator.generatePlayerWithDuplicateLogin(player1.getLogin());
//        Response createResponse2 = apiClient.createPlayer(config.getSupervisorLogin(), player2);
//
//        Assert.assertNotEquals(createResponse2.getStatusCode(), 200, "Expected error for duplicate login");
//    }
//
//    @Test(description = "Test duplicate screen name constraint")
//    @Story("Critical Bug Coverage")
//    @Severity(SeverityLevel.CRITICAL)
//    public void testDuplicateScreenNameConstraint() {
//        // Create first player
//        Player player1 = TestDataGenerator.generateValidPlayer();
//        Response createResponse1 = apiClient.createPlayer(config.getSupervisorLogin(), player1);
//        Assert.assertEquals(createResponse1.getStatusCode(), 200, "First player creation should succeed");
//
//        // Extract player ID from response
//        String responseBody1 = createResponse1.getBody().asString();
//        int playerId1 = Integer.parseInt(responseBody1.replaceAll("[^0-9]", ""));
//        threadLocalPlayerIds.get().add(playerId1);
//
//        // Try to create second player with same screen name
//        Player player2 = TestDataGenerator.generatePlayerWithDuplicateScreenName(player1.getScreenName());
//        Response createResponse2 = apiClient.createPlayer(config.getSupervisorLogin(), player2);
//
//        Assert.assertNotEquals(createResponse2.getStatusCode(), 200, "Expected error for duplicate screen name");
//    }
//
//    @Test(description = "Test supervisor cannot be deleted")
//    @Story("Critical Bug Coverage")
//    @Severity(SeverityLevel.CRITICAL)
//    public void testSupervisorCannotBeDeleted() {
//        // Try to delete supervisor (assuming supervisor has ID 1 or we can get it from get all)
//        Response response = apiClient.deletePlayer(config.getSupervisorLogin(), 1);
//
//        Assert.assertNotEquals(response.getStatusCode(), 200, "Expected error when trying to delete supervisor");
//    }
//
//    @Test(description = "Test user cannot create other users")
//    @Story("Critical Bug Coverage")
//    @Severity(SeverityLevel.CRITICAL)
//    public void testUserCannotCreateOtherUsers() {
//        // First create a regular user
//        Player userPlayer = TestDataGenerator.generateValidPlayer("user");
//        Response createUserResponse = apiClient.createPlayer(config.getSupervisorLogin(), userPlayer);
//        Assert.assertEquals(createUserResponse.getStatusCode(), 200, "User creation should succeed");
//
//        // Extract user ID from response
//        String responseBody = createUserResponse.getBody().asString();
//        int userId = Integer.parseInt(responseBody.replaceAll("[^0-9]", ""));
//        threadLocalPlayerIds.get().add(userId);
//
//        // Try to create another user using the regular user as editor
//        Player newPlayer = TestDataGenerator.generateValidPlayer();
//        Response createResponse = apiClient.createPlayer(userPlayer.getLogin(), newPlayer);
//
//        Assert.assertNotEquals(createResponse.getStatusCode(), 200, "Expected error when user tries to create another user");
//    }
//
//    @Test(description = "Test SQL injection prevention")
//    @Story("Critical Bug Coverage")
//    @Severity(SeverityLevel.CRITICAL)
//    public void testSqlInjectionPrevention() {
//        // Test SQL injection in login field
//        Player player = TestDataGenerator.generateValidPlayer();
//        player.setLogin("'; DROP TABLE players; --");
//
//        Response response = apiClient.createPlayer(config.getSupervisorLogin(), player);
//
//        // Should not succeed with SQL injection attempt
//        Assert.assertNotEquals(response.getStatusCode(), 200, "Expected error for SQL injection attempt");
//    }


}
