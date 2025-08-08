package api.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Player model class for API requests.
 * Represents a player entity with all required fields.
 */
public final class Player {
    
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
    public Player() {
    }

    /**
     * Constructor with all fields.
     *
     * @param age the player's age
     * @param gender the player's gender
     * @param login the player's login
     * @param password the player's password
     * @param role the player's role
     * @param screenName the player's screen name
     */
    public Player(final Integer age, final String gender, final String login, 
                 final String password, final String role, final String screenName) {
        this.age = age;
        this.gender = gender;
        this.login = login;
        this.password = password;
        this.role = role;
        this.screenName = screenName;
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
        if (!(o instanceof Player)) return false;
        Player player = (Player) o;
        return Objects.equals(getAge(), player.getAge()) &&
                Objects.equals(getGender(), player.getGender()) &&
                Objects.equals(getLogin(), player.getLogin()) &&
                Objects.equals(getPassword(), player.getPassword()) &&
                Objects.equals(getRole(), player.getRole()) &&
                Objects.equals(getScreenName(), player.getScreenName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAge(), getGender(), getLogin(), getPassword(), getRole(), getScreenName());
    }

    @Override
    public String toString() {
        return "Player{" +
                "age=" + age +
                ", gender='" + gender + '\'' +
                ", login='" + login + '\'' +
                ", password='[HIDDEN]'" + // Hide password in logs
                ", role='" + role + '\'' +
                ", screenName='" + screenName + '\'' +
                '}';
    }
}
