package api.error;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Error response body model.
 * Represents error responses from the API.
 */
public final class ErrorBody {
    
    @JsonProperty("title")
    private String title;

    /**
     * Default constructor for JSON deserialization.
     */
    public ErrorBody() {
    }

    /**
     * Constructor with title.
     *
     * @param title the error title
     */
    public ErrorBody(final String title) {
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
        if (!(o instanceof ErrorBody)) return false;
        ErrorBody errorBody = (ErrorBody) o;
        return Objects.equals(getTitle(), errorBody.getTitle());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTitle());
    }

    @Override
    public String toString() {
        return "ErrorBody{" +
                "title='" + title + '\'' +
                '}';
    }
}
