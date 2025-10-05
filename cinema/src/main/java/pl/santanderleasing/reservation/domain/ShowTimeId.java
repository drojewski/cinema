package pl.santanderleasing.reservation.domain;

import java.util.Objects;

public record ShowTimeId(String value) {

    public ShowTimeId {
        value = Objects.requireNonNull(value, "ShowTimeId cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("ShowTimeId cannot be blank");
        }
    }

    public static ShowTimeId of(String value) {
        return new ShowTimeId(value);
    }
}