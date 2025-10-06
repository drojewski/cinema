package pl.santanderleasing.reservation.infrastructure;

import pl.santanderleasing.reservation.domain.LoyaltyProgramService;

public class InMemoryLoyaltyProgramService implements LoyaltyProgramService {
    @Override
    public boolean isCinemaCardGoldOwner(String userId) {
        return true;
    }
}
