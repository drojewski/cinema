package pl.santanderleasing.reservation.domain;

import java.util.Objects;

public record ReservedSeat(ReservedSeatId id, SeatPosition position) {

    public ReservedSeat(ReservedSeatId id, SeatPosition position) {
        this.id = Objects.requireNonNull(id, "ReservedSeatId cannot be null");
        this.position = Objects.requireNonNull(position, "SeatPosition cannot be null");
    }

    public static ReservedSeat create(SeatPosition position) {
        return new ReservedSeat(ReservedSeatId.generate(), position);
    }

    public static ReservedSeat reconstitute(ReservedSeatId id, SeatPosition position) {
        return new ReservedSeat(id, position);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReservedSeat that = (ReservedSeat) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}