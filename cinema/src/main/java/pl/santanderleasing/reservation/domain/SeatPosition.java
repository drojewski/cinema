package pl.santanderleasing.reservation.domain;

public record SeatPosition(int row, int number) {

    public SeatPosition {
        if (row <= 0) {
            throw new IllegalArgumentException("Row must be positive number");
        }
        if (number <= 0) {
            throw new IllegalArgumentException("Seat number must be positive number");
        }
    }

    public static SeatPosition of(int row, int number) {
        return new SeatPosition(row, number);
    }
}