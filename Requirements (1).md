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

**DATOS DE ENTRADA:**

| Nombre | Descripción | Tipo de campo | Reglas / Aplicación | Obligatorio |
|:-------|:------------|:--------------|:--------------------|:------------|
| Nombre del Torneo | Nombre con el cual aparecera el torneo en la pagina| String | No debe extir un torneo con el mismo nombre que se encuentre activo o en progreso|Sí|
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
| 2 | Organizador | Ingresa fecha inicial, fecha final, cantidad de equipos y costo por equipo. | — |
| 3 | Organizador| Recibe la confimacion de la creación del torneo en pantalla, con el Id de torneo. | — |

**FLUJO ALTERNO:**

| Paso | Actor | Descripción | Excepciones |
|:-----|:------|:------------|:------------|
| E1 | Organizador | El usuario al hacer el registro de la fecha puso la fecha final igual o anterior a la incial. Muestra el mensaje: "La fecha final debe ser posterior a la fecha inicial" y no permite el guardado | Regresa al paso 2 |
| E2 | Organizador| El usuario al ingresar los datos pone una cantidad de equipos menor a 4. Muestra el mensaje: "La cantidad de equipos debe ser un número par mayor o igual a 4" | Regresa al paso 2 |
| E3 | Organizador| El usuario ingresa un nombre de torneo que ya existe. Muestra el mensaje: "Ya existe un torneo activo o en progreso para el periodo indicado" | Regresa al paso 2 |
| A1 | Organizador |El usuario pone el torneo como borrar y si desea desde las configuraciones del torneo, selecciona "Activar Torneo" desde el panel de gestión o en el estado del torneo selescciona "Activo | No le puuede dar activo si ya fue completado o eliminado |
| A3 | Organizador | El organizador desea finalizar el torneo va a las configuraciones del torneo y selecciona "Finalizar torneo" | E6: Hay partidos sin resultado registrado |
| E4 | organizador | El organizador desea activar el torneo pero no ha finalizado todos los coampos obligatorios. Muestra el  mensaje: "Debe completar la configuración del torneo antes de activarlo (RF06)" | Regresa al panel de gestión |
| E5 | Organizador | El usuario seleecciona iniciar torneo sin equipos inscritos .Muestra mensaje: "No se puede iniciar un torneo sin equipos inscritos" | Regresa al panel de gestión |
| E6 | Organizador |El organizador selecciona finalizar o eliminar torneo. Muestra el mensaje: "Existen partidos pendientes de resultado" | Regresa al panel de gestión |




| Sección | Detalle |
| :--- | :--- |
| **Reglas de Negocio** | <ul><li>1) El torneo se crea siempre en estado **Borrador** o en **Activo** si el administrador lo define. También puede pasar a *In Progress*, *Completed* o *Deleted*.</li><li>2) Transiciones permitidas: **Borrador → Activo → En Progreso → Finalizado**.</li><li>3) No se permiten transiciones inversas ni saltar estados.</li><li>4) La fecha final debe ser estrictamente posterior a la inicial.</li><li>5) Cantidad de equipos: número par $\ge 4$.</li><li>6) Un torneo **Finalizado** es de solo lectura.</li></ul> |
| **Anexos** | **Prototipos:** Mockup de Gestión Administrativa. <br> **Abreviaturas:** N/A <br><br> **Caso de Uso:** <br> ![Diagrama de Caso de Uso](https://github.com/user-attachments/assets/2c6a483f-16f0-4a38-bd5c-e780dd2dc764) |
| **Historial de Revisión** | <ul><li>**Elaborado por:** Vanessa Torres</li><li>**Aprobado por:** David Cajamarca</li><li>**Fecha:** 03/03/2026</li><li>**Cambios:** Se expandieron las reglas de negocio para incluir transiciones de estado y validaciones de fechas según feedback docente.</li></ul> |

### RF02: Registro de Jugadores

| Campo                     | Detalle |
|:--------------------------|:--------|
| **Código**                | RF02 |
| **Nombre**                | Registro de Jugadores |
| **Descripción**           | El sistema debe permitir a los participantes registrarse proporcionando su información básica y cuenta de correo (@escuelaing.edu.co para institucionales o @gmail.com para familiares). Una vez registrado, el usuario podrá completar su perfil deportivo (posiciones, dorsal, foto) y gestionar su disponibilidad para ser contactado por capitanes o responder a invitaciones de equipos. |
| **Cómo se ejecutará**     | A través de un formulario de registro con validación de dominio de correo y un panel de perfil deportivo donde el jugador define sus preferencias de juego y estado de disponibilidad. |
| **Actor principal**       | Estudiante, Graduado, Profesor, Personal Administrativo, Familiar |
| **Precondiciones**        | 1) El participante debe contar con un correo válido según su tipo de usuario. <br> 2) El correo electrónico no debe estar previamente registrado en el sistema. |

**DATOS DE ENTRADA:**

| Nombre | Descripción | Tipo de campo | Reglas / Aplicación | Obligatorio |
|:-------|:------------|:--------------|:--------------------|:------------|
| Correo electrónico | Correo para el registro | Email (texto) | Debe ser @escuelaing.edu.co o @gmail.com (familiares) | Sí |
| Contraseña | Clave de acceso | Texto (password) | Mínimo 8 caracteres, una mayúscula y un número | Sí |
| Nombre completo | Nombre y apellidos | Texto | Mínimo 3 caracteres | Sí |
| Tipo de usuario | Clasificación del usuario | Selección (enum) | Estudiante, Graduado, Profesor, Administrativo, Familiar | Sí |
| Posiciones de juego | Posiciones preferidas | Selección múltiple | Portero, Defensa, Volante, Delantero | Sí |
| Número dorsal | Número de camiseta | Numérico (entero) | Rango de 1 a 99 | Sí |
| Foto de perfil | Imagen del jugador | Imagen (archivo) | Formatos: JPG, PNG. Máximo: 2MB | No |
| Disponibilidad | Estado para búsquedas | Booleano | Por defecto: false | No |
| Semestre | Semestre del estudiante | Numérico (entero) | Solo para estudiantes (Rango 1-10) | Condicional |

**DATOS DE SALIDA:**

| Nombre | Descripción | Tipo de campo | Reglas / Aplicación | Obligatorio |
|:-------|:------------|:--------------|:--------------------|:------------|
| ID del jugador | Identificador único | Texto (UUID) | Generado automáticamente por el sistema | Sí |
| Perfil deportivo | Datos del perfil | Objeto JSON | Información técnica del jugador grabada | Sí |
| Mensaje de confirmación | Notificación de éxito | Texto | Se muestra al finalizar el registro o edición | Sí |

**FLUJO BÁSICO:**

| Paso | Actor | Descripción | Excepciones |
|:-----|:------|:------------|:------------|
| 1 | Usuario | Accede al módulo de registro y selecciona su tipo de usuario e ingresa sus datos básicos. | — |
| 2 | Usuario | Completa su perfil deportivo indicando posiciones, número dorsal y disponibilidad. | — |
| 3 | Usuario | Recibe la confirmación del registro exitoso en pantalla con su ID único de jugador. | — |

**FLUJO ALTERNO:**

| Paso | Actor | Descripción | Excepciones |
|:-----|:------|:------------|:------------|
| E1 | Usuario | El usuario ingresa un correo cuyo dominio no corresponde al tipo de usuario seleccionado. Muestra el mensaje: "El dominio del correo no corresponde al tipo de usuario seleccionado" | Regresa al paso 1 |
| E2 | Usuario | El usuario intenta registrar un correo que ya existe. Muestra el mensaje: "Este correo ya se encuentra registrado en el sistema" | Regresa al paso 1 |
| E3 | Usuario | El usuario ingresa un número dorsal fuera del rango permitido (1-99). Muestra el mensaje: "El número dorsal debe estar entre 1 y 99" | Regresa al paso 2 |
| A1 | Usuario | El usuario activa el estado de disponibilidad desde su panel de gestión para aparecer en las búsquedas de los capitanes. | No puede activarse si no ha completado campos obligatorios |
| A2 | Usuario | El usuario recibe una invitación de un equipo y selecciona "Aceptar invitación" desde su panel de notificaciones. | E4: Ya pertenece a un equipo |
| E4 | Usuario | El usuario intenta aceptar una invitación pero ya está vinculado a otro equipo. Muestra el mensaje: "No puedes aceptar la invitación porque ya perteneces a un equipo" | Regresa al panel de gestión |

| Sección | Detalle |
| :--- | :--- |
| **Reglas de Negocio** | <ul><li>1) Usuarios institucionales deben usar @escuelaing.edu.co y familiares @gmail.com.</li><li>2) El número dorsal es único por equipo (se valida al unirse formalmente).</li><li>3) Solo jugadores con estado **Disponible** son visibles para reclutamiento.</li><li>4) Un jugador no puede estar vinculado a dos equipos simultáneamente.</li><li>5) El sistema aplica patrón **Factory Method** para la creación de tipos de usuario.</li></ul> |
| **Anexos** | **Prototipos:** Mockup de Registro y Perfil. <br> **Abreviaturas:** ECI (Escuela Colombiana de Ingeniería). <br><br> **Caso de Uso:** <img width="380" height="149" alt="image" src="https://github.com/user-attachments/assets/cf59150a-2bf8-4ceb-a429-ae468f0eea46" />
| **Historial de Revisión** | <ul><li>**Elaborado por:** Vanessa Torres</li><li>**Aprobado por:** David Cajamarca</li><li>**Fecha:** 19/03/2026</li><li>**Cambios:** Reconstrucción de requerimiento tras pérdida de datos; ajuste de flujo alterno y eliminación de sistema como actor.</li></ul> |

### RF03: Creación y gestión de equipos

| Campo                     | Detalle |
|:--------------------------|:--------|
| **Código**                | RF03 |
| **Nombre**                | Creación y gestión de equipos |
| **Descripción**           | El sistema debe permitir a un jugador registrado crear un equipo, asumiendo automáticamente el rol de capitán. El capitán podrá definir la identidad visual (nombre, escudo, colores) e invitar a jugadores disponibles. Se validarán reglas estrictas de composición (mínimo/máximo de jugadores y programas académicos) antes de permitir la inscripción formal al torneo. |
| **Cómo se ejecutará**     | A través de un formulario de creación de equipo y un panel de gestión de nómina exclusivo para el usuario con rol de capitán. |
| **Actor principal**       | Capitán |
| **Precondiciones**        | 1) El usuario debe estar registrado con perfil deportivo completo. <br> 2) El usuario no debe pertenecer a ningún equipo actualmente. |

**DATOS DE ENTRADA:**

| Nombre | Descripción | Tipo de campo | Reglas / Aplicación | Obligatorio |
|:-------|:------------|:--------------|:--------------------|:------------|
| Nombre del equipo | Identificador del equipo | Texto | Único dentro del torneo. Máximo 50 caracteres | Sí |
| Escudo del equipo | Imagen representativa | Imagen (archivo) | Formatos: JPG, PNG. Tamaño máximo: 2MB | No |
| Colores del uniforme | Colores del equipo | Texto | Mínimo un color primario definido | Sí |
| Jugadores invitados | Lista de seleccionados | Lista de IDs | Solo jugadores con estado "Disponible" | Sí |

**DATOS DE SALIDA:**

| Nombre | Descripción | Tipo de campo | Reglas / Aplicación | Obligatorio |
|:-------|:------------|:--------------|:--------------------|:------------|
| ID del equipo | Identificador único | Texto (UUID) | Generado automáticamente por el sistema | Sí |
| Estado del equipo | Fase de formación | Texto (enum) | Se retorna inicialmente como "En formación" | Sí |
| Mensaje de confirmación | Notificación de éxito | Texto | Confirmación de creación y resumen de invitaciones | Sí |

**FLUJO BÁSICO:**

| Paso | Actor | Descripción | Excepciones |
|:-----|:------|:------------|:------------|
| 1 | Capitán | Accede al módulo de equipos y selecciona la opción "Crear equipo". | — |
| 2 | Capitán | Ingresa el nombre del equipo, colores de uniforme y carga el escudo (opcional). | E1: Nombre duplicado |
| 3 | Capitán | Busca jugadores disponibles en la plataforma y envía las invitaciones para conformar la nómina. | E2: Jugador no disponible |
| 4 | Capitán | Recibe la confirmación de la creación del equipo en estado "En formación" y el reporte de invitaciones enviadas. | — |

**FLUJO ALTERNO:**

| Paso | Actor | Descripción | Excepciones |
|:-----|:------|:------------|:------------|
| E1 | Capitán | El usuario ingresa un nombre de equipo que ya está registrado. Muestra el mensaje: "Ya existe un equipo con ese nombre en este torneo". | Regresa al paso 2 |
| E2 | Capitán | El usuario intenta invitar a un jugador que ya aceptó otra oferta o cambió su estado. Muestra el mensaje indicando que el jugador ya no está disponible. | Continúa con el resto de la lista |
| A1 | Capitán | El usuario selecciona "Inscribir equipo" para formalizar su participación en el torneo. | E3, E4, E5, E6 |
| E3 | Capitán | El equipo cuenta con menos de 7 jugadores. Muestra el mensaje: "El equipo necesita al menos 7 jugadores para inscribirse". | Regresa al panel de gestión |
| E4 | Capitán | El equipo excede los 12 jugadores permitidos. Muestra el mensaje: "El equipo no puede tener más de 12 jugadores". | Regresa al panel de gestión |
| E5 | Capitán | Menos del 50% de la nómina pertenece a los programas base (Sistemas, IA, Estadística, Ciber). Muestra mensaje de error de composición. | Regresa al panel de gestión |
| E6 | Capitán | Algún miembro pertenece a un programa no autorizado por el reglamento. Muestra el mensaje: "Todos los miembros deben pertenecer a los programas autorizados". | Regresa al panel de gestión |

| Sección | Detalle |
| :--- | :--- |
| **Reglas de Negocio** | <ul><li>1) Nómina: Mínimo 7 y máximo 12 jugadores fijos durante todo el torneo.</li><li>2) Composición: >50% de los miembros deben ser de Ing. Sistemas, IA, Ciberseguridad o Estadística.</li><li>3) Exclusividad: Un jugador no puede estar en dos equipos al mismo tiempo.</li><li>4) Programas permitidos: Pregrados base y Maestrías en Gestión de Información, Informática o Ciencia de Datos.</li><li>5) El sistema utiliza el patrón **Strategy** para validar las reglas de inscripción de forma independiente.</li></ul> |
| **Anexos** | **Prototipos:** Mockup de Gestión de Equipos y Nómina. <br> **Abreviaturas:** N/A <br><br> **Caso de Uso:** <br> <img width="717" height="303" alt="image" src="https://github.com/user-attachments/assets/8e04f9f0-9ac0-4259-944e-2bce47869574" />|
| **Historial de Revisión** | <ul><li>**Elaborado por:** Vanessa Torres</li><li>**Aprobado por:** David Cajamarca</li><li>**Fecha:** 19/03/2026</li><li>**Cambios:** Reconstrucción de requerimiento por pérdida de datos; unificación de criterios de programas académicos y ajuste de flujos.</li></ul> |


### RF04: Búsqueda de Jugadores

| Campo                     | Detalle |
|:--------------------------|:--------|
| **Código**                | RF04 |
| **Nombre**                | Búsqueda de Jugadores |
| **Descripción**           | El sistema debe permitir a los capitanes localizar jugadores disponibles mediante diversos filtros de búsqueda combinables (posición, semestre, edad, género) para evaluar e invitar candidatos a sus equipos. Los resultados mostrarán un perfil resumido con la información deportiva clave de cada jugador. |
| **Cómo se ejecutará**     | A través de un buscador con filtros dinámicos en el panel de gestión del capitán, con visualización de resultados paginados. |
| **Actor principal**       | Capitán |
| **Precondiciones**        | 1) El capitán debe estar autenticado en la plataforma. <br> 2) Deben existir jugadores registrados con perfiles deportivos marcados como "Disponibles". |

**DATOS DE ENTRADA:**

| Nombre | Descripción | Tipo de campo | Reglas / Aplicación | Obligatorio |
|:-------|:------------|:--------------|:--------------------|:------------|
| Posición | Posición de juego | Selección | Portero, Defensa, Volante, Delantero. (Múltiple) | No |
| Semestre | Semestre académico | Numérico (entero) | Solo aplica para perfiles "Estudiante" (1-10) | No |
| Edad | Edad del jugador | Numérico (entero) | Permite definir un rango (mínima - máxima) | No |
| Género | Género del jugador | Selección | Masculino, Femenino, Otro | No |
| Nombre | Nombre del jugador | Texto | Búsqueda parcial. Mínimo 2 caracteres | No |
| Identificación | Documento de identidad | Numérico | Búsqueda exacta por motivos de privacidad | No |

**DATOS DE SALIDA:**

| Nombre | Descripción | Tipo de campo | Reglas / Aplicación | Obligatorio |
|:-------|:------------|:--------------|:--------------------|:------------|
| Lista de jugadores | Resultados obtenidos | Lista de objetos | Paginación de máximo 20 registros por vista | Sí |
| Perfil resumido | Datos clave del jugador | Objeto JSON | Nombre, posiciones, dorsal, foto y tipo | Sí |
| Total de resultados | Conteo de coincidencias | Numérico | Utilizado para la gestión de la paginación | Sí |

**FLUJO BÁSICO:**

| Paso | Actor | Descripción | Excepciones |
|:-----|:------|:------------|:------------|
| 1 | Capitán | Accede al módulo de búsqueda de jugadores desde su panel de gestión. | — |
| 2 | Capitán | Selecciona o ingresa los criterios de búsqueda deseados para filtrar los candidatos. | — |
| 3 | Capitán | Visualiza la lista de jugadores que cumplen con los filtros y están marcados como "Disponibles". | E1: Sin resultados |
| 4 | Capitán | Selecciona un jugador de la lista para ver su perfil completo o proceder con el envío de una invitación. | — |

**FLUJO ALTERNO:**

| Paso | Actor | Descripción | Excepciones |
|:-----|:------|:------------|:------------|
| E1 | Capitán | El usuario ingresa criterios que no coinciden con ningún jugador disponible. Muestra el mensaje: "No se encontraron jugadores con los criterios ingresados". | Regresa al paso 2 |
| A1 | Capitán | El usuario no ingresa ningún filtro; el sistema despliega la totalidad de jugadores disponibles de forma paginada. | — |
| A2 | Capitán | El usuario aplica el filtro de semestre; el sistema excluye automáticamente a Graduados, Profesores o Familiares de los resultados. | — |

| Sección | Detalle |
| :--- | :--- |
| **Reglas de Negocio** | <ul><li>1) Visibilidad: Solo se muestran jugadores con estado **Disponible** y sin equipo vinculado.</li><li>2) Privacidad: La búsqueda por documento de identidad requiere el número exacto.</li><li>3) Filtrado: El filtro de semestre es exclusivo para el tipo de usuario **Estudiante**.</li><li>4) Paginación: La interfaz debe limitar la carga de datos para optimizar el rendimiento.</li><li>5) El sistema implementa el patrón **Specification** para manejar la lógica de filtros dinámicos.</li></ul> |
| **Anexos** | **Prototipos:** Mockup de Buscador de Jugadores y Filtros. <br> **Abreviaturas:** N/A <br><br> **Caso de Uso:** <br> <img width="697" height="351" alt="image" src="https://github.com/user-attachments/assets/b55cf61a-7305-4890-9467-df4c10af3bee" />) |
| **Historial de Revisión** | <ul><li>**Elaborado por:** Vanessa Torres</li><li>**Aprobado por:** David Cajamarca</li><li>**Fecha:** 19/03/2026</li><li>**Cambios:** Reconstrucción de requerimiento; ajuste de flujos alternos y especificación de lógica de filtrado por tipo de usuario.</li></ul> |

### RF05: Inscripción y pagos

| Campo                     | Detalle |
|:--------------------------|:--------|
| **Código**                | RF05 |
| **Nombre**                | Inscripción y pagos |
| **Descripción**           | El sistema debe permitir al capitán cargar el comprobante de pago realizado externamente (Nequi o efectivo) para que el organizador valide la inscripción oficial. La plataforma no procesa transacciones financieras; solo gestiona el flujo documental de verificación mediante estados controlados (Pendiente, En Revisión, Aprobado, Rechazado). |
| **Cómo se ejecutará**     | Mediante un módulo de carga de archivos para el capitán y una bandeja de gestión de validaciones para el organizador del torneo. |
| **Actor principal**       | Capitán / Organizador |
| **Precondiciones**        | 1) El equipo debe cumplir con el mínimo de 7 jugadores y las reglas de composición (RF03). <br> 2) El pago debe haberse realizado previamente por canales externos al sistema. |

**DATOS DE ENTRADA:**

| Nombre | Descripción | Tipo de campo | Reglas / Aplicación | Obligatorio |
|:-------|:------------|:--------------|:--------------------|:------------|
| Comprobante de pago | Soporte digital del pago | Archivo (Imagen/PDF)| Formatos: JPG, PNG, PDF. Máximo: 5MB | Sí |
| ID del equipo | Identificador del equipo | Texto (UUID) | Debe corresponder al equipo del capitán | Sí |

**DATOS DE SALIDA:**

| Nombre | Descripción | Tipo de campo | Reglas / Aplicación | Obligatorio |
|:-------|:------------|:--------------|:--------------------|:------------|
| Estado del pago | Fase de la validación | Texto (enum) | Pendiente, En Revisión, Aprobado, Rechazado | Sí |
| Notificación de estado | Aviso al usuario | Texto | Mensaje dinámico según la transición de estado | Sí |
| Estado del equipo | Estatus de participación | Texto (enum) | Cambia a "Inscrito" tras la aprobación del pago | Sí |

**FLUJO BÁSICO:**

| Paso | Actor | Descripción | Excepciones |
|:-----|:------|:------------|:------------|
| 1 | Capitán | Realiza el pago externo y accede al módulo de inscripción para subir el soporte digital. | E1: Archivo inválido |
| 2 | Capitán | Carga el archivo y visualiza el cambio de estado a "Pendiente" en su panel de gestión. | — |
| 3 | Organizador | Accede a la bandeja de validación, revisa el comprobante y marca el pago como "Aprobado". | A1: Comprobante rechazado |
| 4 | Capitán | Recibe la notificación de aprobación y visualiza que su equipo ahora figura como "Inscrito". | — |

**FLUJO ALTERNO:**

| Paso | Actor | Descripción | Excepciones |
|:-----|:------|:------------|:------------|
| E1 | Capitán | El usuario intenta subir un archivo con formato o tamaño no permitido. Muestra mensaje: "El archivo debe ser JPG, PNG o PDF y no exceder 5MB". | Regresa al paso 1 |
| A1 | Organizador | El organizador detecta que el soporte es ilegible o incorrecto y marca el pago como "Rechazado". | — |
| A2 | Capitán | El usuario recibe la notificación: "Su comprobante fue rechazado. Por favor suba un nuevo soporte" y procede a cargar uno nuevo. | Reinicia el flujo |
| A3 | Capitán | Si el pago es aprobado, el sistema bloquea la edición de la nómina básica para garantizar integridad. | — |

| Sección | Detalle |
| :--- | :--- |
| **Reglas de Negocio** | <ul><li>1) Verificación: Solo los equipos con pago **Aprobado** pueden ser sorteados en el fixture.</li><li>2) Responsabilidad: El capitán es el único autorizado para gestionar el soporte de pago.</li><li>3) Flujo: Las transiciones permitidas son: Pendiente → En Revisión → Aprobado/Rechazado.</li><li>4) Reincidencia: Un pago rechazado habilita nuevamente la carga de archivos al capitán.</li><li>5) El sistema aplica el patrón **State** para controlar el ciclo de vida del pago.</li></ul> |
| **Anexos** | **Prototipos:** Mockup de Carga de Pagos y Panel de Validación. <br> **Abreviaturas:** N/A <br><br> **Caso de Uso:** <br> <img width="698" height="332" alt="image" src="https://github.com/user-attachments/assets/b57a92fd-4917-43f3-9c12-b2c9bf4fefd9" />|
| **Historial de Revisión** | <ul><li>**Elaborado por:** Vanessa Torres</li><li>**Aprobado por:** David Cajamarca</li><li>**Fecha:** 19/03/2026</li><li>**Cambios:** Reconstrucción tras pérdida de datos; ajuste de flujo de validación y estados de inscripción.</li></ul> |

### RF06: Configuración del Torneo

| Campo                     | Detalle |
|:--------------------------|:--------|
| **Código**                | RF06 |
| **Nombre**                | Configurar Torneo |
| **Descripción**           | El sistema debe permitir al organizador definir los parámetros operativos, normativos y logísticos necesarios para la ejecución del torneo: reglamento, fechas clave, cierre de inscripciones, franjas horarias, canchas disponibles y régimen de sanciones. |
| **Cómo se ejecutará**     | Mediante un panel de configuración avanzada exclusivo para el rol de Organizador, con secciones independientes para cada parámetro logístico. |
| **Actor principal**       | Organizador |
| **Precondiciones**        | 1) El torneo debe haber sido registrado previamente (RF01). <br> 2) El torneo debe estar en estado **Borrador** o **Activo**. <br> 3) El usuario debe estar autenticado con permisos de Organizador. |

**DATOS DE ENTRADA:**

| Nombre | Descripción | Tipo de campo | Reglas / Aplicación | Obligatorio |
|:-------|:------------|:--------------|:--------------------|:------------|
| Reglamento | Normativa del torneo | Texto/Documento | Debe ser accesible para todos los participantes | Sí |
| Fechas importantes | Cronograma de fases | Lista de fechas | Deben estar dentro del rango del torneo | Sí |
| Cierre de inscripciones | Límite para equipos | Fecha/Hora | Debe ser estrictamente anterior al inicio del torneo | Sí |
| Horarios de partidos | Franjas disponibles | Lista de horarios | No se permiten solapamientos en la definición | Sí |
| Canchas | Espacios físicos | Lista de texto | Nombre y ubicación por cada escenario | Sí |
| Sanciones | Régimen disciplinario | Texto | Incluye criterios por tarjetas y suspensiones | Sí |

**DATOS DE SALIDA:**

| Nombre | Descripción | Tipo de campo | Reglas / Aplicación | Obligatorio |
|:-------|:------------|:--------------|:--------------------|:------------|
| Confirmación | Notificación de éxito | Texto | Confirmación tras el guardado de parámetros | Sí |
| Parámetros publicados | Configuración visible | Objeto JSON | Información pública para consulta de jugadores | Sí |

**FLUJO BÁSICO:**

| Paso | Actor | Descripción | Excepciones |
|:-----|:------|:------------|:------------|
| 1 | Organizador | Accede al torneo en estado Borrador y selecciona la opción "Configurar torneo". | — |
| 2 | Organizador | Carga el reglamento y define las fechas clave, incluyendo el cierre de inscripciones. | E1: Fecha inválida |
| 3 | Organizador | Establece los horarios permitidos para los encuentros y registra las canchas disponibles. | E2: Solapamiento |
| 4 | Organizador | Define las reglas de sanciones y guarda la configuración general del evento. | — |
| 5 | Organizador | Visualiza la confirmación de guardado y los parámetros quedan disponibles para el público. | — |

**FLUJO ALTERNO:**

| Paso | Actor | Descripción | Excepciones |
|:-----|:------|:------------|:------------|
| E1 | Organizador | El usuario intenta fijar el cierre de inscripciones después del inicio del torneo. Muestra mensaje de error de coherencia de fechas. | Regresa al paso 2 |
| E2 | Organizador | El usuario define franjas horarias que se cruzan entre sí. Muestra mensaje: "Los horarios definidos se solapan entre sí". | Regresa al paso 3 |
| A1 | Organizador | El usuario intenta configurar un torneo ya iniciado o finalizado. | E3 |
| E3 | Organizador | El sistema bloquea la edición y muestra mensaje: "Solo se pueden configurar torneos en estado Borrador o Activo". | Regresa al panel |
| A2 | Organizador | El usuario intenta modificar una cancha que ya tiene encuentros asignados en el fixture. | E4 |
| E4 | Organizador | El sistema impide el cambio y muestra mensaje: "No se puede modificar esta cancha porque tiene partidos programados". | Regresa al paso 3 |

| Sección | Detalle |
| :--- | :--- |
| **Reglas de Negocio** | <ul><li>1) Integridad: No se pueden programar partidos en horarios o escenarios no definidos en este módulo.</li><li>2) Visibilidad: El reglamento y las sanciones deben ser consultables en todo momento por los capitanes.</li><li>3) Restricción: La modificación de canchas se bloquea una vez generado el fixture (RF07).</li><li>4) Dependencia: La configuración completa es requisito para la transición del torneo de **Borrador** a **Activo**.</li><li>5) El sistema valida la coherencia cronológica de todas las fechas ingresadas.</li></ul> |
| **Anexos** | **Prototipos:** Mockup de Panel de Configuración de Torneo. <br> **Abreviaturas:** N/A <br><br> **Caso de Uso:** <br> <img width="692" height="222" alt="image" src="https://github.com/user-attachments/assets/4d423f16-253f-477b-b6ad-498cc2960ff4" />) |
| **Historial de Revisión** | <ul><li>**Elaborado por:** Vanessa Torres</li><li>**Aprobado por:** David Cajamarca</li><li>**Fecha:** 19/03/2026</li><li>**Cambios:** Reconstrucción de requerimiento; adición de restricciones de estado del torneo y validación de canchas programadas.</li></ul> |

### RF07: Alineaciones

| Campo                     | Detalle |
|:--------------------------|:--------|
| **Código**                | RF07 |
| **Nombre**                | Alineaciones |
| **Descripción**           | El sistema debe permitir al capitán organizar la estructura táctica del equipo antes de cada encuentro, definiendo titulares y reservas mediante un esquema visual de la cancha. Asimismo, permite la consulta de la alineación del equipo rival una vez que ambas partes hayan publicado su configuración. |
| **Cómo se ejecutará**     | A través de un módulo interactivo de "Pizarra Táctica" por partido, donde el capitán selecciona la formación y posiciona a los jugadores mediante una interfaz de arrastrar y soltar (drag & drop). |
| **Actor principal**       | Capitán |
| **Precondiciones**        | 1) El capitán debe estar autenticado. <br> 2) El equipo debe estar legalmente inscrito en el torneo (RF05). <br> 3) Debe existir un partido programado en el fixture para el equipo. |

**DATOS DE ENTRADA:**

| Nombre | Descripción | Tipo de campo | Reglas / Aplicación | Obligatorio |
|:-------|:------------|:--------------|:--------------------|:------------|
| Partido ID | Referencia al encuentro | UUID | Debe ser un partido programado del equipo | Sí |
| Formación | Esquema táctico | Selección (Enum) | Opciones: 2-3-1, 3-2-1, 3-3, 2-4, etc. | Sí |
| Titulares | Jugadores en campo | Lista de IDs | Exactamente 7 jugadores de la nómina | Sí |
| Reservas | Jugadores en banca | Lista de IDs | Jugadores restantes de la nómina inscrita | Sí |
| Posiciones visuales | Coordenadas en cancha | Mapa de puntos | Ubicación (x, y) de cada titular en el esquema | Sí |

**DATOS DE SALIDA:**

| Nombre | Descripción | Tipo de campo | Reglas / Aplicación | Obligatorio |
|:-------|:------------|:--------------|:--------------------|:------------|
| Alineación confirmada | Resumen del equipo | Objeto JSON | Incluye roles, titulares y formación guardada | Sí |
| Vista de cancha | Renderización visual | Componente UI | Representación gráfica de los jugadores en campo | Sí |
| Alineación rival | Datos del oponente | Objeto JSON | Visible solo tras la publicación de ambos capitanes | No |

**FLUJO BÁSICO:**

| Paso | Actor | Descripción | Excepciones |
|:-----|:------|:------------|:------------|
| 1 | Capitán | Accede al partido programado y abre el módulo de "Alineación". | — |
| 2 | Capitán | Selecciona la formación táctica deseada para el encuentro. | — |
| 3 | Capitán | Selecciona exactamente 7 titulares de su nómina actual. | E1: Cantidad inválida |
| 4 | Capitán | Ubica visualmente a cada titular en la posición correspondiente dentro de la cancha digital. | — |
| 5 | Capitán | Confirma y publica la alineación oficial del encuentro. | — |
| 6 | Capitán | Visualiza la confirmación de guardado y el reporte de jugadores en reserva. | — |

**FLUJO ALTERNO:**

| Paso | Actor | Descripción | Excepciones |
|:-----|:------|:------------|:------------|
| E1 | Capitán | El usuario intenta guardar con más o menos de 7 jugadores. Muestra mensaje: "Debe seleccionar exactamente 7 titulares para iniciar". | Regresa al paso 3 |
| A1 | Capitán/Jugador | El usuario accede a la vista del partido para espiar la táctica del oponente. | E2 |
| E2 | Capitán/Jugador | El rival no ha publicado su formación. Muestra mensaje: "La alineación del rival aún no está disponible". | — |
| A2 | Capitán | El usuario desea realizar cambios en la alineación antes del inicio del partido. | Permite edición si el partido no ha iniciado |

| Sección | Detalle |
| :--- | :--- |
| **Reglas de Negocio** | <ul><li>1) Participación: Cada equipo debe presentar obligatoriamente 7 jugadores para que el partido sea válido.</li><li>2) Automatización: El sistema asigna como reservas a todos los inscritos que no fueron marcados como titulares.</li><li>3) Transparencia: La alineación rival solo es visible cuando el capitán propio ya ha publicado la suya (evita espionaje previo).</li><li>4) Restricción de Nómina: No se pueden incluir jugadores que no pertenezcan a la lista original de 12 inscritos.</li><li>5) El sistema utiliza un componente interactivo para gestionar las coordenadas de posición en el frontend.</li></ul> |
| **Anexos** | **Prototipos:** Mockup de módulo de alineación con cancha visual e interactiva. <br> **Abreviaturas:** N/A <br><br> **Caso de Uso:** <br> <img width="682" height="193" alt="image" src="https://github.com/user-attachments/assets/764872ff-bcb9-4a3a-8637-e32df16ec5f1" />) |
| **Historial de Revisión** | <ul><li>**Elaborado por:** Santiago Cajamarca</li><li>**Aprobado por:** Juan Esteban Rodríguez</li><li>**Fecha:** 19/03/2026</li><li>**Cambios:** Reconstrucción de requerimiento; estandarización de flujos y reglas de visualización de alineación rival.</li></ul> |


### RF08: Registro de Resultados e Incidencias (Acta del Partido)

| Campo                     | Detalle |
|:--------------------------|:--------|
| **Código**                | RF08 |
| **Nombre**                | Registro de Resultados |
| **Descripción**           | El sistema debe permitir al árbitro registrar el acta digital del encuentro: marcador final, autores de los goles, minutos de las anotaciones y amonestaciones/expulsiones ocurridas en el campo. |
| **Cómo se ejecutará**     | A través de un formulario digital de "Cierre de Partido" habilitado para el usuario con rol de Árbitro una vez finalizado el tiempo reglamentario. |
| **Actor principal**       | Árbitro |
| **Precondiciones**        | 1) El usuario debe estar autenticado como Árbitro. <br> 2) El partido debe figurar en estado "Programado" o "En Juego". <br> 3) Las alineaciones de ambos equipos deben estar confirmadas en el sistema (RF07). |

**DATOS DE ENTRADA:**

| Nombre | Descripción | Tipo de campo | Reglas / Aplicación | Obligatorio |
|:-------|:------------|:--------------|:--------------------|:------------|
| Marcador Final | Goles Local vs Visitante | Numérico | Deben ser valores enteros ≥ 0 | Sí |
| Goleadores | Autores y minutos | Lista de objetos | Deben pertenecer a la alineación activa del partido | Sí |
| Amonestaciones | Tarjetas Amarillas | Lista de objetos | Máximo 2 por jugador; la segunda implica expulsión | No |
| Expulsiones | Tarjetas Rojas | Lista de objetos | Registro de minuto y motivo de la falta | No |

**FLUJO BÁSICO:**

| Paso | Actor | Descripción | Excepciones |
|:-----|:------|:------------|:------------|
| 1 | Árbitro | Selecciona el partido asignado desde su panel de control personal. | — |
| 2 | Árbitro | Ingresa el marcador global (goles anotados por cada bando). | — |
| 3 | Árbitro | Registra individualmente a los goleadores y el minuto exacto de cada tanto. | E1: Inconsistencia |
| 4 | Árbitro | Reporta las tarjetas amarillas y rojas aplicadas a los jugadores durante el encuentro. | E2: Jugador no válido |
| 5 | Árbitro | Confirma el acta final, bloqueando el partido para futuras ediciones ordinarias. | E3: Datos incompletos |

**FLUJO ALTERNO:**

| Paso | Actor | Descripción | Excepciones |
|:-----|:------|:------------|:------------|
| E1 | Árbitro | Los goles individuales no coinciden con el marcador global ingresado. El sistema solicita corregir las cifras. | Regresa al paso 2 |
| E2 | Árbitro | Intenta asignar una incidencia a un jugador que no participó (no estaba en alineación). | Regresa al paso 4 |
| A1 | Organizador | Si se requiere una corrección excepcional tras el cierre, el Organizador debe intervenir. | E4 |
| E4 | Organizador | El usuario sin permisos de administrador intenta editar un acta cerrada. | Bloqueo de acción |

| Sección | Detalle |
| :--- | :--- |
| **Reglas de Negocio** | <ul><li>1) **Autoridad:** El Árbitro es el único responsable de la veracidad de los datos en el acta de juego.</li><li>2) **Coherencia:** El acta no se considera válida si el sumatorio de goles por jugador difiere del marcador final.</li><li>3) **Impacto:** El cierre del acta dispara automáticamente el recálculo de la tabla de posiciones y el ranking de goleadores.</li><li>4) **Patrón Observer:** El registro del resultado es el evento que actualiza los módulos de estadísticas y sanciones.</li></ul> |
| **Anexos** | **Prototipos:** Mockup de Registro de Resultados. <br> **Abreviaturas:** GF (Goles a Favor), GC (Goles en Contra). <br><br> **Caso de Uso:** <br> <img width="782" height="311" alt="image" src="https://github.com/user-attachments/assets/c19568a9-00d4-4363-b66e-2fad21fa77f3" />|
| **Historial de Revisión** | <ul><li>**Elaborado por:** Santiago Cajamarca</li><li>**Aprobado por:** Juan Esteban Rodríguez</li><li>**Fecha:** 19/03/2026</li><li>**Cambios:** Reconstrucción de requerimiento; cambio de actor principal a Árbitro y ajuste de validaciones de acta.</li></ul> |


### RF09: Consulta de Partidos Asignados

| Campo                     | Detalle |
|:--------------------------|:--------|
| **Código**                | RF09 |
| **Nombre**                | Consulta de Partidos |
| **Descripción**           | El sistema debe permitir al árbitro visualizar la agenda de los encuentros que le han sido asignados, proporcionando detalles logísticos como fecha, hora, ubicación (cancha) y equipos participantes para su debida preparación. |
| **Cómo se ejecutará**     | A través de una vista de "Agenda Personal" o "Mis Partidos" dentro del panel del Árbitro, con opciones de filtrado cronológico. |
| **Actor principal**       | Árbitro |
| **Precondiciones**        | 1) El usuario debe estar autenticado con el rol de Árbitro. <br> 2) El organizador debe haber realizado previamente la asignación de árbitros a los encuentros programados. |

**DATOS DE ENTRADA:**

| Nombre | Descripción | Tipo de campo | Reglas / Aplicación | Obligatorio |
|:-------|:------------|:--------------|:--------------------|:------------|
| ID del Árbitro | Identificador único | UUID | Obtenido automáticamente mediante el token de sesión | Sí |
| Rango de Fechas | Filtro temporal | Fecha (Inicio/Fin)| Permite segmentar la búsqueda de encuentros | No |
| Estado del Partido| Filtro por situación | Selección (Enum) | Opciones: Programado, En Juego, Finalizado | No |

**DATOS DE SALIDA:**

| Nombre | Descripción | Tipo de campo | Reglas / Aplicación | Obligatorio |
|:-------|:------------|:--------------|:--------------------|:------------|
| Listado de Agenda | Colección de partidos | Lista de Objetos | Solo registros vinculados al ID del árbitro | Sí |
| Ficha Logística | Detalle del encuentro | Objeto JSON | Incluye: Cancha, hora exacta y nombres de equipos | Sí |

**FLUJO BÁSICO:**

| Paso | Actor | Descripción | Excepciones |
|:-----|:------|:------------|:------------|
| 1 | Árbitro | Accede al módulo de "Mis Partidos Asignados" desde el menú principal. | — |
| 2 | Árbitro | Visualiza la lista de encuentros pendientes ordenados por proximidad temporal. | E1: Sin asignaciones |
| 3 | Árbitro | Aplica filtros por fecha o estado si desea localizar un encuentro específico. | A1: Sin resultados |
| 4 | Árbitro | Selecciona un partido de la lista para desplegar la información logística completa. | — |

**FLUJO ALTERNO:**

| Paso | Actor | Descripción | Excepciones |
|:-----|:------|:------------|:------------|
| E1 | Árbitro | El usuario entra al módulo pero no tiene partidos vinculados. Muestra mensaje: "No tiene partidos asignados en este momento". | — |
| A1 | Árbitro | Al aplicar un filtro, no existen registros que coincidan. Muestra mensaje: "No se encontraron partidos para los criterios seleccionados". | Regresa al paso 2 |

| Sección | Detalle |
| :--- | :--- |
| **Reglas de Negocio** | <ul><li>1) **Privacidad:** Un árbitro no puede consultar la agenda privada de otros árbitros, solo la propia.</li><li>2) **Solo Lectura:** Este módulo no permite la edición de fechas, horas ni escenarios; cualquier cambio debe ser gestionado por el Organizador.</li><li>3) **Disponibilidad:** La información debe estar disponible 24/7 para garantizar que el árbitro conozca su programación.</li><li>4) **Sincronización:** Si el Organizador reprograma un partido (RF06), la vista del Árbitro debe actualizarse en tiempo real.</li></ul> |
| **Anexos** | **Prototipos:** Mockup de Panel del Árbitro (Vista Agenda). <br> **Abreviaturas:** N/A <br><br> **Caso de Uso:** <br><img width="792" height="324" alt="image" src="https://github.com/user-attachments/assets/49dd8ba2-9120-4dfa-adff-dce1993d16fa" />|
| **Historial de Revisión** | <ul><li>**Elaborado por:** Santiago Cajamarca</li><li>**Aprobado por:** Juan Esteban Rodríguez</li><li>**Fecha:** 19/03/2026</li><li>**Cambios:** Reconstrucción de requerimiento; definición de flujos de consulta y reglas de privacidad de agenda.</li></ul> |


### RF10: Tabla de Posiciones

| Campo                     | Detalle |
|:--------------------------|:--------|
| **Código**                | RF10 |
| **Nombre**                | Tabla de Posiciones |
| **Descripción**           | El sistema debe procesar y mostrar la clasificación de los equipos basándose en los resultados validados. Incluye el cálculo de partidos jugados (PJ), ganados (PG), empatados (PE), perdidos (PP), goles a favor (GF), goles en contra (GC), diferencia de gol (DG) y puntos totales (PTS). |
| **Cómo se ejecutará**     | A través de un módulo de visualización dinámica de datos, accesible para cualquier usuario vinculado al torneo desde la interfaz pública o privada. |
| **Actor principal**       | Usuario Autenticado |
| **Precondiciones**        | 1) El torneo debe estar registrado y contar con equipos inscritos. <br> 2) El usuario debe haber iniciado sesión para acceder a las estadísticas detalladas. |

**DATOS DE ENTRADA:**

| Nombre | Descripción | Tipo de campo | Reglas / Aplicación | Obligatorio |
|:-------|:------------|:--------------|:--------------------|:------------|
| Torneo ID | Identificador del torneo | UUID | Se obtiene del contexto de navegación actual | Sí |
| Filtro de Fase | Etapa del torneo | Selección | Permite segmentar la tabla por grupos o fases | No |

**DATOS DE SALIDA:**

| Nombre | Descripción | Tipo de campo | Reglas / Aplicación | Obligatorio |
|:-------|:------------|:--------------|:--------------------|:------------|
| Ranking General | Lista ordenada | Tabla/Componente | Orden descendente por PTS y criterios de desempate | Sí |
| Estadísticas de Equipo | Ficha técnica | Objeto JSON | Desglose de PJ, PG, PE, PP, GF, GC, DG, PTS | Sí |

**FLUJO BÁSICO:**

| Paso | Actor | Descripción | Excepciones |
|:-----|:------|:------------|:------------|
| 1 | Usuario | Navega hacia la sección de "Estadísticas" o "Tabla de Posiciones" del torneo seleccionado. | — |
| 2 | Usuario | Selecciona la fase o el grupo específico que desea consultar para filtrar los datos. | — |
| 3 | Usuario | Visualiza el ranking actualizado con los cálculos de rendimiento de cada equipo. | E1: Sin datos |
| 4 | Usuario | Consulta el detalle expandido de un equipo para verificar su historial de goles y diferencia. | — |

**FLUJO ALTERNO:**

| Paso | Actor | Descripción | Excepciones |
|:-----|:------|:------------|:------------|
| E1 | Usuario | Accede a la tabla antes de que existan partidos finalizados y observa a los equipos con valores en cero. | — |
| A1 | Usuario | Recibe la actualización de los valores en pantalla tras el cierre de un acta de arbitraje (RF08). | — |
| A2 | Usuario | Utiliza el buscador para localizar la posición y estadísticas de un equipo específico rápidamente. | — |

| Sección | Detalle |
| :--- | :--- |
| **Reglas de Negocio** | <ul><li>1) **Puntuación:** Victoria (3 pts), Empate (1 pt), Derrota (0 pts).</li><li>2) **Desempate:** En caso de igualdad en puntos, el orden es: 1º Diferencia de Gol, 2º Goles a Favor, 3º Duelo Directo.</li><li>3) **Fórmula de Integridad:** Se debe cumplir siempre que $PJ = PG + PE + PP$.</li><li>4) **Actualización:** El sistema emplea el patrón **Observer** para recalcular los datos inmediatamente después de que un árbitro confirma el acta (RF08).</li></ul> |
| **Anexos** | **Prototipos:** Mockup de Tabla de Clasificación Dinámica. <br> **Abreviaturas:** PJ (Jugados), PG (Ganados), PE (Empatados), PP (Perdidos), GF (Goles Favor), GC (Goles Contra), DG (Diferencia), PTS (Puntos). <br><br> **Caso de Uso:** <br> <img width="875" height="352" alt="image" src="https://github.com/user-attachments/assets/deeb6f6e-b731-4fc0-a7be-e1dbb383b29d" />|
| **Historial de Revisión** | <ul><li>**Elaborado por:** Santiago Cajamarca</li><li>**Aprobado por:** Juan Esteban Rodríguez</li><li>**Fecha:** 19/03/2026</li><li>**Cambios:** Reconstrucción de requerimiento; depuración de actores en flujo y lógica de desempate técnica.</li></ul> |

### RF11: Llaves Eliminatorias

| Campo                     | Detalle |
|:--------------------------|:--------|
| **Código**                | RF11 |
| **Nombre**                | Llaves Eliminatorias |
| **Descripción**           | El sistema debe generar automáticamente el cuadro de eliminación directa del torneo, incluyendo la creación de los partidos iniciales de manera aleatoria y la progresión por fases: cuartos de final, semifinal y final. El bracket se actualiza conforme se registren los resultados, avanzando automáticamente a los ganadores. |
| **Cómo se ejecutará**     | Mediante un módulo del torneo donde el organizador activa la generación del bracket y el sistema construye las llaves; luego el sistema avanza automáticamente los equipos ganadores. |
| **Actor principal**       | Organizador |
| **Precondiciones**        | 1) El usuario debe estar autenticado con rol de Organizador. <br> 2) Debe existir un torneo en estado Activo o En Progreso. <br> 3) Deben existir los equipos clasificados para eliminación directa. <br> 4) Debe estar definida la cantidad de equipos que ingresan a llaves. |
| **Reglas de Negocio**     | 1) Los partidos iniciales se generan de manera aleatoria. <br> 2) El sistema genera y mantiene las fases: cuartos de final, semifinal y final. <br> 3) El ganador de un partido avanza automáticamente a la ### RF10: Tabla de Posiciones

| Campo                     | Detalle |
|:--------------------------|:--------|
| **Código**                | RF10 |
| **Nombre**                | Tabla de Posiciones |
| **Descripción**           | El sistema debe procesar y mostrar la clasificación de los equipos basándose en los resultados validados. Incluye el cálculo de partidos jugados (PJ), ganados (PG), empatados (PE), perdidos (PP), goles a favor (GF), goles en contra (GC), diferencia de gol (DG) y puntos totales (PTS). |
| **Cómo se ejecutará**     | A través de un módulo de visualización dinámica de datos, accesible para cualquier usuario vinculado al torneo desde la interfaz pública o privada. |
| **Actor principal**       | Usuario Autenticado |
| **Precondiciones**        | 1) El torneo debe estar registrado y contar con equipos inscritos. <br> 2) El usuario debe haber iniciado sesión para acceder a las estadísticas detalladas. |

**DATOS DE ENTRADA:**

| Nombre | Descripción | Tipo de campo | Reglas / Aplicación | Obligatorio |
|:-------|:------------|:--------------|:--------------------|:------------|
| Torneo ID | Identificador del torneo | UUID | Se obtiene del contexto de navegación actual | Sí |
| Filtro de Fase | Etapa del torneo | Selección | Permite segmentar la tabla por grupos o fases | No |

**DATOS DE SALIDA:**

| Nombre | Descripción | Tipo de campo | Reglas / Aplicación | Obligatorio |
|:-------|:------------|:--------------|:--------------------|:------------|
| Ranking General | Lista ordenada | Tabla/Componente | Orden descendente por PTS y criterios de desempate | Sí |
| Estadísticas de Equipo | Ficha técnica | Objeto JSON | Desglose de PJ, PG, PE, PP, GF, GC, DG, PTS | Sí |

**FLUJO BÁSICO:**

| Paso | Actor | Descripción | Excepciones |
|:-----|:------|:------------|:------------|
| 1 | Usuario | Navega hacia la sección de "Estadísticas" o "Tabla de Posiciones" del torneo seleccionado. | — |
| 2 | Usuario | Selecciona la fase o el grupo específico que desea consultar para filtrar los datos. | — |
| 3 | Usuario | Visualiza el ranking actualizado con los cálculos de rendimiento de cada equipo. | E1: Sin datos |
| 4 | Usuario | Consulta el detalle expandido de un equipo para verificar su historial de goles y diferencia. | — |

**FLUJO ALTERNO:**

| Paso | Actor | Descripción | Excepciones |
|:-----|:------|:------------|:------------|
| E1 | Usuario | Accede a la tabla antes de que existan partidos finalizados y observa a los equipos con valores en cero. | — |
| A1 | Usuario | Recibe la actualización de los valores en pantalla tras el cierre de un acta de arbitraje (RF08). | — |
| A2 | Usuario | Utiliza el buscador para localizar la posición y estadísticas de un equipo específico rápidamente. | — |

| Sección | Detalle |
| :--- | :--- |
| **Reglas de Negocio** | <ul><li>1) **Puntuación:** Victoria (3 pts), Empate (1 pt), Derrota (0 pts).</li><li>2) **Desempate:** En caso de igualdad en puntos, el orden es: 1º Diferencia de Gol, 2º Goles a Favor, 3º Duelo Directo.</li><li>3) **Fórmula de Integridad:** Se debe cumplir siempre que $PJ = PG + PE + PP$.</li><li>4) **Actualización:** El sistema emplea el patrón **Observer** para recalcular los datos inmediatamente después de que un árbitro confirma el acta (RF08).</li></ul> |
| **Anexos** | **Prototipos:** Mockup de Tabla de Clasificación Dinámica. <br> **Abreviaturas:** PJ (Jugados), PG (Ganados), PE (Empatados), PP (Perdidos), GF (Goles Favor), GC (Goles Contra), DG (Diferencia), PTS (Puntos). <br><br> **Caso de Uso:** <br> ![Diagrama de Caso de Uso](https://github.com/user-attachments/assets/2c6a483f-16f0-4a38-bd5c-e780dd2dc764) |
| **Historial de Revisión** | <ul><li>**Elaborado por:** Santiago Cajamarca</li><li>**Aprobado por:** Juan Esteban Rodríguez</li><li>**Fecha:** 19/03/2026</li><li>**Cambios:** Reconstrucción de requerimiento; depuración de actores en flujo y lógica de desempate técnica.</li></ul> |siguiente ronda. <br> 4) Una vez publicada la llave, los emparejamientos no cambian salvo acción administrativa controlada. <br> 5) No se puede regenerar el bracket si ya existen resultados registrados. |
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
| RNF01 | Rendimiento | El sistema debe responder a las consultas en menos de 3 segundos bajo condiciones normales de uso (hasta 100 usuarios concurrentes). | El 95% de las peticiones responden en menos de 3 segundos. |
| RNF02 | Usabilidad | La plataforma debe ser intuitiva para estudiantes universitarios sin necesidad de capacitación previa, con navegación clara y mensajes de error comprensibles. | Un usuario nuevo puede completar el flujo de registro y creación de perfil en menos de 5 minutos. |
| RNF03 | Mantenibilidad | El sistema debe implementar patrones de diseño (Strategy, State, Factory Method, Observer) que faciliten la extensión y modificación del código sin afectar funcionalidades existentes. | Se pueden agregar nuevas reglas de validación, estados o tipos de usuario sin modificar clases existentes. |
