# Análisis de Patrones de Diseño — TechCup Fútbol

## Resumen de Patrones Seleccionados

| Patrón         | Tipo           | Problema que resuelve en TechCup                                           |
|----------------|----------------|----------------------------------------------------------------------------|
| Strategy       | Comportamental | Múltiples reglas de validación de equipos que pueden cambiar entre torneos |
| State          | Comportamental | Ciclo de vida del torneo y de los pagos con transiciones controladas       |
| Factory Method | Creacional     | Creación de diferentes tipos de usuarios con reglas de registro distintas  |
| Observer       | Comportamental | Propagación de eventos cuando se registran resultados de partidos          |

---

## 1. Strategy Pattern

### ¿Por qué lo elegimos?

El sistema TechCup requiere validar múltiples reglas de negocio al momento de conformar un equipo. Estas reglas son independientes entre sí, pero todas deben cumplirse para que un equipo sea válido:

- Mínimo de 7 jugadores por equipo.
- Máximo de 12 jugadores por equipo.
- Un jugador no puede pertenecer a dos equipos simultáneamente.
- Más del 50% de los miembros deben ser de los programas de Ingeniería de Sistemas, IA, Ciberseguridad o Estadística.
- Todos los miembros deben pertenecer a los programas permitidos por el reglamento.

Elegimos Strategy porque permite encapsular cada una de estas reglas como un algoritmo independiente e intercambiable, sin acoplarlas al servicio que las ejecuta. Si en un futuro cambia una regla (por ejemplo, que el mínimo suba a 8 jugadores), solo se modifica la estrategia correspondiente sin afectar el resto del sistema.

### ¿Cómo ayuda a resolver el problema del sistema?

Se define una interfaz Ej: EquipoValidationStrategy, con un único método validar(Equipo equipo) que retorna un resultado de validación. Cada regla de negocio se implementa como una clase concreta por ejemplo:

- **MinJugadoresStrategy:** verifica que el equipo tenga al menos 7 jugadores.
- **MaxJugadoresStrategy:** verifica que no exceda los 12 jugadores.
- **JugadorUnicoStrategy:** verifica que ningún jugador esté registrado en otro equipo.
- **ComposicionProgramaStrategy:** verifica que más del 50% pertenezcan a programas válidos.
- **ProgramaPermitidoStrategy:** verifica que todos los miembros pertenezcan a programas autorizados.

El EquipoService recibe una lista inyectada de estrategias (List de EquipoValidationStrategy) y al validar un equipo itera sobre todas ellas. Si alguna falla, retorna el error específico al usuario. Esto cumple con el Principio Abierto/Cerrado (OCP): se pueden agregar nuevas reglas sin modificar el servicio existente.


---

## 2. State Pattern

### ¿Por qué lo elegimos?

TechCup maneja dos entidades con ciclos de vida claramente definidos por estados y transiciones controladas:

- **Torneo:** Borrador --> Activo --> En Progreso --> Finalizado. Un torneo en estado Borrador puede activarse, pero no puede pasar directamente a Finalizado. Un torneo Finalizado no puede volver a ningún estado anterior.
- **Pago:** Pendiente --> En Revisión --> Aprobado o Rechazado. Un pago Aprobado no puede volver a Pendiente.

Sin este patrón, la lógica de transiciones se implementaría con cadenas extensas de if-else o switch-case dentro del servicio, mezclando la lógica de cada estado en un solo lugar. Esto genera código difícil de mantener, propenso a errores y que viola el principio de responsabilidad única. Elegimos State porque encapsula el comportamiento de cada estado en su propia clase, y cada estado sabe exactamente a cuáles otros puede transicionar.

### ¿Cómo ayuda a resolver el problema del sistema?

Se define una interfaz EstadoTorneo con métodos que representan las acciones posibles: iniciar(), activar(), finalizar(). Cada estado concreto implementa esta interfaz:

- **EstadoBorrador:** permite activar() el torneo (transición a Activo), pero lanza excepción si se intenta finalizar() directamente.
- **EstadoActivo:** permite iniciar() el torneo (transición a En Progreso), pero no permite volver a Borrador.
- **EstadoEnProgreso:** permite finalizar() el torneo (transición a Finalizado).
- **EstadoFinalizado:** lanza excepción ante cualquier intento de transición. Es un estado terminal.

La clase Torneo mantiene una referencia a su estado actual (EstadoTorneo estadoActual). Cuando se invoca una acción, el torneo delega al estado actual, y este decide si la transición es válida o no. Si es válida, el propio estado cambia la referencia interna del torneo al nuevo estado.

---

## 3. Factory Method Pattern

### ¿Por qué lo elegimos?

El sistema TechCup permite el registro de diferentes tipos de participantes, cada uno con reglas de registro distintas:

| Tipo de Usuario         | Método de Registro    | Validación de Correo |
|-------------------------|-----------------------|----------------------|
| Estudiante              | Correo institucional  | @escuelaing.edu.co   |
| Graduado                | Correo institucional  | @escuelaing.edu.co   |
| Profesor                | Correo institucional  | @escuelaing.edu.co   |
| Personal Administrativo | Correo institucional  | @escuelaing.edu.co   |
| Familiar                | Correo personal Gmail | @gmail.com           |

Cada tipo de usuario puede tener atributos específicos (por ejemplo, el estudiante tiene semestre, el graduado tiene año de graduación) y validaciones particulares. Sin Factory Method, el controlador o servicio tendría que contener lógica condicional para determinar qué tipo de objeto crear y qué validaciones aplicar, generando acoplamiento y dificultando la extensión.

Elegimos Factory Method porque centraliza la lógica de creación en un punto único, delegando a subclases o implementaciones concretas la decisión de qué tipo de jugador instanciar y con qué validaciones.

### ¿Cómo ayuda a resolver el problema del sistema?

Se define una interfaz, Ej: JugadorFactory con el método crearJugador(RegistroRequest request) que retorna un objeto Jugador configurado según el tipo. Se implementan fábricas concretas:

- **EstudianteFactory:** valida correo @escuelaing.edu.co, requiere campo semestre, crea instancia con tipo ESTUDIANTE.
- **GraduadoFactory:** valida correo institucional, requiere año de graduación, crea instancia con tipo GRADUADO.
- **ProfesorFactory:** valida correo institucional, crea instancia con tipo PROFESOR.
- **FamiliarFactory:** valida correo @gmail.com, no requiere semestre ni programa, crea instancia con tipo FAMILIAR.

El JugadorService utiliza un JugadorFactoryProvider (que internamente es un Map de TipoUsuario a JugadorFactory) para obtener la fábrica correcta según el tipo indicado en el request. El servicio no conoce los detalles de creación de cada tipo; solo invoca factory.crearJugador(request) y obtiene el objeto listo.

**Aplicación en Spring Boot:** Cada fábrica se anota con @Component y se registra en el Map mediante un @PostConstruct en el provider. Agregar un nuevo tipo de usuario (por ejemplo, "Egresado de Maestría") solo requiere crear una nueva implementación de JugadorFactory, sin modificar el servicio ni el controlador.

---

## 4. Observer Pattern

### ¿Por qué lo elegimos?

Cuando un organizador registra el resultado de un partido en TechCup, múltiples componentes del sistema deben reaccionar a ese evento:

- **La tabla de posiciones debe recalcularse** (actualizar PJ, PG, PE, PP, GF, GC, DG y puntos del equipo local y visitante).
- **Las estadísticas de goleadores deben actualizarse** (sumar goles a cada jugador que anotó).
- **Las llaves eliminatorias pueden verse afectadas** (si el resultado determina clasificación o avance en el bracket).
- **El registro de tarjetas puede activar sanciones** (acumulación de amarillas o expulsión por roja).

Sin Observer, el servicio de partidos tendría que conocer y llamar directamente a cada uno de estos módulos, generando un acoplamiento fuerte. Si se agrega un nuevo módulo (por ejemplo, notificaciones push a los capitanes), habría que modificar el servicio de partidos.

Elegimos Observer porque permite que el servicio de partidos publique un evento sin conocer quién lo consume. Cada módulo interesado se suscribe al evento y reacciona de forma independiente, logrando un sistema desacoplado y extensible.

### ¿Cómo ayuda a resolver el problema del sistema?

Se define un evento ResultadoRegistradoEvent que contiene la información del partido: equipoLocal, equipoVisitante, golesLocal, golesVisitante, listaGoleadores, listaTarjetas. El PartidoService, tras registrar el resultado, publica este evento.

Los observers se implementan como listeners independientes:

- **TablaPosicionesListener:** escucha ResultadoRegistradoEvent y recalcula la tabla de posiciones para ambos equipos involucrados.
- **EstadisticasGoleadoresListener:** escucha el mismo evento y actualiza el conteo de goles por jugador en el ranking de goleadores.
- **LlavesEliminatoriasListener:** escucha el evento y, si el partido corresponde a fase eliminatoria, avanza al ganador en el bracket.
- **SancionesListener:** escucha el evento y verifica si algún jugador acumuló tarjetas que ameriten suspensión.

---

## Conclusión

| Patrón         | Principio SOLID que refuerza                                                       | Beneficio práctico                                                                  |
|----------------|------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------|
| Strategy       | Open/Closed — Agregar validaciones sin modificar el servicio                       | Reglas de equipo intercambiables y testeables de forma aislada                      |
| State          | Single Responsibility — Cada estado gestiona su propia lógica                      | Transiciones controladas que previenen estados inválidos en torneos y pagos         |
| Factory Method | Dependency Inversion — El servicio depende de abstracciones, no de tipos concretos | Nuevos tipos de usuario sin modificar lógica existente                              |
| Observer       | Open/Closed + Loose Coupling — Nuevos listeners sin tocar el publicador            | Módulos desacoplados que reaccionan a resultados de partidos de forma independiente |


## Diagramas de Componentes General

![ComponentesGeneral (1).jpg](docs/Images/ComponentesGeneral%20%281%29.jpg)

## Diagrama de Componentes Especifico

![Diagrama de componentes especifico.png](docs/Images/Diagrama%20de%20componentes%20especifico.png)

## Diagrama de clases

![TechUp (1).jpg](docs/Images/TechUp%20%281%29.jpg)
