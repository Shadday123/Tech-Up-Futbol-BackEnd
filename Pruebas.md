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