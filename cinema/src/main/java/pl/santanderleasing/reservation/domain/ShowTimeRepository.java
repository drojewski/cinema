package pl.santanderleasing.reservation.domain;

import java.time.LocalDateTime;

public interface ShowTimeRepository {
    LocalDateTime findScreeningTimeBy(ShowTimeId showTimeId);
}