package com.techcup.techcup_futbol.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("TechCup Fútbol API")
                        .version("1.0.0")
                        .description("""
                                ## Sistema de Gestión de Torneos de Fútbol — TechCup

                                API REST desarrollada como proyecto académico en la \
                                **Escuela Colombiana de Ingeniería Julio Garavito**.

                                Permite gestionar torneos interfacultades: registro de jugadores \
                                y equipos, inscripciones, pagos, alineaciones, partidos, \
                                llaves eliminatorias y tabla de posiciones.

                                > Proyecto académico — Ingeniería de Sistemas · 2026
                                """)
                        .contact(new Contact()
                                .name("Escuela Colombiana de Ingeniería Julio Garavito")
                                .url("https://www.escuelaing.edu.co")
                                .email("sistemas@escuelaing.edu.co"))
                        .license(new License()
                                .name("Uso académico — Escuela Colombiana de Ingeniería")
                                .url("https://www.escuelaing.edu.co")))
                .tags(List.of(
                        new Tag().name("Jugadores")
                                .description("Registro, actualización de perfil y gestión de disponibilidad de jugadores"),
                        new Tag().name("Mercado de Jugadores")
                                .description("Búsqueda y filtrado de jugadores disponibles para ser invitados a equipos"),
                        new Tag().name("Equipos")
                                .description("Creación de equipos, invitación y remoción de jugadores"),
                        new Tag().name("Pagos")
                                .description("Carga de comprobantes de inscripción y validación por parte de los organizadores"),
                        new Tag().name("Partidos")
                                .description("Programación de partidos y registro de resultados"),
                        new Tag().name("Árbitros")
                                .description("Registro de árbitros y asignación a partidos"),
                        new Tag().name("Alineaciones")
                                .description("Registro y consulta de alineaciones (7 titulares) por equipo y partido"),
                        new Tag().name("Llaves Eliminatorias")
                                .description("Generación de brackets y avance de ganadores en fase eliminatoria"),
                        new Tag().name("Tabla de Posiciones")
                                .description("Registro de equipos en torneos y consulta de puntos, goles y partidos jugados")
                ));
    }
}
