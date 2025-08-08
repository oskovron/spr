package api.error;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * No such user error response body model.
 * Represents error responses when a user is not found.
 */
public final class NoSuchUserBody {
    
    @JsonProperty("title")
    private String title;

    /**
     * Default constructor for JSON deserialization.
     */
    public NoSuchUserBody() {
    }

    /**
     * Constructor with title.
     *
     * @param title the error title
     */
    public NoSuchUserBody(final String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof NoSuchUserBody)) return false;
        NoSuchUserBody that = (NoSuchUserBody) o;
        return Objects.equals(getTitle(), that.getTitle());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTitle());
    }

    @Override
    public String toString() {
        return "NoSuchUserBody{" +
                "title='" + title + '\'' +
                '}';
    }
}
