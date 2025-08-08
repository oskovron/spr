package api;

import api.client.ResponseWrapper;
import api.error.ErrorBody;
import api.error.NoSuchUserBody;
import api.model.request.Player;
import api.model.response.PlayerResponse;
import api.model.response.PlayersResponse;
import api.requests.PlayerApiClient;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import util.TestDataGenerator;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static common.Properties.DEFAULT_ADMIN_LOGIN;
import static common.Properties.DEFAULT_SUPERVISOR_LOGIN;
import static common.PropertiesReader.getProperty;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Test suite for Player Controller API.
 * Tests all CRUD operations and validation scenarios.
 */
@Epic("Player Management")
@Feature("Player Controller API")
public final class PlayerControllerTest {
    
    private static final Logger LOGGER = LogManager.getLogger(PlayerControllerTest.class);
    
    static {
        RestAssured.defaultParser = Parser.JSON;
    }

    private PlayerApiClient apiClient;
    private List<Integer> createdPlayerIds;

    @BeforeMethod
    @Step("Set up test environment")
    public void setUp() {
        LOGGER.info("Setting up PlayerControllerTest");
        apiClient = new PlayerApiClient();
        createdPlayerIds = new CopyOnWriteArrayList<>();
    }

    @AfterMethod
    @Step("Clean up test data")
    public void tearDown() {
        LOGGER.info("Cleaning up created players");
        for (Integer playerId : createdPlayerIds) {
            try {
                apiClient.deletePlayer(getProperty(DEFAULT_SUPERVISOR_LOGIN), playerId);
                LOGGER.info("Cleaned up player with ID: {}", playerId);
            } catch (Exception e) {
                LOGGER.warn("Failed to clean up player with ID: {}", playerId, e);
            }
        }
    }

    // Positive Tests

    @Test(description = "Create player with valid data using supervisor and verify response body")
    @Description("Verify that a player can be created successfully using supervisor role and response body matches expected data")
    public void testCreatePlayerWithValidDataUsingSupervisorVerifyResponseBody() {
        SoftAssert softAssert = new SoftAssert();
        Player expectedPlayer = TestDataGenerator.generateValidPlayer();
        PlayerResponse actualPlayer = createAndVerifyPlayer(expectedPlayer, getProperty(DEFAULT_SUPERVISOR_LOGIN));
        assertPlayerEquals(actualPlayer, expectedPlayer, softAssert);
        softAssert.assertAll("Player creation with supervisor verification failed");
    }

    @Test(description = "Create player with valid data using supervisor")
    @Description("Verify that a player can be created and retrieved successfully using supervisor role")
    public void testCreatePlayerWithValidDataUsingSupervisor() {
        SoftAssert softAssert = new SoftAssert();
        Player expectedPlayer = TestDataGenerator.generateValidPlayer();
        PlayerResponse actualPlayer = createAndVerifyPlayer(expectedPlayer, getProperty(DEFAULT_SUPERVISOR_LOGIN));

        ResponseWrapper getPlayerResponse = apiClient.getPlayer(actualPlayer.getPlayerId());
        getPlayerResponse.expectingStatusCode(200);
        PlayerResponse getPlayerResponseBody = getPlayerResponse.readEntity(PlayerResponse.class);
        assertPlayerEquals(getPlayerResponseBody, expectedPlayer, softAssert);
        softAssert.assertAll("Player creation and retrieval with supervisor failed");
    }

    @Test(description = "Create player with valid data using admin")
    @Description("Verify that a player can be created and retrieved successfully using admin role")
    public void testCreatePlayerWithValidDataUsingAdmin() {
        SoftAssert softAssert = new SoftAssert();
        Player expectedPlayer = TestDataGenerator.generateValidPlayer();
        PlayerResponse actualPlayer = createAndVerifyPlayer(expectedPlayer, getProperty(DEFAULT_ADMIN_LOGIN));

        ResponseWrapper getPlayerResponse = apiClient.getPlayer(actualPlayer.getPlayerId());
        getPlayerResponse.expectingStatusCode(200);
        PlayerResponse getPlayerResponseBody = getPlayerResponse.readEntity(PlayerResponse.class);
        assertPlayerEquals(getPlayerResponseBody, expectedPlayer, softAssert);
        softAssert.assertAll("Player creation and retrieval with admin failed");
    }

    @Test(description = "Create player with admin role")
    @Description("Verify that a player with admin role can be created successfully")
    public void testCreatePlayerWithAdminRole() {
        Player player = TestDataGenerator.generateValidPlayer("admin");
        ResponseWrapper actualPlayer = apiClient
                .createPlayer(getProperty(DEFAULT_SUPERVISOR_LOGIN), player);
        actualPlayer.expectingStatusCode(200);
        createdPlayerIds.add(actualPlayer.readEntity(PlayerResponse.class).getPlayerId());
    }

    @Test(description = "Create player with user role")
    @Description("Verify that a player with user role can be created successfully")
    public void testCreatePlayerWithUserRole() {
        Player player = TestDataGenerator.generateValidPlayer("user");
        ResponseWrapper actualPlayer = apiClient
                .createPlayer(getProperty(DEFAULT_SUPERVISOR_LOGIN), player);
        actualPlayer.expectingStatusCode(200);
        createdPlayerIds.add(actualPlayer.readEntity(PlayerResponse.class).getPlayerId());
    }

    // Negative Tests - Validation

    @Test(description = "Create player with invalid age (too young)")
    @Description("Verify that creating a player with age below minimum returns appropriate error")
    public void testCreatePlayerWithInvalidAgeYoung() {
        Player player = TestDataGenerator.generatePlayerWithInvalidAgeYoung();
        ResponseWrapper response = apiClient
                .createPlayer(getProperty(DEFAULT_SUPERVISOR_LOGIN), player);
        response.expectingStatusCode(400);
        ErrorBody errorBody = response.readError(ErrorBody.class);
        assertNotNull(errorBody, "Error body should not be null");
    }

    @Test(description = "Create player with invalid age (too old)")
    @Description("Verify that creating a player with age above maximum returns appropriate error")
    public void testCreatePlayerWithInvalidAgeOld() {
        Player player = TestDataGenerator.generatePlayerWithInvalidAgeOld();
        ResponseWrapper response = apiClient
                .createPlayer(getProperty(DEFAULT_SUPERVISOR_LOGIN), player);
        response.expectingStatusCode(400);
        ErrorBody errorBody = response.readError(ErrorBody.class);
        assertNotNull(errorBody, "Error body should not be null");
    }

    @Test(description = "Create player with invalid gender")
    @Description("Verify that creating a player with invalid gender returns appropriate error")
    public void testCreatePlayerWithInvalidGender() {
        Player player = TestDataGenerator.generatePlayerWithInvalidGender();
        ResponseWrapper response = apiClient
                .createPlayer(getProperty(DEFAULT_SUPERVISOR_LOGIN), player);
        response.expectingStatusCode(400);
        ErrorBody errorBody = response.readError(ErrorBody.class);
        assertNotNull(errorBody, "Error body should not be null");
    }

    @Test(description = "Create player with invalid password (too short)")
    @Description("Verify that creating a player with password below minimum length returns appropriate error")
    public void testCreatePlayerWithInvalidPasswordShort() {
        Player player = TestDataGenerator.generatePlayerWithInvalidPasswordShort();
        ResponseWrapper response = apiClient
                .createPlayer(getProperty(DEFAULT_SUPERVISOR_LOGIN), player);
        response.expectingStatusCode(400);
        ErrorBody errorBody = response.readError(ErrorBody.class);
        assertNotNull(errorBody, "Error body should not be null");
    }

    @Test(description = "Create player with invalid password (too long)")
    @Description("Verify that creating a player with password above maximum length returns appropriate error")
    public void testCreatePlayerWithInvalidPasswordLong() {
        Player player = TestDataGenerator.generatePlayerWithInvalidPasswordLong();
        ResponseWrapper response = apiClient
                .createPlayer(getProperty(DEFAULT_SUPERVISOR_LOGIN), player);
        response.expectingStatusCode(400);
        ErrorBody errorBody = response.readError(ErrorBody.class);
        assertNotNull(errorBody, "Error body should not be null");
    }

    @Test(description = "Create player with invalid password (no numbers)")
    @Description("Verify that creating a player with password containing no numbers returns appropriate error")
    public void testCreatePlayerWithInvalidPasswordNoNumbers() {
        Player player = TestDataGenerator.generatePlayerWithInvalidPasswordNoNumbers();
        ResponseWrapper response = apiClient
                .createPlayer(getProperty(DEFAULT_SUPERVISOR_LOGIN), player);
        response.expectingStatusCode(400);
        ErrorBody errorBody = response.readError(ErrorBody.class);
        assertNotNull(errorBody, "Error body should not be null");
    }

    @Test(description = "Create player with invalid password (no letters)")
    @Description("Verify that creating a player with password containing no letters returns appropriate error")
    public void testCreatePlayerWithInvalidPasswordNoLetters() {
        Player player = TestDataGenerator.generatePlayerWithInvalidPasswordNoLetters();
        ResponseWrapper response = apiClient
                .createPlayer(getProperty(DEFAULT_SUPERVISOR_LOGIN), player);
        response.expectingStatusCode(400);
        ErrorBody errorBody = response.readError(ErrorBody.class);
        assertNotNull(errorBody, "Error body should not be null");
    }

    @Test(description = "Create player with invalid role")
    @Description("Verify that creating a player with invalid role returns appropriate error")
    public void testCreatePlayerWithInvalidRole() {
        Player player = TestDataGenerator.generatePlayerWithInvalidRole();
        ResponseWrapper response = apiClient
                .createPlayer(getProperty(DEFAULT_SUPERVISOR_LOGIN), player);
        response.expectingStatusCode(400);
        ErrorBody errorBody = response.readError(ErrorBody.class);
        assertNotNull(errorBody, "Error body should not be null");
    }

    @Test(description = "Create player with null required fields")
    @Description("Verify that creating a player with null required fields returns appropriate error")
    public void testCreatePlayerWithNullFields() {
        Player player = TestDataGenerator.generatePlayerWithNullFields();
        ResponseWrapper response = apiClient
                .createPlayer(getProperty(DEFAULT_SUPERVISOR_LOGIN), player);
        response.expectingStatusCode(400);
        ErrorBody errorBody = response.readError(ErrorBody.class);
        assertNotNull(errorBody, "Error body should not be null");
    }

    // Negative Tests - Authorization

    @Test(description = "Create player with non-existent editor")
    @Description("Verify that creating a player with non-existent editor returns appropriate error")
    public void testCreatePlayerWithNonExistentEditor() {
        Player player = TestDataGenerator.generateValidPlayer();
        ResponseWrapper response = apiClient
                .createPlayer("non_existent_editor", player);
        response.expectingStatusCode(404);
        ErrorBody errorBody = response.readError(ErrorBody.class);
        assertNotNull(errorBody, "Error body should not be null");
    }

    @Test(description = "Create player with user editor")
    @Description("Verify that creating a player with user editor returns appropriate error")
    public void testCreatePlayerWithNonSuperAdminOrAdminEditor() {
        Player player = TestDataGenerator.generateValidPlayer();
        ResponseWrapper response = apiClient
                .createPlayer("user", player);
        response.expectingStatusCode(403);
        ErrorBody errorBody = response.readError(ErrorBody.class);
        assertNotNull(errorBody, "Error body should not be null");
    }

    // GET Operations

    @Test(description = "Get player by valid ID using POST method")
    @Description("Verify that a player can be retrieved successfully by valid ID")
    public void testGetPlayerByValidId() {
        Player player = TestDataGenerator.generateValidPlayer();
        PlayerResponse createdPlayer = createAndVerifyPlayer(player, getProperty(DEFAULT_SUPERVISOR_LOGIN));
        
        ResponseWrapper getPlayerResponse = apiClient.getPlayer(createdPlayer.getPlayerId());
        getPlayerResponse.expectingStatusCode(200);
        PlayerResponse retrievedPlayer = getPlayerResponse.readEntity(PlayerResponse.class);
        assertNotNull(retrievedPlayer, "Retrieved player should not be null");
        assertEquals(retrievedPlayer.getPlayerId(), createdPlayer.getPlayerId(), 
                "Player ID should match the created player");
    }

    @Test(description = "Get all players")
    @Description("Verify that all players can be retrieved successfully")
    public void testGetAllPlayers() {
        ResponseWrapper response = apiClient.getAllPlayers();
        response.expectingStatusCode(200);
        PlayersResponse playersResponse = response.readEntity(PlayersResponse.class);
        assertNotNull(playersResponse, "Players response should not be null");
        assertNotNull(playersResponse.getPlayers(), "Players list should not be null");
    }

    @Test(description = "Get player by non-existent ID")
    @Description("Verify that getting a player with non-existent ID returns appropriate error")
    public void testGetPlayerByNonExistentId() {
        ResponseWrapper response = apiClient.getPlayer(99999);
        response.expectingStatusCode(404);
        NoSuchUserBody errorBody = response.readError(NoSuchUserBody.class);
        assertNotNull(errorBody, "Error body should not be null");
    }

    @Test(description = "Get player with null playerId")
    @Description("Verify that getting a player with null ID returns appropriate error")
    public void testGetPlayerWithNullId() {
        ResponseWrapper response = apiClient.getPlayer(null);
        response.expectingStatusCode(400);
        ErrorBody errorBody = response.readError(ErrorBody.class);
        assertNotNull(errorBody, "Error body should not be null");
    }

    // UPDATE Operations

    @Test(description = "Update player age")
    @Description("Verify that a player's age can be updated successfully")
    public void testUpdatePlayerAge() {
        Player originalPlayer = TestDataGenerator.generateValidPlayer();
        PlayerResponse createdPlayer = createAndVerifyPlayer(originalPlayer, getProperty(DEFAULT_SUPERVISOR_LOGIN));
        
        Player updatePlayer = new Player();
        updatePlayer.setAge(30);
        
        ResponseWrapper updateResponse = apiClient.updatePlayer(
                getProperty(DEFAULT_SUPERVISOR_LOGIN), createdPlayer.getPlayerId(), updatePlayer);
        updateResponse.expectingStatusCode(200);
        PlayerResponse updatedPlayer = updateResponse.readEntity(PlayerResponse.class);
        
        assertEquals(updatedPlayer.getAge(), Integer.valueOf(30), 
                "Player age should be updated to 30");
        assertEquals(updatedPlayer.getPlayerId(), createdPlayer.getPlayerId(), 
                "Player ID should remain the same");
    }

    @Test(description = "Update player gender")
    @Description("Verify that a player's gender can be updated successfully")
    public void testUpdatePlayerGender() {
        Player originalPlayer = TestDataGenerator.generateValidPlayer();
        PlayerResponse createdPlayer = createAndVerifyPlayer(originalPlayer, getProperty(DEFAULT_SUPERVISOR_LOGIN));
        
        Player updatePlayer = new Player();
        updatePlayer.setGender("female");
        
        ResponseWrapper updateResponse = apiClient.updatePlayer(
                getProperty(DEFAULT_SUPERVISOR_LOGIN), createdPlayer.getPlayerId(), updatePlayer);
        updateResponse.expectingStatusCode(200);
        PlayerResponse updatedPlayer = updateResponse.readEntity(PlayerResponse.class);
        
        assertEquals(updatedPlayer.getGender(), "female", 
                "Player gender should be updated to female");
        assertEquals(updatedPlayer.getPlayerId(), createdPlayer.getPlayerId(), 
                "Player ID should remain the same");
    }

    @Test(description = "Update player with invalid age")
    @Description("Verify that updating a player with invalid age returns appropriate error")
    public void testUpdatePlayerWithInvalidAge() {
        Player originalPlayer = TestDataGenerator.generateValidPlayer();
        PlayerResponse createdPlayer = createAndVerifyPlayer(originalPlayer, getProperty(DEFAULT_SUPERVISOR_LOGIN));
        
        Player updatePlayer = new Player();
        updatePlayer.setAge(150); // Invalid age
        
        ResponseWrapper response = apiClient.updatePlayer(
                getProperty(DEFAULT_SUPERVISOR_LOGIN), createdPlayer.getPlayerId(), updatePlayer);
        response.expectingStatusCode(400);
        ErrorBody errorBody = response.readError(ErrorBody.class);
        assertNotNull(errorBody, "Error body should not be null");
    }

    @Test(description = "Update non-existent player")
    @Description("Verify that updating a non-existent player returns appropriate error")
    public void testUpdateNonExistentPlayer() {
        Player updatePlayer = new Player();
        updatePlayer.setAge(30);
        
        ResponseWrapper response = apiClient.updatePlayer(
                getProperty(DEFAULT_SUPERVISOR_LOGIN), 99999, updatePlayer);
        response.expectingStatusCode(404);
        NoSuchUserBody errorBody = response.readError(NoSuchUserBody.class);
        assertNotNull(errorBody, "Error body should not be null");
    }

    @Test(description = "Update player with non-existent editor")
    @Description("Verify that updating a player with non-existent editor returns appropriate error")
    public void testUpdatePlayerWithNonExistentEditor() {
        Player originalPlayer = TestDataGenerator.generateValidPlayer();
        PlayerResponse createdPlayer = createAndVerifyPlayer(originalPlayer, getProperty(DEFAULT_SUPERVISOR_LOGIN));
        
        Player updatePlayer = new Player();
        updatePlayer.setAge(30);
        
        ResponseWrapper response = apiClient.updatePlayer(
                "non_existent_editor", createdPlayer.getPlayerId(), updatePlayer);
        response.expectingStatusCode(404);
        ErrorBody errorBody = response.readError(ErrorBody.class);
        assertNotNull(errorBody, "Error body should not be null");
    }

    // DELETE Operations

    @Test(description = "Delete player with supervisor")
    @Description("Verify that a player can be deleted successfully using supervisor role")
    public void testDeletePlayerWithSupervisor() {
        Player player = TestDataGenerator.generateValidPlayer();
        PlayerResponse createdPlayer = createAndVerifyPlayer(player, getProperty(DEFAULT_SUPERVISOR_LOGIN));
        
        Response deleteResponse = apiClient.deletePlayer(
                getProperty(DEFAULT_SUPERVISOR_LOGIN), createdPlayer.getPlayerId());
        assertEquals(deleteResponse.getStatusCode(), 200, 
                "Delete operation should return 200 status code");
    }

    @Test(description = "Delete player with admin")
    @Description("Verify that a player can be deleted successfully using admin role")
    public void testDeletePlayerWithAdmin() {
        Player player = TestDataGenerator.generateValidPlayer();
        PlayerResponse createdPlayer = createAndVerifyPlayer(player, getProperty(DEFAULT_ADMIN_LOGIN));
        
        Response deleteResponse = apiClient.deletePlayer(
                getProperty(DEFAULT_ADMIN_LOGIN), createdPlayer.getPlayerId());
        assertEquals(deleteResponse.getStatusCode(), 200, 
                "Delete operation should return 200 status code");
    }

    @Test(description = "Delete non-existent player")
    @Description("Verify that deleting a non-existent player returns appropriate error")
    public void testDeleteNonExistentPlayer() {
        Response deleteResponse = apiClient.deletePlayer(
                getProperty(DEFAULT_SUPERVISOR_LOGIN), 99999);
        assertEquals(deleteResponse.getStatusCode(), 404, 
                "Delete operation should return 404 status code for non-existent player");
    }

    @Test(description = "Delete player with non-existent editor")
    @Description("Verify that deleting a player with non-existent editor returns appropriate error")
    public void testDeletePlayerWithNonExistentEditor() {
        Response deleteResponse = apiClient.deletePlayer("non_existent_editor", 1);
        assertEquals(deleteResponse.getStatusCode(), 404, 
                "Delete operation should return 404 status code for non-existent editor");
    }

    // Business Logic Tests

    @Test(description = "Test duplicate login constraint")
    @Description("Verify that creating a player with duplicate login returns appropriate error")
    public void testDuplicateLoginConstraint() {
        Player firstPlayer = TestDataGenerator.generateValidPlayer();
        PlayerResponse createdPlayer = createAndVerifyPlayer(firstPlayer, getProperty(DEFAULT_SUPERVISOR_LOGIN));
        
        Player duplicatePlayer = TestDataGenerator.generatePlayerWithDuplicateLogin(firstPlayer.getLogin());
        ResponseWrapper response = apiClient
                .createPlayer(getProperty(DEFAULT_SUPERVISOR_LOGIN), duplicatePlayer);
        response.expectingStatusCode(409);
        ErrorBody errorBody = response.readError(ErrorBody.class);
        assertNotNull(errorBody, "Error body should not be null");
    }

    @Test(description = "Test supervisor cannot be deleted")
    @Description("Verify that supervisor user cannot be deleted")
    public void testSupervisorCannotBeDeleted() {
        Response deleteResponse = apiClient.deletePlayer(
                getProperty(DEFAULT_SUPERVISOR_LOGIN), 1); // Assuming supervisor has ID 1
        assertEquals(deleteResponse.getStatusCode(), 403, 
                "Delete operation should return 403 status code for supervisor");
    }

    @Test(description = "Test user cannot create other users")
    @Description("Verify that regular user cannot create other users")
    public void testUserCannotCreateOtherUsers() {
        Player player = TestDataGenerator.generateValidPlayer();
        ResponseWrapper response = apiClient
                .createPlayer("user", player);
        response.expectingStatusCode(403);
        ErrorBody errorBody = response.readError(ErrorBody.class);
        assertNotNull(errorBody, "Error body should not be null");
    }

    // Helper Methods

    @Step("Create and verify player with editor: {editor}")
    private PlayerResponse createAndVerifyPlayer(final Player player, final String editor) {
        ResponseWrapper response = apiClient.createPlayer(editor, player);
        response.expectingStatusCode(200);
        PlayerResponse createdPlayer = response.readEntity(PlayerResponse.class);
        assertNotNull(createdPlayer, "Created player should not be null");
        assertNotNull(createdPlayer.getPlayerId(), "Created player should have an ID");
        createdPlayerIds.add(createdPlayer.getPlayerId());
        return createdPlayer;
    }

    @Step("Assert player equals")
    private void assertPlayerEquals(final PlayerResponse actual, final Player expected, final SoftAssert softAssert) {
        softAssert.assertEquals(actual.getAge(), expected.getAge(), 
                "Player age should match expected value");
        softAssert.assertEquals(actual.getGender(), expected.getGender(), 
                "Player gender should match expected value");
        softAssert.assertEquals(actual.getLogin(), expected.getLogin(), 
                "Player login should match expected value");
        softAssert.assertEquals(actual.getRole(), expected.getRole(), 
                "Player role should match expected value");
        softAssert.assertEquals(actual.getScreenName(), expected.getScreenName(), 
                "Player screen name should match expected value");
    }
}
