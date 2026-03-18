package com.techcup.techcup_futbol.core.validator;

import com.techcup.techcup_futbol.Controller.dto.CreateTournamentRequest;
import com.techcup.techcup_futbol.core.model.TournamentState;
import com.techcup.techcup_futbol.exception.TournamentException;

import java.time.LocalDate;
import java.util.Arrays;

public class TournamentValidator {

    private TournamentValidator() {}

    public static void validate(CreateTournamentRequest request) {
        if (request == null) {
            throw new TournamentException(TournamentException.REQUEST_NULL);
        }
        validateName(request.name());
        validateDates(request.startDate().toLocalDate(), request.endDate().toLocalDate());
        validateRegistrationFee(request.registrationFee());
        validateMaxTeams(request.maxTeams());
    }


    public static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new TournamentException("name", TournamentException.NAME_EMPTY);
        }
    }

    public static void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new TournamentException("dates", TournamentException.DATES_NULL);
        }
        if (!endDate.isAfter(startDate)) {
            throw new TournamentException("dates",
                    String.format(TournamentException.END_DATE_NOT_AFTER_START,
                            startDate, endDate));
        }
    }

    public static void validateRegistrationFee(double fee) {
        if (fee < 0) {
            throw new TournamentException("registrationFee",
                    String.format(TournamentException.REGISTRATION_FEE_NEGATIVE, fee));
        }
    }

    public static void validateMaxTeams(int maxTeams) {
        if (maxTeams < 2) {
            throw new TournamentException("maxTeams",
                    String.format(TournamentException.MAX_TEAMS_TOO_LOW, maxTeams));
        }
    }

    public static void validateStateTransition(TournamentState current, TournamentState next) {
        boolean isAllowed = switch (current) {
            case DRAFT       -> next == TournamentState.ACTIVE      || next == TournamentState.DELETED;
            case ACTIVE      -> next == TournamentState.IN_PROGRESS || next == TournamentState.DELETED;
            case IN_PROGRESS -> next == TournamentState.COMPLETED;
            case DELETED, COMPLETED -> false;
        };

        if (!isAllowed) {
            throw new TournamentException("state",
                    String.format(TournamentException.INVALID_STATE_TRANSITION, current, next));
        }
    }
}