package com.techcup.techcup_futbol.util;

import java.util.UUID;

/**
 * Fuente única de generación de identificadores en el sistema.
 *
 * Todos los servicios deben usar este generador en vez de llamar
 * a UUID.randomUUID() directamente, garantizando:
 *  - Un único punto de cambio si en el futuro se cambia la estrategia
 *  - Generación siempre incondicional en el servicio (nunca en el controlador)
 *  - Eliminación del patrón "if (id == null) generar" que acepta IDs del cliente
 */
public final class IdGenerator {

    private IdGenerator() {}

    public static String generateId() {
        return UUID.randomUUID().toString();
    }
}
