package api.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Player short response model for listing players.
 * Contains basic player information without sensitive data.
 */
public final class PlayerShortResponse {
    
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("screenName")
    private String screenName;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("age")
    private Integer age;

    /**
     * Default constructor for JSON deserialization.
     */
    public PlayerShortResponse() {
    }

    /**
     * Constructor with all fields.
     *
     * @param id the player ID
     * @param screenName the player screen name
     * @param gender the player gender
     * @param age the player age
     */
    public PlayerShortResponse(final Integer id, final String screenName, final String gender, final Integer age) {
        this.id = id;
        this.screenName = screenName;
        this.gender = gender;
        this.age = age;
    }

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(final String screenName) {
        this.screenName = screenName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(final String gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(final Integer age) {
        this.age = age;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof PlayerShortResponse)) return false;
        PlayerShortResponse that = (PlayerShortResponse) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getScreenName(), that.getScreenName()) &&
                Objects.equals(getGender(), that.getGender()) &&
                Objects.equals(getAge(), that.getAge());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getScreenName(), getGender(), getAge());
    }

    @Override
    public String toString() {
        return "PlayerShortResponse{" +
                "id=" + id +
                ", screenName='" + screenName + '\'' +
                ", gender='" + gender + '\'' +
                ", age=" + age +
                '}';
    }
}
