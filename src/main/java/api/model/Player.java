package api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Player {
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

    // Default constructor
    public Player() {}

    // Constructor with all fields
    public Player(Integer playerId, Integer age, String gender, String login, String password, String role, String screenName) {
        this.playerId = playerId;
        this.age = age;
        this.gender = gender;
        this.login = login;
        this.password = password;
        this.role = role;
        this.screenName = screenName;
    }

    // Constructor for create player (without playerId)
    public Player(Integer age, String gender, String login, String password, String role, String screenName) {
        this.age = age;
        this.gender = gender;
        this.login = login;
        this.password = password;
        this.role = role;
        this.screenName = screenName;
    }

    // Getters and Setters
    public Integer getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Integer playerId) {
        this.playerId = playerId;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player)) return false;
        Player player = (Player) o;
        return Objects.equals(getPlayerId(), player.getPlayerId()) &&
                Objects.equals(getAge(), player.getAge()) &&
                Objects.equals(getGender(), player.getGender()) &&
                Objects.equals(getLogin(), player.getLogin()) &&
                Objects.equals(getPassword(), player.getPassword()) &&
                Objects.equals(getRole(), player.getRole()) &&
                Objects.equals(getScreenName(), player.getScreenName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPlayerId(), getAge(), getGender(), getLogin(), getPassword(), getRole(), getScreenName());
    }

    @Override
    public String toString() {
        return "Player{" +
                "playerId=" + playerId +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", login='" + login + '\'' +
                ", password='" + (password != null ? "***" : null) + '\'' +
                ", role='" + role + '\'' +
                ", screenName='" + screenName + '\'' +
                '}';
    }
}
