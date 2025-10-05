package pl.santanderleasing.reservation.domain;

public interface LoyaltyProgramService {
    boolean isEntitledToCancelWithFullRefund(String userId);
}