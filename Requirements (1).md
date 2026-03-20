# Requerimientos del Proyecto: TECHCUP FÚTBOL

## 1. Listado de Requerimientos

### Requerimientos Funcionales (RF)
* RF01: Registro de Torneo
* RF02: Registro de Jugadores
* RF03: Creación y gestión de equipos
* RF04: Búsqueda de Jugadores
* RF05: Inscripción y pagos
* RF06: Configurar Torneo
* RF07: Alineaciones
* RF08: Registro de Partidos
* RF09: Consulta de partidos
* RF10: Tabla de Posiciones
* RF11: Llaves Eliminatorias
* RF12: Estadísticas
* RF13: Autenticación y Login
* RF14: Control de roles y permisos

### Requerimientos No Funcionales (RNF)
* RNF01: Registro de acciones (auditoría)
* RNF02: Backend en Spring Boot por capas (controladores, adaptadores, lógica y datos)
* RNF03: Manejo de API REST con Spring Boot
* RNF04: Frontend en React con TypeScript (aplicación web)
* RNF05: Base de datos PostgreSQL
* RNF06: Rendimiento — El sistema debe responder a las consultas en menos de 3 segundos bajo condiciones normales de uso
* RNF07: Usabilidad — La plataforma debe ser intuitiva para estudiantes sin necesidad de capacitación previa
* RNF08: Mantenibilidad — El sistema debe implementar patrones de diseño que faciliten la extensión y modificación del código

---

## 2. Detalle de Requerimientos Funcionales

### RF01: Registro de Torneo

| Campo                     | Detalle |
|:--------------------------|:--------|
| **Código**                | RF01 |
| **Nombre**                | Registro de Torneo |
| **Descripción**           | El sistema debe permitir al organizador crear un nuevo torneo proporcionando la información básica requerida (fechas, cantidad de equipos, costo). El torneo se crea inicialmente en estado **Borrador** y puede gestionarse a través de un ciclo de vida con transiciones controladas: **Borrador → Activo → En Progreso → Finalizado**. Además, el organizador podrá consultar los torneos existentes filtrando por estado. |
| **Cómo se ejecutará**     | A través de un formulario administrativo donde se definen los parámetros globales del evento y un panel de gestión que permite cambiar el estado del torneo según las transiciones permitidas. |
| **Actor principal**       | Organizador |
| **Precondiciones**        | 1) El usuario debe estar autenticado con rol de Organizador. <br> 2) No debe existir otro torneo activo o en progreso para el mismo periodo de fechas. |
| **Reglas de Negocio**     | 1) El torneo se crea siempre en estado **Borrador**. <br> 2) Las transiciones de estado permitidas son: Borrador → Activo, Activo → En Progreso, En Progreso → Finalizado. <br> 3) No se permite ninguna transición inversa ni saltar estados (por ejemplo, de Borrador a Finalizado). <br> 4) La fecha final debe ser posterior a la fecha inicial. <br> 5) La cantidad de equipos debe ser un número par mayor o igual a 4. <br> 6) Un torneo Finalizado no admite ninguna modificación. |
| **Anexos**                | **Prototipos:** Mockup de Gestión Administrativa. <br> **Abreviaturas:** N/A |
| **Historial de revisión** | **Elaborado por:** Vanessa Torres <br> **Aprobado por:** David Cajamarca <br> **Fecha:** 03/03/2026 <br> **Descripción y Justificación de cambios:** Se expandieron las reglas de negocio para incluir transiciones de estado válidas y validaciones de fechas según feedback del profesor. |

**DATOS DE ENTRADA:**

| Nombre | Descripción | Tipo de campo | Reglas / Aplicación | Obligatorio |
|:-------|:------------|:--------------|:--------------------|:------------|
| Fecha inicial | Fecha de inicio del torneo | Fecha (date) | Debe ser igual o posterior a la fecha actual | Sí |
| Fecha final | Fecha de finalización del torneo | Fecha (date) | Debe ser estrictamente posterior a la fecha inicial | Sí |
| Cantidad de equipos | Número máximo de equipos participantes | Numérico (entero) | Debe ser un número par ≥ 4 | Sí |
| Costo por equipo | Valor de inscripción por equipo | Moneda (decimal) | Debe ser ≥ 0 | Sí |

**DATOS DE SALIDA:**

| Nombre | Descripción | Tipo de campo | Reglas / Aplicación | Obligatorio |
|:-------|:------------|:--------------|:--------------------|:------------|
| ID del torneo | Identificador único del torneo creado | Texto (UUID) | Generado automáticamente por el sistema | Sí |
| Estado | Estado inicial del torneo | Texto (enum) | Siempre se retorna como "Borrador" al crear | Sí |
| Mensaje de confirmación | Notificación de creación exitosa | Texto | Se muestra al organizador tras el guardado | Sí |

**FLUJO BÁSICO:**

| Paso | Actor | Descripción | Excepciones |
|:-----|:------|:------------|:------------|
| 1 | Organizador | Accede al módulo de torneos y selecciona "Crear torneo" | — |
| 2 | Organizador | Ingresa fecha inicial, fecha final, cantidad de equipos y costo por equipo | — |
| 3 | Sistema | Valida que la fecha final sea posterior a la fecha inicial | E1: Fecha final ≤ fecha inicial |
| 4 | Sistema | Valida que la cantidad de equipos sea un número par ≥ 4 | E2: Cantidad inválida |
| 5 | Sistema | Valida que no exista otro torneo activo/en progreso en el mismo periodo | E3: Torneo solapado |
| 6 | Sistema | Crea el torneo en estado "Borrador" y retorna confirmación con ID | — |

**FLUJO ALTERNO:**

| Paso | Actor | Descripción | Excepciones |
|:-----|:------|:------------|:------------|
| E1 | Sistema | Muestra mensaje: "La fecha final debe ser posterior a la fecha inicial" y no permite el guardado | Regresa al paso 2 |
| E2 | Sistema | Muestra mensaje: "La cantidad de equipos debe ser un número par mayor o igual a 4" | Regresa al paso 2 |
| E3 | Sistema | Muestra mensaje: "Ya existe un torneo activo o en progreso para el periodo indicado" | Regresa al paso 2 |
| A1 | Organizador | Si desea activar el torneo, selecciona "Activar" desde el panel de gestión | E4: Torneo sin configuración completa |
| A2 | Organizador | Si desea iniciar el torneo activo, selecciona "Iniciar torneo" | E5: No hay equipos inscritos aprobados |
| A3 | Organizador | Si desea finalizar el torneo, selecciona "Finalizar torneo" | E6: Hay partidos sin resultado registrado |
| E4 | Sistema | Muestra mensaje: "Complete la configuración del torneo antes de activarlo (RF06)" | Regresa al panel de gestión |
| E5 | Sistema | Muestra mensaje: "No se puede iniciar un torneo sin equipos inscritos" | Regresa al panel de gestión |
| E6 | Sistema | Muestra mensaje: "Existen partidos pendientes de resultado" | Regresa al panel de gestión |

**Notas y comentarios:** Se aplica el patrón de diseño **State** para gestionar las transiciones de estado del torneo.

---

### RF02: Registro de Jugadores

| Campo                     | Detalle |
|:--------------------------|:--------|
| **Código**                | RF02 |
| **Nombre**                | Registro de Jugadores |
| **Descripción**           | El sistema debe permitir a los participantes registrarse en la plataforma utilizando su correo institucional (estudiantes, graduados, profesores, personal administrativo) o correo personal de Gmail (familiares). Una vez registrado, el participante podrá crear y completar su perfil deportivo indicando posiciones de juego, número dorsal y foto. Además, podrá marcarse como disponible para que los capitanes lo contacten, y podrá recibir, aceptar o rechazar invitaciones para unirse a un equipo. |
| **Cómo se ejecutará**     | Mediante un módulo de registro inicial (con validación de correo según tipo de usuario) y un módulo de edición de perfil deportivo accesible desde el panel del jugador. |
| **Actor principal**       | Estudiante, Graduado, Profesor, Personal Administrativo, Familiar |
| **Precondiciones**        | 1) El participante debe contar con un correo electrónico válido del dominio correspondiente a su tipo de usuario. <br> 2) El correo electrónico no debe estar previamente registrado en el sistema. |
| **Reglas de Negocio**     | 1) Estudiantes, graduados, profesores y personal administrativo deben registrarse con correo institucional (@escuelaing.edu.co). <br> 2) Los familiares deben registrarse exclusivamente con correo Gmail (@gmail.com). <br> 3) El número dorsal debe ser único dentro de un mismo equipo (la unicidad se valida al unirse a un equipo). <br> 4) Solo los jugadores con estado "Disponible" aparecerán en las búsquedas de los capitanes. <br> 5) Un jugador no puede aceptar una invitación si ya pertenece a un equipo. |
| **Anexos**                | **Prototipos:** Mockup de Registro y Perfil Deportivo. <br> **Abreviaturas:** ECI (Escuela Colombiana de Ingeniería) |
| **Historial de revisión** | **Elaborado por:** Vanessa Torres <br> **Aprobado por:** David Cajamarca <br> **Fecha:** 03/03/2026 <br> **Descripción y Justificación de cambios:** Se separó claramente el registro de la gestión de perfil. Se agregaron validaciones de correo duplicado y dorsal único. Se corrigió errata "prodrá" por "podrá". |

**DATOS DE ENTRADA:**

| Nombre | Descripción | Tipo de campo | Reglas / Aplicación | Obligatorio |
|:-------|:------------|:--------------|:--------------------|:------------|
| Correo electrónico | Correo del participante para registro | Email (texto) | Debe ser @escuelaing.edu.co (institucional) o @gmail.com (familiares) | Sí |
| Contraseña | Clave de acceso del usuario | Texto (password) | Mínimo 8 caracteres, al menos una mayúscula y un número | Sí |
| Nombre completo | Nombre y apellidos del participante | Texto | Mínimo 3 caracteres | Sí |
| Tipo de usuario | Clasificación del participante | Selección (enum) | Estudiante, Graduado, Profesor, Personal Administrativo, Familiar | Sí |
| Posiciones de juego | Posiciones preferidas del jugador | Selección múltiple | Portero, Defensa, Volante, Delantero | Sí |
| Número dorsal | Número de camiseta preferido | Numérico (entero) | Rango de 1 a 99 | Sí |
| Foto de perfil | Imagen del jugador | Imagen (archivo) | Formatos: JPG, PNG. Tamaño máximo: 2MB | No |
| Disponibilidad | Indica si el jugador está buscando equipo | Booleano | Por defecto: false | No |
| Semestre | Semestre actual del estudiante | Numérico (entero) | Solo aplica si tipo de usuario = Estudiante. Rango 1-10 | Condicional |

**DATOS DE SALIDA:**

| Nombre | Descripción | Tipo de campo | Reglas / Aplicación | Obligatorio |
|:-------|:------------|:--------------|:--------------------|:------------|
| ID del jugador | Identificador único del jugador | Texto (UUID) | Generado automáticamente | Sí |
| Perfil deportivo | Datos del perfil creado | Objeto JSON | Incluye posiciones, dorsal, foto y disponibilidad | Sí |
| Mensaje de confirmación | Notificación de registro exitoso | Texto | Diferente según si fue solo registro o también perfil completo | Sí |

**FLUJO BÁSICO:**

| Paso | Actor | Descripción | Excepciones |
|:-----|:------|:------------|:------------|
| 1 | Usuario | Accede al módulo de registro y selecciona su tipo de usuario | — |
| 2 | Usuario | Ingresa correo electrónico, contraseña y nombre completo | — |
| 3 | Sistema | Valida que el dominio del correo corresponda al tipo de usuario seleccionado | E1: Dominio de correo no válido para el tipo |
| 4 | Sistema | Verifica que el correo no esté previamente registrado | E2: Correo ya registrado |
| 5 | Sistema | Crea la cuenta del usuario y redirige al módulo de perfil deportivo | — |
| 6 | Usuario | Completa su perfil deportivo: posiciones, dorsal, foto (opcional) | — |
| 7 | Sistema | Valida los datos del perfil (dorsal en rango, posiciones válidas) | E3: Datos de perfil inválidos |
| 8 | Sistema | Guarda el perfil y muestra confirmación | — |

**FLUJO ALTERNO:**

| Paso | Actor | Descripción | Excepciones |
|:-----|:------|:------------|:------------|
| E1 | Sistema | Muestra mensaje: "El dominio del correo no corresponde al tipo de usuario seleccionado" | Regresa al paso 2 |
| E2 | Sistema | Muestra mensaje: "Este correo ya se encuentra registrado en el sistema" | Regresa al paso 2 |
| E3 | Sistema | Muestra mensaje detallado del campo inválido (dorsal fuera de rango, posición no válida) | Regresa al paso 6 |
| A1 | Usuario | Si desea marcarse como disponible, activa el toggle desde su perfil | — |
| A2 | Sistema | Actualiza estado de disponibilidad y el jugador aparece en búsquedas de capitanes | — |
| A3 | Usuario | Si recibe invitación de un equipo, puede aceptar o rechazar desde su panel de notificaciones | E4: Ya pertenece a un equipo |
| E4 | Sistema | Muestra mensaje: "No puedes aceptar la invitación porque ya perteneces a un equipo" | La invitación se descarta |

**Notas y comentarios:** Se aplica el patrón **Factory Method** para la creación de usuarios según tipo, con validaciones de correo específicas por tipo.

---

### RF03: Creación y gestión de equipos

| Campo                     | Detalle |
|:--------------------------|:--------|
| **Código**                | RF03 |
| **Nombre**                | Creación y gestión de equipos |
| **Descripción**           | El sistema debe permitir a un jugador registrado crear un equipo, asumiendo automáticamente el rol de capitán. El capitán podrá asignar nombre, escudo y colores de uniforme al equipo, e invitar jugadores disponibles. El sistema validará todas las reglas de composición del equipo de forma independiente antes de permitir la inscripción al torneo. |
| **Cómo se ejecutará**     | A través de un formulario de creación de equipo y un panel de gestión de nómina exclusivo para el capitán. |
| **Actor principal**       | Capitán |
| **Precondiciones**        | 1) El usuario debe estar registrado con perfil deportivo completo. <br> 2) El usuario no debe pertenecer a ningún equipo actualmente. |
| **Reglas de Negocio**     | 1) Un equipo debe tener un **mínimo de 7** y un **máximo de 12** jugadores. <br> 2) **Más de la mitad** de los miembros deben pertenecer a Ingeniería de Sistemas, IA, Ciberseguridad o Estadística. <br> 3) Un jugador **no puede pertenecer a dos equipos** simultáneamente. <br> 4) Todos los miembros deben pertenecer a los programas permitidos (Ing. Sistemas, IA, Ciberseguridad, Estadística, Maestría en Gestión de Información, Informática o Ciencia de Datos). <br> 5) El nombre del equipo debe ser único dentro del torneo. <br> 6) No se permiten cambios de jugadores entre equipos una vez iniciado el torneo; los 12 jugadores iniciales deben terminar el torneo. |
| **Anexos**                | **Prototipos:** Mockup de Gestión de Equipos y Nómina. <br> **Abreviaturas:** N/A |
| **Historial de revisión** | **Elaborado por:** Vanessa Torres <br> **Aprobado por:** David Cajamarca <br> **Fecha:** 03/03/2026 <br> **Descripción y Justificación de cambios:** Se agregaron las reglas de programas permitidos completas (incluyendo maestrías) y la restricción de nómina fija. |

**DATOS DE ENTRADA:**

| Nombre | Descripción | Tipo de campo | Reglas / Aplicación | Obligatorio |
|:-------|:------------|:--------------|:--------------------|:------------|
| Nombre del equipo | Nombre identificador del equipo | Texto | Máximo 50 caracteres. Debe ser único dentro del torneo | Sí |
| Escudo del equipo | Imagen representativa del equipo | Imagen (archivo) | Formatos: JPG, PNG. Tamaño máximo: 2MB | No |
| Colores del uniforme | Colores principales y secundarios | Texto | Mínimo un color primario | Sí |
| Jugadores invitados | Lista de jugadores a invitar al equipo | Lista de IDs | Solo jugadores con estado "Disponible" y sin equipo | Sí |

**DATOS DE SALIDA:**

| Nombre | Descripción | Tipo de campo | Reglas / Aplicación | Obligatorio |
|:-------|:------------|:--------------|:--------------------|:------------|
| ID del equipo | Identificador único del equipo creado | Texto (UUID) | Generado automáticamente | Sí |
| Estado del equipo | Estado actual del equipo | Texto (enum) | Inicialmente: "En formación" | Sí |
| Invitaciones enviadas | Cantidad de invitaciones enviadas exitosamente | Numérico | Solo cuenta invitaciones a jugadores válidos | Sí |
| Mensaje de confirmación | Notificación de creación exitosa | Texto | Incluye resumen de invitaciones enviadas | Sí |

**FLUJO BÁSICO:**

| Paso | Actor | Descripción | Excepciones |
|:-----|:------|:------------|:------------|
| 1 | Capitán | Accede al módulo de equipos y selecciona "Crear equipo" | — |
| 2 | Capitán | Ingresa nombre del equipo, colores de uniforme y carga escudo (opcional) | — |
| 3 | Sistema | Valida que el nombre del equipo no esté duplicado en el torneo | E1: Nombre duplicado |
| 4 | Sistema | Crea el equipo en estado "En formación" y asigna al creador como capitán | — |
| 5 | Capitán | Busca jugadores disponibles y envía invitaciones | — |
| 6 | Sistema | Verifica que cada jugador invitado esté disponible y no pertenezca a otro equipo | E2: Jugador no disponible o ya en equipo |
| 7 | Sistema | Envía las invitaciones y notifica al capitán del resultado | — |

**FLUJO ALTERNO:**

| Paso | Actor | Descripción | Excepciones |
|:-----|:------|:------------|:------------|
| E1 | Sistema | Muestra mensaje: "Ya existe un equipo con ese nombre en este torneo" | Regresa al paso 2 |
| E2 | Sistema | Muestra mensaje indicando cuáles jugadores no pudieron ser invitados y la razón | Continúa con las invitaciones válidas |
| A1 | Capitán | Si desea inscribir el equipo al torneo, selecciona "Inscribir equipo" | E3, E4, E5, E6 |
| E3 | Sistema | Valida mínimo 7 jugadores. Si no cumple: "El equipo necesita al menos 7 jugadores para inscribirse" | Regresa al panel de gestión |
| E4 | Sistema | Valida máximo 12 jugadores. Si excede: "El equipo no puede tener más de 12 jugadores" | Regresa al panel de gestión |
| E5 | Sistema | Valida composición >50% de programas válidos. Si no cumple: "Más de la mitad de los jugadores deben pertenecer a los programas de Ingeniería de Sistemas, IA, Ciberseguridad o Estadística" | Regresa al panel de gestión |
| E6 | Sistema | Valida pertenencia a programas permitidos. Si no cumple: "Todos los miembros deben pertenecer a los programas autorizados" | Regresa al panel de gestión |

**Notas y comentarios:** Se aplica el patrón **Strategy** para validar cada regla de composición de equipo de forma independiente e intercambiable.

---

### RF04: Búsqueda de Jugadores

| Campo                     | Detalle |
|:--------------------------|:--------|
| **Código**                | RF04 |
| **Nombre**                | Búsqueda de Jugadores |
| **Descripción**           | El sistema debe permitir a los capitanes localizar jugadores disponibles mediante diversos filtros de búsqueda combinables para evaluar e invitar candidatos a sus equipos. Los resultados mostrarán un perfil resumido de cada jugador. |
| **Cómo se ejecutará**     | A través de un buscador con filtros dinámicos en el panel de gestión del capitán, con resultados paginados. |
| **Actor principal**       | Capitán |
| **Precondiciones**        | 1) El capitán debe estar autenticado. <br> 2) Deben existir jugadores registrados con perfiles deportivos activos en el sistema. |
| **Reglas de Negocio**     | 1) Solo se muestran jugadores con estado "Disponible" y que no pertenezcan a ningún equipo. <br> 2) El filtro de semestre solo aplica para usuarios con perfil de Estudiante. <br> 3) La búsqueda por identificación debe ser exacta para proteger la privacidad. <br> 4) Los demás filtros permiten coincidencia parcial o por rango. |
| **Anexos**                | **Prototipos:** Mockup de Buscador de Jugadores y Filtros. <br> **Abreviaturas:** N/A |
| **Historial de revisión** | **Elaborado por:** Vanessa Torres <br> **Aprobado por:** David Cajamarca <br> **Fecha:** 03/03/2026 <br> **Descripción y Justificación de cambios:** Se detalló el comportamiento de cada filtro y la paginación de resultados. |

**DATOS DE ENTRADA:**

| Nombre | Descripción | Tipo de campo | Reglas / Aplicación | Obligatorio |
|:-------|:------------|:--------------|:--------------------|:------------|
| Posición | Posición de juego del jugador | Selección | Portero, Defensa, Volante, Delantero. Permite selección múltiple | No |
| Semestre | Semestre actual del estudiante | Numérico (entero) | Solo aplica si el jugador es tipo Estudiante. Rango 1-10 | No |
| Edad | Edad del jugador | Numérico (entero) | Permite rango (edad mínima - edad máxima) | No |
| Género | Género del jugador | Selección | Masculino, Femenino, Otro | No |
| Nombre | Nombre del jugador | Texto | Búsqueda parcial (contiene). Mínimo 2 caracteres | No |
| Identificación | Documento de identidad | Numérico | Búsqueda exacta para proteger privacidad | No |

**DATOS DE SALIDA:**

| Nombre | Descripción | Tipo de campo | Reglas / Aplicación | Obligatorio |
|:-------|:------------|:--------------|:--------------------|:------------|
| Lista de jugadores | Resultados de la búsqueda | Lista de objetos | Paginada, máximo 20 por página | Sí |
| Perfil resumido | Datos visibles de cada jugador | Objeto JSON | Nombre, posiciones, dorsal, foto, tipo de usuario | Sí |
| Total de resultados | Cantidad total de coincidencias | Numérico | Para la paginación | Sí |

**FLUJO BÁSICO:**

| Paso | Actor | Descripción | Excepciones |
|:-----|:------|:------------|:------------|
| 1 | Capitán | Accede al módulo de búsqueda de jugadores | — |
| 2 | Capitán | Selecciona o ingresa uno o más criterios de búsqueda (filtros) | — |
| 3 | Sistema | Consulta los jugadores disponibles aplicando los filtros combinados | — |
| 4 | Sistema | Retorna la lista paginada de jugadores con perfil resumido | E1: Sin resultados |
| 5 | Capitán | Selecciona un jugador para ver su perfil completo o enviar invitación | — |

**FLUJO ALTERNO:**

| Paso | Actor | Descripción | Excepciones |
|:-----|:------|:------------|:------------|
| E1 | Sistema | Muestra mensaje: "No se encontraron jugadores con los criterios ingresados" | Regresa al paso 2 |
| A1 | Capitán | Si no ingresa ningún filtro, el sistema muestra todos los jugadores disponibles paginados | — |
| A2 | Capitán | Si selecciona filtro de semestre pero el resultado incluye no-estudiantes, el sistema los excluye automáticamente | — |

**Notas y comentarios:** Se implementa el patrón **Specification** en el backend para combinar filtros dinámicos de forma desacoplada.

---

### RF05: Inscripción y pagos

| Campo                     | Detalle |
|:--------------------------|:--------|
| **Código**                | RF05 |
| **Nombre**                | Inscripción y pagos |
| **Descripción**           | El sistema debe permitir al capitán de cada equipo cargar el comprobante de pago realizado externamente (por Nequi o efectivo) para que el organizador valide la inscripción oficial del equipo al torneo. El pago no se procesa dentro de la plataforma; el sistema solo gestiona el flujo de verificación documental con estados controlados. |
| **Cómo se ejecutará**     | A través de un módulo de carga de archivos para el capitán y una bandeja de validación para el organizador, con un flujo de estados: Pendiente → En Revisión → Aprobado/Rechazado. |
| **Actor principal**       | Capitán / Organizador |
| **Precondiciones**        | 1) El equipo debe estar creado y contar con el mínimo de 7 jugadores. <br> 2) El equipo debe haber pasado todas las validaciones de composición (RF03). <br> 3) El pago debe haberse realizado previamente por Nequi o efectivo al coordinador del evento. |
| **Reglas de Negocio**     | 1) El pago no se realiza dentro de la plataforma; solo se gestiona el comprobante. <br> 2) Solo los equipos con estado de pago "Aprobado" pueden participar en el torneo. <br> 3) El pago es responsabilidad exclusiva del capitán del equipo. <br> 4) Las transiciones de estado del pago son: Pendiente → En Revisión → Aprobado o Rechazado. <br> 5) Un pago rechazado permite al capitán subir un nuevo comprobante, reiniciando el flujo a Pendiente. |
| **Anexos**                | **Prototipos:** Mockup de Módulo de Carga de Pagos y Panel de Validación. <br> **Abreviaturas:** N/A |
| **Historial de revisión** | **Elaborado por:** Vanessa Torres <br> **Aprobado por:** David Cajamarca <br> **Fecha:** 03/03/2026 <br> **Descripción y Justificación de cambios:** Se detalló el flujo de estados del pago y se agregó la posibilidad de resubir comprobante tras rechazo. |

**DATOS DE ENTRADA:**

| Nombre | Descripción | Tipo de campo | Reglas / Aplicación | Obligatorio |
|:-------|:------------|:--------------|:--------------------|:------------|
| Comprobante de pago | Imagen o PDF del comprobante | Archivo (imagen/PDF) | Formatos: JPG, PNG, PDF. Tamaño máximo: 5MB | Sí |
| ID del equipo | Equipo al que corresponde el pago | Texto (UUID) | Debe corresponder al equipo del capitán autenticado | Sí |

**DATOS DE SALIDA:**

| Nombre | Descripción | Tipo de campo | Reglas / Aplicación | Obligatorio |
|:-------|:------------|:--------------|:--------------------|:------------|
| Estado del pago | Estado actual del comprobante | Texto (enum) | Pendiente, En Revisión, Aprobado, Rechazado | Sí |
| Notificación | Mensaje al capitán sobre el estado | Texto | Diferente según transición de estado | Sí |
| Estado del equipo | Estado de inscripción del equipo | Texto (enum) | Cambia a "Inscrito" solo cuando pago = Aprobado | Sí |

**FLUJO BÁSICO:**

| Paso | Actor | Descripción | Excepciones |
|:-----|:------|:------------|:------------|
| 1 | Capitán | Realiza el pago externamente (Nequi o efectivo al coordinador) | — |
| 2 | Capitán | Accede al módulo de inscripción y sube el comprobante | — |
| 3 | Sistema | Valida el formato y tamaño del archivo | E1: Formato o tamaño inválido |
| 4 | Sistema | Registra el comprobante y cambia el estado a "Pendiente" | — |
| 5 | Sistema | Notifica al organizador que hay un nuevo comprobante por revisar | — |
| 6 | Organizador | Accede a la bandeja de validación y revisa el comprobante | — |
| 7 | Organizador | Marca el pago como "Aprobado" | — |
| 8 | Sistema | Actualiza el estado del equipo a "Inscrito" y notifica al capitán | — |

**FLUJO ALTERNO:**

| Paso | Actor | Descripción | Excepciones |
|:-----|:------|:------------|:------------|
| E1 | Sistema | Muestra mensaje: "El archivo debe ser JPG, PNG o PDF y no exceder 5MB" | Regresa al paso 2 |
| A1 | Organizador | Si el comprobante es ilegible o incorrecto, marca el pago como "Rechazado" | — |
| A2 | Sistema | Notifica al capitán: "Su comprobante fue rechazado. Por favor suba un nuevo soporte" | — |
| A3 | Capitán | Sube un nuevo comprobante y el flujo reinicia desde el paso 3 | — |

**Notas y comentarios:** Se aplica el patrón **State** para gestionar las transiciones de estado del pago (análogo al ciclo de vida del torneo).

---

### RF06: Configurar Torneo

| Campo                     | Detalle |
|:--------------------------|:--------|
| **Código**                | RF06 |
| **Nombre**                | Configurar Torneo |
| **Descripción**           | El sistema debe permitir al organizador, una vez creado el torneo, definir los parámetros operativos, normativos y logísticos necesarios para su ejecución: reglamento, fechas importantes, cierre de inscripciones, horarios de partidos, canchas disponibles y sanciones aplicables. |
| **Cómo se ejecutará**     | Mediante un panel de configuración avanzada disponible exclusivamente para el rol de Organizador, con secciones independientes por cada tipo de configuración. |
| **Actor principal**       | Organizador |
| **Precondiciones**        | 1) El torneo debe haber sido registrado previamente (RF01). <br> 2) El torneo debe estar en estado **Borrador** o **Activo** (no se permite configurar torneos En Progreso o Finalizados). <br> 3) El usuario debe contar con permisos de Organizador. |
| **Reglas de Negocio**     | 1) No se pueden programar partidos en horarios o canchas que no hayan sido definidos en este módulo. <br> 2) El reglamento debe ser accesible para todos los participantes en todo momento. <br> 3) La fecha de cierre de inscripciones debe ser anterior a la fecha de inicio del torneo. <br> 4) Las sanciones definidas aquí serán la base para el registro de incidencias en los partidos. <br> 5) No se puede modificar la configuración de canchas si ya hay partidos programados en ellas. |
| **Anexos**                | **Prototipos:** Mockup de Panel de Configuración de Torneo. <br> **Abreviaturas:** N/A |
| **Historial de revisión** | **Elaborado por:** Vanessa Torres <br> **Aprobado por:** David Cajamarca <br> **Fecha:** 03/03/2026 <br> **Descripción y Justificación de cambios:** Se agregó restricción de estado para configuración y la protección de canchas con partidos programados. |

**DATOS DE ENTRADA:**

| Nombre | Descripción | Tipo de campo | Reglas / Aplicación | Obligatorio |
|:-------|:------------|:--------------|:--------------------|:------------|
| Reglamento | Documento o texto con las reglas del torneo | Texto/Documento | Debe ser legible y accesible para todos los participantes | Sí |
| Fechas importantes | Fechas clave del torneo (inicio fase grupos, eliminatorias, etc.) | Lista de fechas | Deben estar dentro del rango fecha_inicio - fecha_fin del torneo | Sí |
| Cierre de inscripciones | Fecha y hora límite para inscribir equipos | Fecha/Hora | Debe ser anterior a la fecha de inicio del torneo | Sí |
| Horarios de partidos | Franjas horarias disponibles para programar partidos | Lista de horarios | Deben ser horarios coherentes (no solapados) | Sí |
| Canchas | Canchas disponibles para el torneo | Lista de texto | Cada cancha debe tener nombre y ubicación | Sí |
| Sanciones | Reglas de sanciones aplicables (tarjetas, suspensiones) | Texto | Deben incluir criterio de acumulación de tarjetas | Sí |

**DATOS DE SALIDA:**

| Nombre | Descripción | Tipo de campo | Reglas / Aplicación | Obligatorio |
|:-------|:------------|:--------------|:--------------------|:------------|
| Confirmación | Notificación de configuración guardada | Texto | Se muestra al organizador tras guardar | Sí |
| Parámetros publicados | Configuración visible para participantes | Objeto JSON | Reglamento, fechas y canchas accesibles públicamente | Sí |

**FLUJO BÁSICO:**

| Paso | Actor | Descripción | Excepciones |
|:-----|:------|:------------|:------------|
| 1 | Organizador | Accede al torneo previamente creado y selecciona "Configurar torneo" | — |
| 2 | Organizador | Ingresa el reglamento del torneo | — |
| 3 | Organizador | Define las fechas importantes y el cierre de inscripciones | E1: Cierre posterior al inicio |
| 4 | Organizador | Define los horarios disponibles para partidos | E2: Horarios solapados |
| 5 | Organizador | Registra las canchas disponibles | — |
| 6 | Organizador | Define las reglas de sanciones | — |
| 7 | Sistema | Almacena la configuración y la asocia al torneo | — |
| 8 | Sistema | Publica los parámetros para consulta de los participantes | — |

**FLUJO ALTERNO:**

| Paso | Actor | Descripción | Excepciones |
|:-----|:------|:------------|:------------|
| E1 | Sistema | Muestra mensaje: "La fecha de cierre de inscripciones debe ser anterior a la fecha de inicio del torneo" | Regresa al paso 3 |
| E2 | Sistema | Muestra mensaje: "Los horarios definidos se solapan entre sí" | Regresa al paso 4 |
| A1 | Organizador | Si intenta configurar un torneo en estado "En Progreso" o "Finalizado" | E3 |
| E3 | Sistema | Muestra mensaje: "Solo se pueden configurar torneos en estado Borrador o Activo" | Regresa al panel principal |
| A2 | Organizador | Si intenta modificar una cancha que ya tiene partidos programados | E4 |
| E4 | Sistema | Muestra mensaje: "No se puede modificar esta cancha porque tiene partidos programados" | Regresa al paso 5 |

**Notas y comentarios:** La configuración del torneo es requisito previo para poder activar el torneo (transición Borrador → Activo en RF01).

---

### RF07: Alineaciones

| Campo                     | Detalle |
|:--------------------------|:--------|
| **Código**                | RF07 |
| **Nombre**                | Alineaciones |
| **Descripción**           | El sistema debe permitir al capitán organizar la alineación del equipo antes de cada partido, seleccionando titulares y reservas, eligiendo una formación táctica y ubicando a los jugadores visualmente en la cancha. Además, tanto capitanes como jugadores podrán consultar la alineación del equipo rival una vez publicada. |
| **Cómo se ejecutará**     | Mediante un módulo por partido donde el capitán elige formación, selecciona titulares y reservas, y dispone a los titulares en un esquema visual de cancha. El sistema publica la alineación para consulta. |
| **Actor principal**       | Capitán |
| **Precondiciones**        | 1) El capitán debe estar autenticado. <br> 2) El equipo debe existir y tener una cantidad válida de jugadores. <br> 3) Debe existir un partido programado para el equipo. |
| **Reglas de Negocio**     | 1) Durante cada partido participarán **mínimo 7 jugadores** por equipo. <br> 2) Antes de cada partido, el capitán define quiénes participarán; los demás quedan como reservas. <br> 3) Capitanes y jugadores pueden **consultar la alineación del equipo rival**. <br> 4) **No se permiten cambios de jugadores entre equipos**; se juega el torneo con la nómina inicial. |
| **Anexos**                | **Prototipos:** Mockup de módulo de alineación con cancha visual. <br> **Abreviaturas:** N/A |
| **Historial de revisión** | **Elaborado por:** Santiago Cajamarca <br> **Aprobado por:** Juan Esteban Rodríguez <br> **Fecha:** 04/03/2026 <br> **Descripción y Justificación de cambios:** Se formalizaron las tablas de datos y flujos según formato corregido. |

**DATOS DE ENTRADA:**

| Nombre | Descripción | Tipo de campo | Reglas / Aplicación | Obligatorio |
|:-------|:------------|:--------------|:--------------------|:------------|
| Partido | Partido para el cual se define la alineación | Selección (ID) | Solo partidos programados del equipo del capitán | Sí |
| Formación | Formación táctica del equipo | Selección | Opciones predefinidas: 2-3-1, 3-2-1, 3-3, 2-4, etc. | Sí |
| Titulares | Lista de jugadores titulares | Lista de IDs | Exactamente 7 jugadores de la nómina del equipo | Sí |
| Reservas | Lista de jugadores suplentes | Lista de IDs | Los jugadores restantes de la nómina que no son titulares | Sí |
| Posiciones en cancha | Ubicación visual de cada titular en la cancha | Lista de coordenadas | Cada titular debe tener una posición asignada | Sí |

**DATOS DE SALIDA:**

| Nombre | Descripción | Tipo de campo | Reglas / Aplicación | Obligatorio |
|:-------|:------------|:--------------|:--------------------|:------------|
| Alineación guardada | Confirmación de alineación creada | Objeto JSON | Incluye titulares, reservas, formación y posiciones | Sí |
| Vista de cancha | Representación visual de la alineación | Imagen/Componente | Muestra jugadores posicionados según formación | Sí |
| Alineación rival | Alineación del equipo contrario (si publicada) | Objeto JSON | Disponible solo después de que ambos capitanes publiquen | Sí |

**FLUJO BÁSICO:**

| Paso | Actor | Descripción | Excepciones |
|:-----|:------|:------------|:------------|
| 1 | Capitán | Accede al partido y abre el módulo de alineación | — |
| 2 | Capitán | Selecciona una formación táctica | — |
| 3 | Capitán | Selecciona 7 titulares de la nómina del equipo | E1: Menos o más de 7 titulares |
| 4 | Sistema | Asigna automáticamente los jugadores restantes como reservas | — |
| 5 | Capitán | Ubica visualmente a los titulares en la cancha según la formación | — |
| 6 | Capitán | Confirma y publica la alineación | — |
| 7 | Sistema | Guarda y publica la alineación para consulta | — |

**FLUJO ALTERNO:**

| Paso | Actor | Descripción | Excepciones |
|:-----|:------|:------------|:------------|
| E1 | Sistema | Muestra mensaje: "Debe seleccionar exactamente 7 titulares" | Regresa al paso 3 |
| A1 | Capitán/Jugador | Si desea consultar la alineación del rival, accede a la vista del partido | E2 |
| E2 | Sistema | Si el rival aún no ha publicado su alineación: "La alineación del rival aún no está disponible" | — |

**Notas y comentarios:** La visualización en cancha requiere un componente interactivo en el frontend con drag & drop.

---

### RF08: Registro de Partidos

| Campo                     | Detalle |
|:--------------------------|:--------|
| **Código**                | RF08 |
| **Nombre**                | Registro de Partidos |
| **Descripción**           | El sistema debe permitir al organizador registrar la información completa de cada partido disputado: marcador final, jugadores goleadores, tarjetas amarillas y tarjetas rojas. El registro de un resultado dispara la actualización automática de tabla de posiciones y estadísticas. |
| **Cómo se ejecutará**     | Mediante un formulario de registro de resultados accesible desde el panel del organizador, asociado a cada partido programado. |
| **Actor principal**       | Organizador |
| **Precondiciones**        | 1) El usuario debe estar autenticado con rol de Organizador. <br> 2) Debe existir un partido programado con ambos equipos asignados. <br> 3) El torneo debe estar en estado "En Progreso". |
| **Reglas de Negocio**     | 1) La suma de goles registrados por jugador de cada equipo debe coincidir con el marcador del equipo. <br> 2) Un jugador que recibe tarjeta roja no puede recibir más tarjetas en el mismo partido. <br> 3) Solo se pueden registrar goles y tarjetas de jugadores que estén en la alineación del partido (titulares). <br> 4) Un resultado registrado no puede modificarse sin aprobación del administrador. |
| **Anexos**                | **Prototipos:** Mockup de Registro de Resultados. <br> **Abreviaturas:** N/A |
| **Historial de revisión** | **Elaborado por:** Santiago Cajamarca <br> **Aprobado por:** Juan Esteban Rodríguez <br> **Fecha:** 04/03/2026 <br> **Descripción y Justificación de cambios:** Se formalizaron tablas y se agregó la regla de coherencia de goles con marcador. |

**DATOS DE ENTRADA:**

| Nombre | Descripción | Tipo de campo | Reglas / Aplicación | Obligatorio |
|:-------|:------------|:--------------|:--------------------|:------------|
| Partido | Partido al que corresponde el resultado | Selección (ID) | Solo partidos programados sin resultado previo | Sí |
| Goles equipo local | Cantidad de goles del equipo local | Numérico (entero) | Debe ser ≥ 0 | Sí |
| Goles equipo visitante | Cantidad de goles del equipo visitante | Numérico (entero) | Debe ser ≥ 0 | Sí |
| Goleadores | Lista de jugadores que anotaron gol | Lista de objetos | Cada entrada: jugador (ID), minuto, equipo. La suma debe coincidir con el marcador | Sí |
| Tarjetas amarillas | Lista de tarjetas amarillas mostradas | Lista de objetos | Cada entrada: jugador (ID), minuto. Solo jugadores en alineación | No |
| Tarjetas rojas | Lista de tarjetas rojas mostradas | Lista de objetos | Cada entrada: jugador (ID), minuto. Solo jugadores en alineación | No |

**DATOS DE SALIDA:**

| Nombre | Descripción | Tipo de campo | Reglas / Aplicación | Obligatorio |
|:-------|:------------|:--------------|:--------------------|:------------|
| Resultado registrado | Confirmación del resultado guardado | Objeto JSON | Incluye marcador, goleadores y tarjetas | Sí |
| Tabla actualizada | Tabla de posiciones recalculada | Objeto JSON | Se actualiza automáticamente tras el registro | Sí |
| Estadísticas actualizadas | Goleadores y tarjetas actualizados | Objeto JSON | Se actualiza automáticamente | Sí |

**FLUJO BÁSICO:**

| Paso | Actor | Descripción | Excepciones |
|:-----|:------|:------------|:------------|
| 1 | Organizador | Accede al partido programado y selecciona "Registrar resultado" | — |
| 2 | Organizador | Ingresa el marcador final (goles local y visitante) | — |
| 3 | Organizador | Registra los goleadores indicando jugador, minuto y equipo | E1: Goles no coinciden con marcador |
| 4 | Organizador | Registra las tarjetas amarillas y rojas (opcional) | E2: Jugador no está en alineación |
| 5 | Sistema | Valida la coherencia de toda la información registrada | E3: Datos inconsistentes |
| 6 | Sistema | Guarda el resultado y publica el evento "ResultadoRegistrado" | — |
| 7 | Sistema | Recalcula automáticamente tabla de posiciones y estadísticas | — |

**FLUJO ALTERNO:**

| Paso | Actor | Descripción | Excepciones |
|:-----|:------|:------------|:------------|
| E1 | Sistema | Muestra mensaje: "La cantidad de goles registrados no coincide con el marcador del equipo [nombre]" | Regresa al paso 3 |
| E2 | Sistema | Muestra mensaje: "El jugador [nombre] no está en la alineación de este partido" | Regresa al paso 4 |
| E3 | Sistema | Muestra mensaje detallado de la inconsistencia encontrada | Regresa al paso correspondiente |
| A1 | Organizador | Si necesita corregir un resultado ya registrado, debe solicitar aprobación al administrador | E4 |
| E4 | Sistema | Si el organizador no tiene permiso de administrador: "Solo un administrador puede modificar resultados ya registrados" | — |

**Notas y comentarios:** Se aplica el patrón **Observer** — al registrar un resultado, el sistema publica un evento que es escuchado por TablaPosicionesListener, EstadisticasGoleadoresListener, LlavesEliminatoriasListener y SancionesListener.

---

### RF09: Consulta de partidos

| Campo                     | Detalle |
|:--------------------------|:--------|
| **Código**                | RF09 |
| **Nombre**                | Consulta de partidos |
| **Descripción**           | El sistema debe permitir al árbitro consultar la información de los partidos que le han sido asignados: fecha, hora, cancha y equipos que disputarán el encuentro, para poder prepararse adecuadamente. |
| **Cómo se ejecutará**     | Mediante un panel exclusivo para el árbitro que muestra sus partidos asignados con toda la información logística. |
| **Actor principal**       | Árbitro |
| **Precondiciones**        | 1) El árbitro debe estar autenticado con rol de Árbitro. <br> 2) Deben existir partidos programados con árbitro asignado. |
| **Reglas de Negocio**     | 1) El árbitro solo puede ver los partidos que le han sido asignados. <br> 2) La información del partido debe incluir al menos: fecha/hora, cancha y ambos equipos. <br> 3) El árbitro no puede modificar la información del partido. |
| **Anexos**                | **Prototipos:** Mockup de Panel del Árbitro. <br> **Abreviaturas:** N/A |
| **Historial de revisión** | **Elaborado por:** Santiago Cajamarca <br> **Aprobado por:** Juan Esteban Rodríguez <br> **Fecha:** 04/03/2026 <br> **Descripción y Justificación de cambios:** Se formalizaron las tablas de datos y flujos. |

**DATOS DE ENTRADA:**

| Nombre | Descripción | Tipo de campo | Reglas / Aplicación | Obligatorio |
|:-------|:------------|:--------------|:--------------------|:------------|
| ID del árbitro | Identificador del árbitro autenticado | Texto (UUID) | Se obtiene automáticamente de la sesión del usuario | Sí |
| Filtro de fecha | Rango de fechas para filtrar partidos | Fecha (rango) | Opcional, para ver partidos de un periodo específico | No |

**DATOS DE SALIDA:**

| Nombre | Descripción | Tipo de campo | Reglas / Aplicación | Obligatorio |
|:-------|:------------|:--------------|:--------------------|:------------|
| Lista de partidos | Partidos asignados al árbitro | Lista de objetos | Solo partidos asignados al árbitro autenticado | Sí |
| Detalle por partido | Información logística de cada partido | Objeto JSON | Fecha, hora, cancha, equipo local, equipo visitante | Sí |

**FLUJO BÁSICO:**

| Paso | Actor | Descripción | Excepciones |
|:-----|:------|:------------|:------------|
| 1 | Árbitro | Accede al panel de "Mis partidos" | — |
| 2 | Sistema | Consulta los partidos asignados al árbitro autenticado | — |
| 3 | Sistema | Muestra la lista de partidos con fecha, hora, cancha y equipos | E1: Sin partidos asignados |
| 4 | Árbitro | Selecciona un partido para ver su detalle completo | — |

**FLUJO ALTERNO:**

| Paso | Actor | Descripción | Excepciones |
|:-----|:------|:------------|:------------|
| E1 | Sistema | Muestra mensaje: "No tiene partidos asignados en este momento" | — |
| A1 | Árbitro | Si aplica filtro de fecha y no hay partidos en el rango: "No hay partidos programados para las fechas seleccionadas" | — |

**Notas y comentarios:** Es un módulo de solo lectura; el árbitro no puede modificar información.

---

### RF10: Tabla de Posiciones

| Campo                     | Detalle |
|:--------------------------|:--------|
| **Código**                | RF10 |
| **Nombre**                | Tabla de Posiciones |
| **Descripción**           | El sistema debe calcular y mostrar automáticamente la tabla de posiciones por equipo a partir de los resultados registrados, incluyendo: partidos jugados (PJ), ganados (PG), empatados (PE), perdidos (PP), goles a favor (GF), goles en contra (GC), diferencia de gol (DG) y puntos (PTS). La tabla se recalcula cada vez que se registra o actualiza un resultado. |
| **Cómo se ejecutará**     | Mediante un módulo de consulta del torneo accesible para cualquier usuario autenticado. |
| **Actor principal**       | Usuario autenticado (cualquier rol) |
| **Precondiciones**        | 1) Debe existir un torneo creado y visible. <br> 2) Debe existir al menos un equipo inscrito. |
| **Reglas de Negocio**     | 1) La tabla se calcula automáticamente a partir de los partidos con resultado registrado. <br> 2) **PJ = PG + PE + PP**. <br> 3) **DG = GF - GC**. <br> 4) Victoria = 3 puntos, Empate = 1 punto, Derrota = 0 puntos. <br> 5) En caso de empate en puntos, se ordena por: a) diferencia de gol, b) goles a favor, c) resultado del enfrentamiento directo. <br> 6) Si no hay partidos registrados, la tabla muestra todos los equipos con valores en cero. |
| **Anexos**                | **Prototipos:** Mockup de Tabla de Posiciones. <br> **Abreviaturas:** PJ, PG, PE, PP, GF, GC, DG, PTS |
| **Historial de revisión** | **Elaborado por:** Santiago Cajamarca <br> **Aprobado por:** Juan Esteban Rodríguez <br> **Fecha:** 04/03/2026 <br> **Descripción y Justificación de cambios:** Se definieron los criterios de desempate y la puntuación. |

**DATOS DE ENTRADA:**

| Nombre | Descripción | Tipo de campo | Reglas / Aplicación | Obligatorio |
|:-------|:------------|:--------------|:--------------------|:------------|
| Torneo | Torneo del cual se consulta la tabla | Selección (ID) | Solo torneos con al menos un equipo inscrito | Sí |

**DATOS DE SALIDA:**

| Nombre | Descripción | Tipo de campo | Reglas / Aplicación | Obligatorio |
|:-------|:------------|:--------------|:--------------------|:------------|
| Tabla de posiciones | Lista ordenada de equipos con estadísticas | Lista de objetos | Cada equipo: nombre, PJ, PG, PE, PP, GF, GC, DG, PTS | Sí |
| Posición | Número de posición en la tabla | Numérico | Ordenado por PTS, DG, GF, enfrentamiento directo | Sí |

**FLUJO BÁSICO:**

| Paso | Actor | Descripción | Excepciones |
|:-----|:------|:------------|:------------|
| 1 | Usuario | Ingresa al torneo y selecciona "Tabla de posiciones" | — |
| 2 | Sistema | Obtiene todos los equipos inscritos y los resultados registrados | — |
| 3 | Sistema | Calcula por equipo: PJ, PG, PE, PP, GF, GC, DG y PTS | — |
| 4 | Sistema | Ordena la tabla aplicando criterios de desempate si es necesario | — |
| 5 | Sistema | Muestra la tabla actualizada al usuario | — |

**FLUJO ALTERNO:**

| Paso | Actor | Descripción | Excepciones |
|:-----|:------|:------------|:------------|
| A1 | Sistema | Si no existen partidos con resultado, muestra la tabla con todos los valores en cero | — |
| A2 | Sistema | Si un resultado de partido se actualiza, recalcula y actualiza la tabla inmediatamente | — |

**Notas y comentarios:** La tabla se actualiza reactivamente mediante el patrón **Observer** (TablaPosicionesListener).

---

### RF11: Llaves Eliminatorias

| Campo                     | Detalle |
|:--------------------------|:--------|
| **Código**                | RF11 |
| **Nombre**                | Llaves Eliminatorias |
| **Descripción**           | El sistema debe generar automáticamente el cuadro de eliminación directa del torneo, incluyendo la creación de los partidos iniciales de manera aleatoria y la progresión por fases: cuartos de final, semifinal y final. El bracket se actualiza conforme se registren los resultados, avanzando automáticamente a los ganadores. |
| **Cómo se ejecutará**     | Mediante un módulo del torneo donde el organizador activa la generación del bracket y el sistema construye las llaves; luego el sistema avanza automáticamente los equipos ganadores. |
| **Actor principal**       | Organizador |
| **Precondiciones**        | 1) El usuario debe estar autenticado con rol de Organizador. <br> 2) Debe existir un torneo en estado Activo o En Progreso. <br> 3) Deben existir los equipos clasificados para eliminación directa. <br> 4) Debe estar definida la cantidad de equipos que ingresan a llaves. |
| **Reglas de Negocio**     | 1) Los partidos iniciales se generan de manera aleatoria. <br> 2) El sistema genera y mantiene las fases: cuartos de final, semifinal y final. <br> 3) El ganador de un partido avanza automáticamente a la siguiente ronda. <br> 4) Una vez publicada la llave, los emparejamientos no cambian salvo acción administrativa controlada. <br> 5) No se puede regenerar el bracket si ya existen resultados registrados. |
| **Anexos**                | **Prototipos:** Mockup de vista de bracket y panel de generación. <br> **Abreviaturas:** N/A |
| **Historial de revisión** | **Elaborado por:** Santiago Cajamarca <br> **Aprobado por:** Juan Esteban Rodríguez <br> **Fecha:** 04/03/2026 <br> **Descripción y Justificación de cambios:** Se formalizaron tablas de datos y flujos. |

**DATOS DE ENTRADA:**

| Nombre | Descripción | Tipo de campo | Reglas / Aplicación | Obligatorio |
|:-------|:------------|:--------------|:--------------------|:------------|
| Torneo | Torneo para el cual se generan las llaves | Selección (ID) | Solo torneos en estado Activo o En Progreso | Sí |
| Equipos clasificados | Cantidad de equipos que entran a eliminación | Numérico (entero) | Debe ser potencia de 2 (4, 8, 16) | Sí |
| Método de emparejamiento | Cómo se emparejan los equipos en ronda inicial | Selección | Aleatorio (por defecto) | Sí |

**DATOS DE SALIDA:**

| Nombre | Descripción | Tipo de campo | Reglas / Aplicación | Obligatorio |
|:-------|:------------|:--------------|:--------------------|:------------|
| Bracket generado | Cuadro de eliminación con todas las fases | Objeto JSON | Incluye partidos por fase con equipos asignados | Sí |
| Partidos creados | Lista de partidos generados por fase | Lista de objetos | Cada partido tiene equipos, fase y estado | Sí |
| Avance automático | Actualización del bracket al registrar resultados | Evento | El ganador se asigna automáticamente a la siguiente llave | Sí |

**FLUJO BÁSICO:**

| Paso | Actor | Descripción | Excepciones |
|:-----|:------|:------------|:------------|
| 1 | Organizador | Accede al torneo y entra al módulo "Llaves eliminatorias" | — |
| 2 | Organizador | Confirma la cantidad de equipos y el método de emparejamiento | — |
| 3 | Sistema | Valida que la cantidad de equipos sea compatible con eliminación directa (potencia de 2) | E1: Cantidad incompatible |
| 4 | Sistema | Genera de forma aleatoria los partidos iniciales y crea el bracket completo | — |
| 5 | Sistema | Publica el bracket para consulta de todos los usuarios | — |
| 6 | Sistema | Conforme se registren resultados, asigna ganadores a la siguiente llave automáticamente | — |

**FLUJO ALTERNO:**

| Paso | Actor | Descripción | Excepciones |
|:-----|:------|:------------|:------------|
| E1 | Sistema | Muestra mensaje: "La cantidad de equipos debe ser una potencia de 2 (4, 8, 16)" | Regresa al paso 2 |
| A1 | Sistema | Si un partido termina en empate y no hay regla de desempate configurada, bloquea el avance y solicita al organizador definir el criterio o registrar el ganador manualmente | — |
| A2 | Organizador | Si intenta regenerar llaves después de haber resultados registrados | E2 |
| E2 | Sistema | Muestra mensaje: "No se puede regenerar el bracket porque ya existen resultados registrados" | — |

**Notas y comentarios:** El avance automático de ganadores se gestiona mediante el patrón **Observer** (LlavesEliminatoriasListener).

---

### RF12: Estadísticas

| Campo                     | Detalle |
|:--------------------------|:--------|
| **Código**                | RF12 |
| **Nombre**                | Estadísticas |
| **Descripción**           | El sistema debe permitir consultar estadísticas del torneo calculadas automáticamente a partir de los partidos registrados: máximos goleadores (ranking por goles), historial completo de partidos (con filtros) y resultados desglosados por equipo. |
| **Cómo se ejecutará**     | Mediante un módulo de "Estadísticas" accesible desde el torneo, con secciones diferenciadas para cada tipo de consulta. |
| **Actor principal**       | Usuario autenticado (cualquier rol) |
| **Precondiciones**        | 1) Debe existir un torneo creado y visible. <br> 2) Para mostrar datos reales deben existir partidos con resultados registrados. |
| **Reglas de Negocio**     | 1) Las estadísticas se calculan automáticamente a partir de la información de los partidos. <br> 2) "Máximos goleadores" se ordena de mayor a menor por cantidad de goles; en caso de empate, por menos partidos jugados. <br> 3) El historial de partidos permite filtrar por equipo y rango de fechas. <br> 4) Los resultados por equipo reflejan únicamente partidos del torneo seleccionado. |
| **Anexos**                | **Prototipos:** Mockup de módulo de estadísticas. <br> **Abreviaturas:** N/A |
| **Historial de revisión** | **Elaborado por:** Santiago Cajamarca <br> **Aprobado por:** Juan Esteban Rodríguez <br> **Fecha:** 04/03/2026 <br> **Descripción y Justificación de cambios:** Se formalizaron las tablas y se agregó criterio de desempate en goleadores. |

**DATOS DE ENTRADA:**

| Nombre | Descripción | Tipo de campo | Reglas / Aplicación | Obligatorio |
|:-------|:------------|:--------------|:--------------------|:------------|
| Torneo | Torneo del cual se consultan estadísticas | Selección (ID) | Solo torneos existentes | Sí |
| Tipo de estadística | Sección a consultar | Selección | Máximos goleadores / Historial de partidos / Resultados por equipo | Sí |
| Equipo (filtro) | Equipo para filtrar resultados | Selección (ID) | Solo equipos inscritos en el torneo. Aplica para historial y resultados | No |
| Rango de fechas (filtro) | Periodo de tiempo para filtrar | Fecha (rango) | Aplica para historial de partidos | No |

**DATOS DE SALIDA:**

| Nombre | Descripción | Tipo de campo | Reglas / Aplicación | Obligatorio |
|:-------|:------------|:--------------|:--------------------|:------------|
| Máximos goleadores | Ranking de jugadores por goles anotados | Lista de objetos | Cada entrada: jugador, equipo, goles. Ordenado desc. por goles | Condicional |
| Historial de partidos | Lista de partidos jugados | Lista de objetos | Cada entrada: fecha, hora, equipos, marcador. Filtrable | Condicional |
| Resultados por equipo | Detalle de rendimiento de un equipo | Objeto JSON | PJ, PG, PE, PP, GF, GC, goleadores del equipo | Condicional |

**FLUJO BÁSICO:**

| Paso | Actor | Descripción | Excepciones |
|:-----|:------|:------------|:------------|
| 1 | Usuario | Ingresa al torneo y selecciona "Estadísticas" | — |
| 2 | Usuario | Selecciona el tipo de estadística a consultar | — |
| 3 | Usuario | Aplica filtros opcionales (equipo, fechas) | — |
| 4 | Sistema | Consulta los resultados registrados y calcula la estadística solicitada | — |
| 5 | Sistema | Muestra la información en la vista correspondiente | E1: Sin datos |

**FLUJO ALTERNO:**

| Paso | Actor | Descripción | Excepciones |
|:-----|:------|:------------|:------------|
| E1 | Sistema | Si no existen partidos con resultados, muestra las secciones vacías con mensaje: "Aún no hay datos disponibles" | — |
| A1 | Usuario | Si filtra por un equipo sin partidos jugados, el sistema muestra: "Este equipo aún no ha disputado partidos" | — |

**Notas y comentarios:** Las estadísticas se actualizan reactivamente mediante el patrón **Observer** (EstadisticasGoleadoresListener).

---

### RF13: Autenticación y Login

| Campo                     | Detalle |
|:--------------------------|:--------|
| **Código**                | RF13 |
| **Nombre**                | Autenticación y Login |
| **Descripción**           | El sistema debe permitir a los usuarios iniciar sesión utilizando su correo electrónico registrado (institucional o Gmail) y contraseña. El sistema debe gestionar sesiones mediante tokens JWT y permitir el cierre de sesión seguro. |
| **Cómo se ejecutará**     | Mediante una pantalla de login que valida credenciales contra el backend y retorna un token JWT para las peticiones subsiguientes. |
| **Actor principal**       | Cualquier usuario registrado |
| **Precondiciones**        | 1) El usuario debe haberse registrado previamente (RF02). |
| **Reglas de Negocio**     | 1) Las credenciales se validan contra los datos registrados en el sistema. <br> 2) El token JWT tiene un tiempo de expiración configurable. <br> 3) Todas las rutas protegidas del sistema requieren un token válido. <br> 4) La contraseña debe almacenarse encriptada (hash). |
| **Anexos**                | **Prototipos:** Mockup de pantalla de Login. <br> **Abreviaturas:** JWT (JSON Web Token) |
| **Historial de revisión** | **Elaborado por:** Santiago Cajamarca <br> **Aprobado por:** --- <br> **Fecha:** 09/03/2026 <br> **Descripción y Justificación de cambios:** Nuevo requerimiento creado a partir de la separación de RNF01 como funcionalidad del sistema. |

**DATOS DE ENTRADA:**

| Nombre | Descripción | Tipo de campo | Reglas / Aplicación | Obligatorio |
|:-------|:------------|:--------------|:--------------------|:------------|
| Correo electrónico | Correo registrado del usuario | Email (texto) | Debe existir en el sistema | Sí |
| Contraseña | Clave de acceso del usuario | Texto (password) | Se valida contra el hash almacenado | Sí |

**DATOS DE SALIDA:**

| Nombre | Descripción | Tipo de campo | Reglas / Aplicación | Obligatorio |
|:-------|:------------|:--------------|:--------------------|:------------|
| Token JWT | Token de autenticación para la sesión | Texto | Contiene ID del usuario, roles y fecha de expiración | Sí |
| Datos del usuario | Información básica del usuario autenticado | Objeto JSON | Nombre, correo, roles asignados | Sí |

**FLUJO BÁSICO:**

| Paso | Actor | Descripción | Excepciones |
|:-----|:------|:------------|:------------|
| 1 | Usuario | Accede a la pantalla de login e ingresa correo y contraseña | — |
| 2 | Sistema | Valida que el correo exista en el sistema | E1: Correo no registrado |
| 3 | Sistema | Valida que la contraseña coincida con el hash almacenado | E2: Contraseña incorrecta |
| 4 | Sistema | Genera token JWT con los datos y roles del usuario | — |
| 5 | Sistema | Retorna el token y los datos básicos del usuario | — |

**FLUJO ALTERNO:**

| Paso | Actor | Descripción | Excepciones |
|:-----|:------|:------------|:------------|
| E1 | Sistema | Muestra mensaje: "El correo electrónico no se encuentra registrado" | Regresa al paso 1 |
| E2 | Sistema | Muestra mensaje: "La contraseña ingresada es incorrecta" | Regresa al paso 1 |
| A1 | Usuario | Si desea cerrar sesión, el sistema invalida el token y redirige al login | — |

**Notas y comentarios:** Se implementa con Spring Security y JWT en el backend.

---

### RF14: Control de roles y permisos

| Campo                     | Detalle |
|:--------------------------|:--------|
| **Código**                | RF14 |
| **Nombre**                | Control de roles y permisos |
| **Descripción**           | El sistema debe gestionar un esquema de roles y permisos que controle el acceso a las funcionalidades según el tipo de usuario. Cada usuario tiene uno o más roles que determinan qué módulos y acciones puede ejecutar dentro de la plataforma. |
| **Cómo se ejecutará**     | Mediante un sistema de autorización basado en roles (RBAC) integrado en el backend, con un panel de administración para gestionar asignaciones. |
| **Actor principal**       | Administrador |
| **Precondiciones**        | 1) El usuario debe estar autenticado (RF13). <br> 2) El administrador debe tener el rol de Administrador asignado. |
| **Reglas de Negocio**     | 1) Los roles del sistema son: Estudiante, Graduado, Profesor, Personal Administrativo, Familiar, Capitán, Organizador, Árbitro, Administrador. <br> 2) Un usuario puede tener múltiples roles (ejemplo: Estudiante + Capitán). <br> 3) El rol de Capitán se asigna automáticamente al crear un equipo. <br> 4) Solo el Administrador puede asignar/revocar roles manualmente. <br> 5) Cada endpoint del API debe validar que el usuario tenga el rol requerido. |
| **Anexos**                | **Prototipos:** Mockup de Panel de Administración de Roles. <br> **Abreviaturas:** RBAC (Role-Based Access Control) |
| **Historial de revisión** | **Elaborado por:** Santiago Cajamarca <br> **Aprobado por:** --- <br> **Fecha:** 09/03/2026 <br> **Descripción y Justificación de cambios:** Nuevo requerimiento creado a partir de la separación de RNF02 como funcionalidad del sistema. |

**DATOS DE ENTRADA:**

| Nombre | Descripción | Tipo de campo | Reglas / Aplicación | Obligatorio |
|:-------|:------------|:--------------|:--------------------|:------------|
| Usuario | Usuario al cual asignar o revocar un rol | Selección (ID) | Debe existir en el sistema | Sí |
| Rol | Rol a asignar o revocar | Selección (enum) | Debe ser uno de los roles válidos del sistema | Sí |
| Acción | Tipo de operación | Selección | Asignar / Revocar | Sí |

**DATOS DE SALIDA:**

| Nombre | Descripción | Tipo de campo | Reglas / Aplicación | Obligatorio |
|:-------|:------------|:--------------|:--------------------|:------------|
| Confirmación | Notificación de operación exitosa | Texto | Indica qué rol se asignó/revocó y a quién | Sí |
| Roles actualizados | Lista de roles actuales del usuario | Lista de textos | Refleja los roles después de la operación | Sí |

**FLUJO BÁSICO:**

| Paso | Actor | Descripción | Excepciones |
|:-----|:------|:------------|:------------|
| 1 | Administrador | Accede al panel de administración de roles | — |
| 2 | Administrador | Busca al usuario por nombre o correo | E1: Usuario no encontrado |
| 3 | Administrador | Selecciona el rol y la acción (asignar/revocar) | — |
| 4 | Sistema | Valida que la operación sea coherente (no revocar un rol que no tiene) | E2: Operación inválida |
| 5 | Sistema | Ejecuta la operación y actualiza los roles del usuario | — |
| 6 | Sistema | Muestra confirmación con los roles actualizados | — |

**FLUJO ALTERNO:**

| Paso | Actor | Descripción | Excepciones |
|:-----|:------|:------------|:------------|
| E1 | Sistema | Muestra mensaje: "No se encontró un usuario con los datos ingresados" | Regresa al paso 2 |
| E2 | Sistema | Muestra mensaje: "El usuario no tiene el rol [nombre] para poder revocarlo" | Regresa al paso 3 |
| A1 | Sistema | Si un usuario crea un equipo (RF03), el rol Capitán se asigna automáticamente sin intervención del administrador | — |

**Notas y comentarios:** Se implementa con @PreAuthorize de Spring Security en cada controlador del backend.

---

## 3. Detalle de Requerimientos No Funcionales

| Código | Nombre | Descripción | Criterio de Aceptación |
|:-------|:-------|:------------|:----------------------|
| RNF01 | Registro de acciones (auditoría) | El sistema debe registrar automáticamente las acciones relevantes realizadas por los usuarios, incluyendo: usuario, fecha/hora, acción ejecutada y recurso afectado. | Toda acción de creación, modificación o eliminación queda registrada con trazabilidad completa. |
| RNF02 | Backend en Spring Boot por capas | El backend debe estar construido con Spring Boot siguiendo una arquitectura por capas: controladores, adaptadores, lógica de negocio y capa de datos. | El proyecto compila y ejecuta correctamente con separación clara de paquetes por capa. |
| RNF03 | Manejo de API REST | La comunicación entre frontend y backend se realiza mediante API REST con Spring Boot, siguiendo convenciones de nomenclatura y códigos HTTP estándar. | Todos los endpoints siguen la convención REST y retornan códigos HTTP apropiados (200, 201, 400, 401, 403, 404, 500). |
| RNF04 | Frontend en React con TypeScript | La aplicación web debe construirse con React y TypeScript, proporcionando una interfaz responsiva y tipada. | La aplicación funciona correctamente en navegadores modernos y está escrita en TypeScript. |
| RNF05 | Base de datos PostgreSQL | La persistencia de datos se realiza mediante PostgreSQL como motor de base de datos relacional. | Las entidades se mapean correctamente y las operaciones CRUD funcionan contra PostgreSQL. |
| RNF06 | Rendimiento | El sistema debe responder a las consultas en menos de 3 segundos bajo condiciones normales de uso (hasta 100 usuarios concurrentes). | El 95% de las peticiones responden en menos de 3 segundos. |
| RNF07 | Usabilidad | La plataforma debe ser intuitiva para estudiantes universitarios sin necesidad de capacitación previa, con navegación clara y mensajes de error comprensibles. | Un usuario nuevo puede completar el flujo de registro y creación de perfil en menos de 5 minutos. |
| RNF08 | Mantenibilidad | El sistema debe implementar patrones de diseño (Strategy, State, Factory Method, Observer) que faciliten la extensión y modificación del código sin afectar funcionalidades existentes. | Se pueden agregar nuevas reglas de validación, estados o tipos de usuario sin modificar clases existentes. |
