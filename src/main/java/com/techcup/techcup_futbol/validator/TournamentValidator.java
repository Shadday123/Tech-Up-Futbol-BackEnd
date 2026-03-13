package com.techcup.techcup_futbol.validator;

import com.techcup.techcup_futbol.Controller.dto.CreateTournamentRequest;
import com.techcup.techcup_futbol.model.TournamentState;

public class TournamentValidator {

    /**
     * Valida la lógica de negocio al momento de crear un torneo.
     * @param request Datos provenientes del DTO
     */
    public static void validate(CreateTournamentRequest request) {

        // campos no nulos
        if (request == null) {
            throw new IllegalArgumentException("La solicitud del torneo no puede ser nula.");
        }

        //  fecha_fin > fecha_inicio
        if (request.endDate().isBefore(request.startDate())) {
            throw new IllegalArgumentException("Inconsistencia en fechas: La fecha de finalización debe ser posterior a la de inicio.");
        }

        // La fecha de inicio no puede ser igual a la fecha de fin
        if (request.endDate().isEqual(request.startDate())) {
            throw new IllegalArgumentException("El torneo debe tener una duración mínima de al menos un día.");
        }

    }

    /**
     * Valida si la transición de un estado a otro es permitida (State Pattern).
     */
    public static void validateStateTransition(TournamentState current, TournamentState next) {
        boolean isAllowed = switch (current) {
            // De borrador solo puede pasar a activo o eliminado
            case DRAFT       -> next == TournamentState.ACTIVE || next == TournamentState.DELETED;

            // De Activo puede pasar a en progreso o eliminado
            case ACTIVE      -> next == TournamentState.IN_PROGRESS || next == TournamentState.DELETED;

            // De En Progreso solo puede ser completado ya no puede ser eliminado
            case IN_PROGRESS -> next == TournamentState.COMPLETED;

            // estos estados no permiten mas cambios
            case DELETED, COMPLETED -> false;
        };

        if (!isAllowed) {
            throw new IllegalStateException("Error de flujo: No se puede pasar de " + current + " a " + next);
        }
    }
}
