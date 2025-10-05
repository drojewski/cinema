package pl.santanderleasing.reservation.infrastructure;

import pl.santanderleasing.reservation.domain.ShowTimeId;
import pl.santanderleasing.reservation.domain.ShowTimeRepository;

import java.time.LocalDateTime;

public class InMemoryShowTimeRepository implements ShowTimeRepository {

    @Override
    public LocalDateTime findScreeningTimeBy(ShowTimeId showTimeId) {
        return null;
    }
}
