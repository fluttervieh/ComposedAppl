package at.fhv.se.collabnotes.rest.data;

import java.util.Optional;

import io.swagger.v3.oas.annotations.media.Schema;

public class UpdateResponse {
    @Schema(required = true)
    private boolean ok;
    @Schema(required = true)
    private Optional<String> error;

    private static final UpdateResponse OK_INSTANCE = new UpdateResponse();

    public static UpdateResponse ofError(String error) {
        return new UpdateResponse(error);
    }

    public static UpdateResponse ofOk() {
        return OK_INSTANCE;
    }

    public boolean isOk() {
        return ok;
    }

    public Optional<String> getError() {
        return error;
    }

    private UpdateResponse() {
        this.ok = true;
        this.error = Optional.empty();
    }

    private UpdateResponse(String error) {
        this.ok = false;
        this.error = Optional.of(error);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((error == null) ? 0 : error.hashCode());
        result = prime * result + (ok ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UpdateResponse other = (UpdateResponse) obj;
        if (error == null) {
            if (other.error != null)
                return false;
        } else if (!error.equals(other.error))
            return false;
        if (ok != other.ok)
            return false;
        return true;
    }
}
