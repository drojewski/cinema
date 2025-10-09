package pl.santanderleasing.reservation.domain;

import java.util.Objects;

public record ReservedSeat(SeatPosition position) {

    public ReservedSeat(SeatPosition position) {
        this.position = Objects.requireNonNull(position, "SeatPosition cannot be null");
    }

    public static ReservedSeat create(SeatPosition position) {
        return new ReservedSeat(position);
    }

    public static ReservedSeat reconstitute(SeatPosition position) {
        return new ReservedSeat(position);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ReservedSeat that = (ReservedSeat) o;
        return Objects.equals(position, that.position);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(position);
    }
}