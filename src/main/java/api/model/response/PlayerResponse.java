package api.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Player response model class for API responses.
 * Represents a player entity with all fields including the generated ID.
 */
public final class PlayerResponse {
    
    @JsonProperty("id")
    private Integer playerId;

    @JsonProperty("age")
    private Integer age;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("login")
    private String login;

    @JsonProperty("password")
    private String password;

    @JsonProperty("role")
    private String role;

    @JsonProperty("screenName")
    private String screenName;

    /**
     * Default constructor for JSON deserialization.
     */
    public PlayerResponse() {
    }

    /**
     * Constructor with all fields.
     *
     * @param playerId the player's ID
     * @param age the player's age
     * @param gender the player's gender
     * @param login the player's login
     * @param password the player's password
     * @param role the player's role
     * @param screenName the player's screen name
     */
    public PlayerResponse(final Integer playerId, final Integer age, final String gender, 
                         final String login, final String password, final String role, final String screenName) {
        this.playerId = playerId;
        this.age = age;
        this.gender = gender;
        this.login = login;
        this.password = password;
        this.role = role;
        this.screenName = screenName;
    }

    public Integer getPlayerId() {
        return playerId;
    }

    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(final Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(final String gender) {
        this.gender = gender;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(final String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(final String role) {
        this.role = role;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(final String screenName) {
        this.screenName = screenName;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof PlayerResponse)) return false;
        PlayerResponse that = (PlayerResponse) o;
        return Objects.equals(getPlayerId(), that.getPlayerId()) &&
                Objects.equals(getAge(), that.getAge()) &&
                Objects.equals(getGender(), that.getGender()) &&
                Objects.equals(getLogin(), that.getLogin()) &&
                Objects.equals(getPassword(), that.getPassword()) &&
                Objects.equals(getRole(), that.getRole()) &&
                Objects.equals(getScreenName(), that.getScreenName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPlayerId(), getAge(), getGender(), getLogin(), getPassword(), getRole(), getScreenName());
    }

    @Override
    public String toString() {
        return "PlayerResponse{" +
                "playerId=" + playerId +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", login='" + login + '\'' +
                ", password='[HIDDEN]'" + // Hide password in logs
                ", role='" + role + '\'' +
                ", screenName='" + screenName + '\'' +
                '}';
    }
}
