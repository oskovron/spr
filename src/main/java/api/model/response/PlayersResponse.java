package api.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

/**
 * Players response model for listing multiple players.
 * Contains a list of player short responses.
 */
public final class PlayersResponse {

    @JsonProperty("players")
    private List<PlayerShortResponse> players;

    /**
     * Default constructor for JSON deserialization.
     */
    public PlayersResponse() {
    }

    /**
     * Constructor with players list.
     *
     * @param players the list of players
     */
    public PlayersResponse(final List<PlayerShortResponse> players) {
        this.players = players;
    }

    public List<PlayerShortResponse> getPlayers() {
        return players;
    }

    public void setPlayers(final List<PlayerShortResponse> players) {
        this.players = players;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof PlayersResponse)) return false;
        PlayersResponse that = (PlayersResponse) o;
        return Objects.equals(getPlayers(), that.getPlayers());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPlayers());
    }

    @Override
    public String toString() {
        return "PlayersResponse{" +
                "players=" + players +
                '}';
    }
}
