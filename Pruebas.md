# Definición de pruebas 


# Registro de Tornero 
## 1. Happy Path (Escenarios de Éxito)

| ID | Escenario | Entrada (Input)                                     | Resultado Esperado (Output)                                      |
|:---|:---|:----------------------------------------------------|:-----------------------------------------------------------------|
| **HP-01** | Creación de Torneo | JSON con nombre, fechas (inicio < fin) y costo > 0. | Código **201 Created**. Estado inicial: `DRAFT'.                 |
| **HP-02** | Consulta por ID | ID de un torneo existente (ej: `T001`).             | Código **200 OK**. Retorna el JSON con los detalles del torneo.  |
| **HP-03** | Listado General | Petición `GET /api/tournaments`.                    | Código **200 OK**. Lista de todos los torneos en el `DataStore`. |
| **HP-04** | Inicio de Torneo | Petición `PUT` al endpoint `/start.                 | Código **200 OK**. Estado cambia de `DRAFT` a `ACTIVE`.          |

## 2. Error Path (Escenarios de Fallo)

| ID | Escenario | Causa del Error                                           | Resultado Esperado |
|:---|:---|:----------------------------------------------------------|:---|
| **EP-01** | Campos Vacíos | Enviar un `CreateTournamentRequest` sin nombre.           | Código **400 Bad Request**. Error de validación `@NotBlank`. |
| **EP-02** | Fechas Inválidas | Fecha final anterior a la fecha inicial.                  | Excepción `IllegalArgumentException` lanzada por el Validador. |
| **EP-03** | ID Inexistente | Buscar un torneo con un ID que no está en el `DataStore`. | Código **404 Not Found** o mensaje de error descriptivo. |
| **EP-04** | Costo Negativo | Registrar un torneo con `registrationFee` menor a 0.      | Código **400 Bad Request**. Violación de restricción `@Min`. |

## 3. Escenarios Condicionales (Lógica de Negocio)
*Mínimo 2 requeridos*.

### **CS-01: Integridad de la Máquina de Estados**
**Contexto**: Un torneo recién creado está en estado `DRAFT`.
**Condición**: Se intenta llamar directamente al endpoint `/finish`.
* **Resultado**: El sistema debe impedir la transición. Un torneo no puede finalizarse si no ha pasado por el estado `ACTIVE` o `EN PROGRESO`.

### **CS-02: Persistencia Volátil (DataStore)**
* **Contexto**: El sistema utiliza un `DataStore` simulado en memoria.
* **Condición**: Se registra un nuevo torneo mediante un `POST`.
* **Resultado**: Al ejecutar un `GET` general inmediatamente después, el nuevo torneo debe figurar en la lista, validando la persistencia en el Mapa estático.

### **CS-03: Validación de Nombre Único**
* **Condición**: Intentar crear un torneo con un nombre idéntico a uno ya registrado.
* **Resultado**: El sistema debe lanzar un error de negocio para evitar duplicidad de torneos con el mismo nombre en la plataforma.


# Registro de Jugadores 

Este documento detalla los escenarios de prueba para el registro y perfil deportivo de los jugadores en **TechCup Fútbol**, integrando las reglas del documento maestro y los validadores de dominio.

## 1. Happy Path (Escenarios de Éxito)

| ID | Escenario | Entrada (Input) | Resultado Esperado (Output)                                 |
|:---|:---|:---|:------------------------------------------------------------|
| **HP-P01** | Registro Estudiante | Correo `@escuelaing.edu.co`, semestre y datos básicos. | Código **201 Created**. Jugador guardado en el `DataStore`. |
| **HP-P02** | Registro Familiar | Correo `@gmail.com`, parentesco (Relationship) y datos básicos. | Código **201 Created**. Perfil creado exitosamente.         |
| **HP-P03** | Perfil Deportivo | Posición válida (`DEFENDER`) y dorsal (1-99). | Código **200 OK**. Atributos deportivos actualizados.       |
| **HP-P04** | Disponibilidad | Marcarse como "disponible para equipo". | Código **200 OK**. El campo `available` cambia a `true`.    |

## 2. Error Path (Escenarios de Fallo)

| ID         | Escenario | Causa del Error | Resultado Esperado                                      |
|:-----------|:---|:---|:--------------------------------------------------------|
| **EP-P01** | Dominio Inválido | Uso de correo `@outlook.com` o `@yahoo.es`. | Excepción `IllegalArgumentException` (Error 400).       |
| **EP-P02** | ID Duplicado | Registrar un `numberID` que ya existe en el sistema. | El validador lanza "A player with this ID is already registered". |
| **EP-P03** | Dorsal Fuera de Rango | Intentar asignar el dorsal `0` o `101`. | Código **400 Bad Request**. El rango permitido es 1-99. |

## 3. Conditional Scenarios (Lógica de Negocio)

### **CS-P01: Validación de Datos por Tipo de Actor**
**Contexto**: El sistema diferencia entre Estudiantes y Familiares.
**Condición**: Un registro de tipo `StudentPlayer` se envía sin el número de semestre.
**Resultado**: El sistema debe rechazar la petición o asignar un error de validación, ya que el semestre es obligatorio para este perfil.

### **CS-P02: Unicidad de Correo Electrónico**
**Contexto**: El email es el identificador único para la autenticación.
**Condición**: Un usuario intenta registrarse con un email que ya está asociado a otro `numberID`.
**Resultado**: El `PlayerValidator` debe detectar el conflicto en el `DataStore` y bloquear la creación del segundo perfil.

### **CS-P03: Consistencia de Posiciones**
**Condición**: Se intenta enviar una posición que no existe en el `PositionEnum` (ej: "Mago").
**Resultado**: El sistema responde con **400 Bad Request** debido a que el JSON no puede ser mapeado al Enum definido.

# Creación de equipos

Este documento detalla los escenarios de prueba para la creación, administración y validación de equipos en **TechCup Fútbol**, asegurando el cumplimiento de las restricciones de conformación y las reglas de los programas académicos.

## 1. Happy Path (Escenarios de Éxito)

| ID | Escenario                  | Entrada (Input) | Resultado Esperado (Output) |
|:---|:---------------------------|:---|:---|
| **HP-T01** | Creación de Equipo         | Nombre único, URL de escudo, colores y Capitán asignado. | Código **201 Created**. Equipo registrado en estado `PENDING`. |
| **HP-T02** | Invitación Exitosa         | ID de un jugador disponible e ID de un equipo propio. | Código **200 OK**. El jugador recibe la notificación en su perfil. |
| **HP-T03** | Nómina Válida de un equipo | Registro de entre 7 y 12 jugadores con mayoría de Ingeniería. | Código **200 OK**. El equipo es marcado como apto para participar. |
| **HP-T04** | Consulta de Equipo         | Petición `GET /api/teams/{id}` con un ID válido. | Código **200 OK**. Retorna el JSON con la alineación y escudo. |

## 2. Error Path (Escenarios de Fallo)

| ID | Escenario | Causa del Error | Resultado Esperado |
|:---|:---|:---|:---|
| **EP-T01** | Nombre Duplicado | Intentar crear un equipo con un nombre que ya existe. | Código **400 Bad Request**. Mensaje: "Team name already taken". |
| **EP-T02** | Jugador Ocupado | Invitar a un jugador que ya pertenece a otro equipo. | El sistema bloquea la acción por restricción de unicidad. |
| **EP-T03** | Tamaño Inválido | Intentar cerrar un equipo con menos de 7 o más de 12 integrantes. | Error de validación de negocio. Bloqueo de cambio a "Aprobado". |
| **EP-T04** | Minoría de Programa | Equipo con menos del 50% de miembros de Ingeniería (Sistemas/IA/etc). | Código **400 Bad Request**. Incumplimiento de regla de programas. |

## 3. Conditional Scenarios (Lógica de Negocio)

### **CS-T01: Validación de Rol de Capitán**
* **Contexto**: Según el reglamento, solo ciertos actores pueden liderar equipos.
* **Condición**: Un usuario registrado con el rol de `Árbitro` intenta crear un equipo.
* **Resultado**: El sistema debe denegar la operación, ya que el perfil de árbitro no tiene permisos de capitanía.

### **CS-T02: Bloqueo de Cambios en Torneo Activo**
* **Contexto**: No se permiten cambios de equipo una vez que la competición inicia.
* **Condición**: Un jugador intenta aceptar una invitación cuando el torneo ya está en estado `ACTIVE`.
* **Resultado**: El sistema debe rechazar la transacción, manteniendo la nómina fija hasta el final del torneo.

### **CS-T03: Asignación de Recurso Visual por Defecto**
* **Condición**: El capitán crea el equipo pero no proporciona una URL para el escudo.
* **Resultado**: El sistema debe asignar automáticamente la imagen `default-shield.png` para asegurar la integridad visual en la tabla de posiciones.