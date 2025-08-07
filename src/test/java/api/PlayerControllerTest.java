package api;

import api.client.ResponseWrapper;
import api.error.ErrorBody;
import api.error.NoSuchUserBody;
import api.model.request.Player;
import api.model.response.PlayerResponse;
import api.model.response.PlayersResponse;
import api.requests.PlayerApiClient;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
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

import static common.Properties.DEFAULT_ADMIN_LOGIN;
import static common.Properties.DEFAULT_SUPERVISOR_LOGIN;
import static common.PropertiesReader.getProperty;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.testng.Assert.*;

public class PlayerControllerTest {
    static {
        RestAssured.defaultParser = Parser.JSON;
    }

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
        for (Integer playerId : createdPlayerIds) {
            try {
                apiClient.deletePlayer(getProperty(DEFAULT_SUPERVISOR_LOGIN), playerId);
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
        PlayerResponse actualPlayer = createAndVerifyPlayer(expectedPlayer, getProperty(DEFAULT_SUPERVISOR_LOGIN));
        assertPlayerEquals(actualPlayer, expectedPlayer, softAssert);
    }

    @Test(description = "Create player with valid data using supervisor")
    public void testCreatePlayerWithValidDataUsingSupervisor() {
        SoftAssert softAssert = new SoftAssert();
        Player expectedPlayer = TestDataGenerator.generateValidPlayer();
        PlayerResponse actualPlayer = createAndVerifyPlayer(expectedPlayer, getProperty(DEFAULT_SUPERVISOR_LOGIN));

        ResponseWrapper<PlayerResponse> getPlayerResponse = apiClient.getPlayer(actualPlayer.getPlayerId());
        getPlayerResponse.expectingStatusCode(200);
        PlayerResponse getPlayerResponseBody = getPlayerResponse.readEntity();
        assertPlayerEquals(getPlayerResponseBody, expectedPlayer, softAssert);
    }

    @Test(description = "Create player with valid data using admin")
    public void testCreatePlayerWithValidDataUsingAdmin() {
        SoftAssert softAssert = new SoftAssert();
        Player expectedPlayer = TestDataGenerator.generateValidPlayer();
        PlayerResponse actualPlayer = createAndVerifyPlayer(expectedPlayer, getProperty(DEFAULT_ADMIN_LOGIN));

        ResponseWrapper<PlayerResponse> getPlayerResponse = apiClient.getPlayer(actualPlayer.getPlayerId());
        getPlayerResponse.expectingStatusCode(200);
        PlayerResponse getPlayerResponseBody = getPlayerResponse.readEntity();
        assertPlayerEquals(getPlayerResponseBody, expectedPlayer, softAssert);
    }

    @Test(description = "Create player with admin role")
    public void testCreatePlayerWithAdminRole() {
        Player player = TestDataGenerator.generateValidPlayer("admin");
        ResponseWrapper<PlayerResponse> actualPlayer = apiClient
                .createPlayer(getProperty(DEFAULT_SUPERVISOR_LOGIN), player);
        actualPlayer.expectingStatusCode(200);
        createdPlayerIds.add(actualPlayer.readEntity().getPlayerId());
    }

    @Test(description = "Create player with user role")
    public void testCreatePlayerWithUserRole() {
        Player player = TestDataGenerator.generateValidPlayer("user");
        ResponseWrapper<PlayerResponse> actualPlayer = apiClient
                .createPlayer(getProperty(DEFAULT_SUPERVISOR_LOGIN), player);
        actualPlayer.expectingStatusCode(200);
        createdPlayerIds.add(actualPlayer.readEntity().getPlayerId());
    }

    /**
     * I'll assume that we expect 400 error with valid message in response body, e.g. "Player age is too young/old."
     */
    @Test(description = "Create player with invalid age (too young)")
    public void testCreatePlayerWithInvalidAgeYoung() {
        Player player = TestDataGenerator.generatePlayerWithInvalidAgeYoung();
        Response response = apiClient
                .createPlayer(getProperty(DEFAULT_SUPERVISOR_LOGIN), player)
                .getResponse();
        assertEquals(response.getStatusCode(), 400, "Expected error for invalid age");

        try {
            String title = response.as(ErrorBody.class).getTitle();
            assertEquals(title, "Player age is too young/old.",
                    "Expected error message");
        } catch (RuntimeException e) {
            logger.error("Could not parse ErrorBody from response", response.asString());
            fail("Response body was not JSON or empty", e);
        }
    }

    @Test(description = "Create player with invalid age (too old)")
    public void testCreatePlayerWithInvalidAgeOld() {
        Player player = TestDataGenerator.generatePlayerWithInvalidAgeOld();
        Response response = apiClient
                .createPlayer(getProperty(DEFAULT_SUPERVISOR_LOGIN), player)
                .getResponse();
        assertEquals(response.getStatusCode(), 400, "Expected error for invalid age");

        try {
            String title = response.as(ErrorBody.class).getTitle();
            assertEquals(title, "Player age is too young/old.",
                    "Expected error message");
        } catch (RuntimeException e) {
            logger.error("Could not parse ErrorBody from response", response.asString());
            fail("Response body was not JSON or empty", e);
        }
    }

    /**
     * I'll assume that we expect 400 error with valid message in response body, e.g. "Gender can be male/female."
     */
    @Test(description = "Create player with invalid gender")
    public void testCreatePlayerWithInvalidGender() {
        Player player = TestDataGenerator.generatePlayerWithInvalidGender();
        Response response = apiClient
                .createPlayer(getProperty(DEFAULT_SUPERVISOR_LOGIN), player)
                .getResponse();
        assertEquals(response.getStatusCode(), 400, "Expected error for invalid gender");

        try {
            String title = response.as(ErrorBody.class).getTitle();
            assertEquals(title, "Gender can be male/female.",
                    "Expected error message");
        } catch (RuntimeException e) {
            logger.error("Could not parse ErrorBody from response", response.asString());
            fail("Response body was not JSON or empty", e);
        }
    }

    /**
     * I'll assume that we expect 400 error with valid message in response body, e.g.
     * "Password must contain latin letters and numbers (min 7 max 15 characters)."
     */
    @Test(description = "Create player with invalid password (too short)")
    public void testCreatePlayerWithInvalidPasswordShort() {
        Player player = TestDataGenerator.generatePlayerWithInvalidPasswordShort();
        Response response = apiClient
                .createPlayer(getProperty(DEFAULT_SUPERVISOR_LOGIN), player)
                .getResponse();
        assertEquals(response.getStatusCode(), 400, "Expected error for invalid password");

        try {
            String title = response.as(ErrorBody.class).getTitle();
            assertEquals(title, "Password must contain latin letters and numbers (min 7 max 15 characters).",
                    "Expected error message");
        } catch (RuntimeException e) {
            logger.error("Could not parse ErrorBody from response", response.asString());
            fail("Response body was not JSON or empty", e);
        }
    }

    @Test(description = "Create player with invalid password (too long)")
    public void testCreatePlayerWithInvalidPasswordLong() {
        Player player = TestDataGenerator.generatePlayerWithInvalidPasswordLong();
        Response response = apiClient
                .createPlayer(getProperty(DEFAULT_SUPERVISOR_LOGIN), player)
                .getResponse();
        assertEquals(response.getStatusCode(), 400, "Expected error for invalid password");

        try {
            String title = response.as(ErrorBody.class).getTitle();
            assertEquals(title, "Password must contain latin letters and numbers (min 7 max 15 characters).",
                    "Expected error message");
        } catch (RuntimeException e) {
            logger.error("Could not parse ErrorBody from response", response.asString());
            fail("Response body was not JSON or empty", e);
        }
    }

    @Test(description = "Create player with invalid password (no numbers)")
    public void testCreatePlayerWithInvalidPasswordNoNumbers() {
        Player player = TestDataGenerator.generatePlayerWithInvalidPasswordNoNumbers();
        Response response = apiClient
                .createPlayer(getProperty(DEFAULT_SUPERVISOR_LOGIN), player)
                .getResponse();
        assertEquals(response.getStatusCode(), 400, "Expected error for invalid password");

        try {
            String title = response.as(ErrorBody.class).getTitle();
            assertEquals(title, "Password must contain latin letters and numbers (min 7 max 15 characters).",
                    "Expected error message");
        } catch (RuntimeException e) {
            logger.error("Could not parse ErrorBody from response", response.asString());
            fail("Response body was not JSON or empty", e);
        }
    }

    @Test(description = "Create player with invalid password (no letters)")
    public void testCreatePlayerWithInvalidPasswordNoLetters() {
        Player player = TestDataGenerator.generatePlayerWithInvalidPasswordNoLetters();
        Response response = apiClient
                .createPlayer(getProperty(DEFAULT_SUPERVISOR_LOGIN), player)
                .getResponse();
        assertEquals(response.getStatusCode(), 400, "Expected error for invalid password");

        try {
            String title = response.as(ErrorBody.class).getTitle();
            assertEquals(title, "Password must contain latin letters and numbers (min 7 max 15 characters).",
                    "Expected error message");
        } catch (RuntimeException e) {
            logger.error("Could not parse ErrorBody from response", response.asString());
            fail("Response body was not JSON or empty", e);
        }
    }

    @Test(description = "Create player with invalid role")
    public void testCreatePlayerWithInvalidRole() {
        Player player = TestDataGenerator.generatePlayerWithInvalidRole();
        Response response = apiClient
                .createPlayer(getProperty(DEFAULT_SUPERVISOR_LOGIN), player)
                .getResponse();
        assertEquals(response.getStatusCode(), 400, "Expected error for invalid role");

        try {
            String title = response.as(ErrorBody.class).getTitle();
            assertEquals(title, "User can be created only with one role from the list: ‘admin’ or ‘user’.",
                    "Expected error message");
        } catch (RuntimeException e) {
            logger.error("Could not parse ErrorBody from response", response.asString());
            fail("Response body was not JSON or empty", e);
        }
    }

    @Test(description = "Create player with null required fields")
    public void testCreatePlayerWithNullFields() {
        Player player = TestDataGenerator.generatePlayerWithNullFields();
        Response response = apiClient
                .createPlayer(getProperty(DEFAULT_SUPERVISOR_LOGIN), player)
                .getResponse();
        assertEquals(response.getStatusCode(), 400, "Expected error for null required fields");

        try {
            String title = response.as(ErrorBody.class).getTitle();
            assertEquals(title, "Required fields missing.",
                    "Expected error message");
        } catch (RuntimeException e) {
            logger.error("Could not parse ErrorBody from response", response.asString());
            fail("Response body was not JSON or empty", e);
        }
    }

    @Test(description = "Create player with non-existent editor")
    public void testCreatePlayerWithNonExistentEditor() {
        Player player = TestDataGenerator.generateValidPlayer();
        Response response = apiClient
                .createPlayer("nonexistent_user", player)
                .getResponse();
        assertEquals(response.getStatusCode(), 403, "Expected error for non-existent editor");

        try {
            String title = response.as(ErrorBody.class).getTitle();
            assertEquals(title, "Non-existent editor.",
                    "Expected error message");
        } catch (RuntimeException e) {
            logger.error("Could not parse ErrorBody from response", response.asString());
            fail("Response body was not JSON or empty", e);
        }
    }

    @Test(description = "Create player with user editor")
    public void testCreatePlayerWithNonSuperAdminOrAdminEditor() {
        Player player = TestDataGenerator.generateValidPlayer();
        Response response = apiClient
                .createPlayer("user", player)
                .getResponse();
        assertEquals(response.getStatusCode(), 403, "Expected error for non SuperAdmin or admin editor");

        try {
            String title = response.as(ErrorBody.class).getTitle();
            assertEquals(title, "Only those with role ‘supervisor’ or ‘admin’ can create users.",
                    "Expected error message");
        } catch (RuntimeException e) {
            logger.error("Could not parse ErrorBody from response", response.asString());
            fail("Response body was not JSON or empty", e);
        }
    }

    @Test(description = "Get player by valid ID using POST method")
    public void testGetPlayerByValidId() {
        Player player = TestDataGenerator.generateValidPlayer();
        PlayerResponse actualPlayer = createAndVerifyPlayer(player, getProperty(DEFAULT_SUPERVISOR_LOGIN));

        ResponseWrapper<PlayerResponse> getPlayerResponse = apiClient.getPlayer(actualPlayer.getPlayerId());
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

        try {
            String title = getPlayerResponse.getResponse().as(NoSuchUserBody.class)
                    .getTitle();
            assertEquals(title, "User does not exist.", "Expected error message");
        } catch (RuntimeException e) {
            logger.error("Could not parse NoSuchUserBody from response", getPlayerResponse.toString());
            fail("Response body was not JSON or empty", e);
        }

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
        PlayerResponse actualPlayer = createAndVerifyPlayer(player, getProperty(DEFAULT_SUPERVISOR_LOGIN));

        ResponseWrapper<PlayerResponse> getPlayerResponse = apiClient.getPlayer(actualPlayer.getPlayerId());
        getPlayerResponse.expectingStatusCode(200);
        PlayerResponse getPlayerResponseBody = getPlayerResponse.readEntity();

        Player updateData = new Player();
        updateData.setAge(30);
        ResponseWrapper<PlayerResponse> updatedPlayerResponse =
                apiClient.updatePlayer(getProperty(DEFAULT_SUPERVISOR_LOGIN), actualPlayer.getPlayerId(), updateData);
        updatedPlayerResponse.expectingStatusCode(200);

        ResponseWrapper<PlayerResponse> getPlayerAfterUpdateResponse = apiClient.getPlayer(actualPlayer.getPlayerId());
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
        PlayerResponse actualPlayer = createAndVerifyPlayer(player, getProperty(DEFAULT_SUPERVISOR_LOGIN));

        ResponseWrapper<PlayerResponse> getPlayerResponse = apiClient.getPlayer(actualPlayer.getPlayerId());
        getPlayerResponse.expectingStatusCode(200);
        PlayerResponse getPlayerResponseBody = getPlayerResponse.readEntity();

        Player updateData = new Player();
        updateData.setGender("female");

        ResponseWrapper<PlayerResponse> updatedPlayerResponse =
                apiClient.updatePlayer(getProperty(DEFAULT_SUPERVISOR_LOGIN), actualPlayer.getPlayerId(), updateData);
        updatedPlayerResponse.expectingStatusCode(200);

        ResponseWrapper<PlayerResponse> getPlayerAfterUpdateResponse = apiClient.getPlayer(actualPlayer.getPlayerId());
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
        PlayerResponse actualPlayer = createAndVerifyPlayer(player, getProperty(DEFAULT_SUPERVISOR_LOGIN));

        ResponseWrapper<PlayerResponse> getPlayerResponse = apiClient.getPlayer(actualPlayer.getPlayerId());
        getPlayerResponse.expectingStatusCode(200);
        Player updateData = new Player();
        updateData.setAge(15);

        ResponseWrapper<PlayerResponse> updatedPlayerResponse = apiClient.updatePlayer(
                getProperty(DEFAULT_SUPERVISOR_LOGIN), actualPlayer.getPlayerId(), updateData);
        updatedPlayerResponse.expectingStatusCode(403);

        try {
            String title = updatedPlayerResponse.getResponse().as(ErrorBody.class).getTitle();
            assertEquals(title, "User should be older than 16 and younger than 60 years old.",
                    "Expected error message");
        } catch (RuntimeException e) {
            logger.error("Could not parse ErrorBody from response", updatedPlayerResponse.toString());
            fail("Response body was not JSON or empty", e);
        }
    }

    @Test(description = "Update non-existent player")
    public void testUpdateNonExistentPlayer() {
        Player updateData = new Player();
        updateData.setAge(25);

        ResponseWrapper<PlayerResponse> updatePlayerResponse =
                apiClient.updatePlayer(getProperty(DEFAULT_SUPERVISOR_LOGIN), 999999, updateData);

        try {
            String title = updatePlayerResponse.getResponse().as(NoSuchUserBody.class).getTitle();
            assertEquals(title, "User does not exist.",
                    "Expected error message");
        } catch (RuntimeException e) {
            logger.error("Could not parse NoSuchUserBody from response", updatePlayerResponse.toString());
            fail("Response body was not JSON or empty", e);
        }
    }

    @Test(description = "Update player with non-existent editor")
    public void testUpdatePlayerWithNonExistentEditor() {
        Player player = TestDataGenerator.generateValidPlayer();
        PlayerResponse actualPlayer = createAndVerifyPlayer(player, getProperty(DEFAULT_SUPERVISOR_LOGIN));

        ResponseWrapper<PlayerResponse> getPlayerResponse = apiClient.getPlayer(actualPlayer.getPlayerId());
        getPlayerResponse.expectingStatusCode(200);

        Player updateData = new Player();
        updateData.setAge(25);
        ResponseWrapper<PlayerResponse> updatedPlayerResponse =
                apiClient.updatePlayer("nonexistent_editor", actualPlayer.getPlayerId(), updateData);
        updatedPlayerResponse.expectingStatusCode(403);
    }

    @Test(description = "Delete player with supervisor")
    public void testDeletePlayerWithSupervisor() {
        Player player = TestDataGenerator.generateValidPlayer();
        PlayerResponse actualPlayer = createAndVerifyPlayer(player, getProperty(DEFAULT_SUPERVISOR_LOGIN));

        Response response = apiClient.deletePlayer(getProperty(DEFAULT_SUPERVISOR_LOGIN), actualPlayer.getPlayerId());
        assertEquals(response.getStatusCode(), 204, "Expected status code 204");

        ResponseWrapper<PlayerResponse> getPlayerResponse = apiClient.getPlayer(actualPlayer.getPlayerId());
        getPlayerResponse.expectingStatusCode(404);
    }

    @Test(description = "Delete player with admin")
    public void testDeletePlayerWithAdmin() {
        Player player = TestDataGenerator.generateValidPlayer();
        PlayerResponse actualPlayer = createAndVerifyPlayer(player, getProperty(DEFAULT_SUPERVISOR_LOGIN));

        Response response = apiClient.deletePlayer(getProperty(DEFAULT_ADMIN_LOGIN), actualPlayer.getPlayerId());
        assertEquals(response.getStatusCode(), 204, "Expected status code 204");

        ResponseWrapper<PlayerResponse> getPlayerResponse = apiClient.getPlayer(actualPlayer.getPlayerId());
        getPlayerResponse.expectingStatusCode(404);
    }


    @Test(description = "Delete non-existent player")
    public void testDeleteNonExistentPlayer() {
        Response response = apiClient.deletePlayer(getProperty(DEFAULT_SUPERVISOR_LOGIN), 999999);
        assertEquals(response.getStatusCode(), 403);
    }

    @Test(description = "Delete player with non-existent editor")
    public void testDeletePlayerWithNonExistentEditor() {
        Player player = TestDataGenerator.generateValidPlayer();
        PlayerResponse actualPlayer = createAndVerifyPlayer(player, getProperty(DEFAULT_SUPERVISOR_LOGIN));
        Response response = apiClient.deletePlayer("nonexistent_editor", actualPlayer.getPlayerId());
        createdPlayerIds.add(actualPlayer.getPlayerId());
        assertEquals(response.getStatusCode(), 403);
    }


    @Test(description = "Test duplicate login constraint")
    public void testDuplicateLoginConstraint() {
        Player playerOne = TestDataGenerator.generateValidPlayer();
//        ResponseWrapper<PlayerResponse> firstPlayer = apiClient
//                .createPlayer(getProperty(DEFAULT_SUPERVISOR_LOGIN), playerOne);
//        firstPlayer.expectingStatusCode(200);
//        Integer playerOneId = firstPlayer.readEntity().getPlayerId();
//        assertNotNull(playerOneId, "PlayerId should be not null");
//        createdPlayerIds.add(playerOneId);

        PlayerResponse firstPlayer = createAndVerifyPlayer(playerOne, getProperty(DEFAULT_SUPERVISOR_LOGIN));

        Player player2 = TestDataGenerator.generatePlayerWithDuplicateLogin(playerOne.getLogin());
        ResponseWrapper<PlayerResponse> playerTwo = apiClient.createPlayer(getProperty(DEFAULT_SUPERVISOR_LOGIN), player2);
        if (playerTwo.getResponse().getStatusCode() == 200) {
            createdPlayerIds.add(playerTwo.readEntity().getPlayerId());
        }
        playerTwo.expectingStatusCode(403);
    }

    @Test(description = "Test supervisor cannot be deleted")
    public void testSupervisorCannotBeDeleted() {
        // Try to delete supervisor (assuming supervisor has ID 1 or we can get it from get all)
        Response response = apiClient.deletePlayer(getProperty(DEFAULT_SUPERVISOR_LOGIN), 1);
        assertEquals(response.getStatusCode(), 403);
    }

    @Test(description = "Test user cannot create other users")
    public void testUserCannotCreateOtherUsers() {
        Player userPlayer = TestDataGenerator.generateValidPlayer();
        PlayerResponse createUserResponse = createAndVerifyPlayer(userPlayer, getProperty(DEFAULT_SUPERVISOR_LOGIN));

        ResponseWrapper<PlayerResponse> getPlayerResponse = apiClient.getPlayer(createUserResponse.getPlayerId());
        getPlayerResponse.expectingStatusCode(200);

        Player newPlayer = TestDataGenerator.generateValidPlayer();
        ResponseWrapper<PlayerResponse> player = apiClient.createPlayer(userPlayer.getLogin(), newPlayer);
        player.expectingStatusCode(403);
    }

    private PlayerResponse createAndVerifyPlayer(Player player, String editor) {
        ResponseWrapper<PlayerResponse> response = apiClient.createPlayer(editor, player);
        response.expectingStatusCode(200);
        PlayerResponse body = response.readEntity();
        assertNotNull(body, "Response body should not be null");
        Integer id = body.getPlayerId();
        assertNotNull(id, "PlayerId should be not null");
        createdPlayerIds.add(id);
        return body;
    }

    private void assertPlayerEquals(PlayerResponse actual, Player expected, SoftAssert sa) {
        sa.assertEquals(actual.getAge(), expected.getAge(), "Wrong age");
        sa.assertEquals(actual.getGender(), expected.getGender(), "Wrong gender");
        sa.assertEquals(actual.getLogin(), expected.getLogin(), "Wrong gender");
        sa.assertEquals(actual.getPassword(), expected.getPassword(), "Wrong gender");
        sa.assertEquals(actual.getRole(), expected.getRole(), "Wrong gender");
        sa.assertEquals(actual.getScreenName(), expected.getScreenName(), "Wrong gender");
        sa.assertAll();
    }
}
