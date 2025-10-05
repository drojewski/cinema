package pl.santanderleasing.reservation.domain;

import java.util.Objects;
import java.util.UUID;

public record ReservedSeatId(String value) {

    public ReservedSeatId {
        Objects.requireNonNull(value, "ReservedSeatId cannot be null");
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException("ReservedSeatId cannot be empty");
        }
    }

    public static ReservedSeatId generate() {
        return new ReservedSeatId(UUID.randomUUID().toString());
    }

    public static ReservedSeatId of(String value) {
        return new ReservedSeatId(value);
    }
}