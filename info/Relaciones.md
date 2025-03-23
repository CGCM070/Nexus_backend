### Nexus `Cesar Castillo`
# Flujo de la Aplicación y Propuesta Inicial de Entrada

## 1. **Estructura de la Base de Datos**

- **User**: Centro de la aplicación, con relaciones a mensajes, notas, tareas, un servidor personal y canales a los que ha sido invitado.
- **Server**: Representa un espacio de trabajo, contiene canales y un usuario.
- **Channel**: Pertenece a un servidor, contiene mensajes, notas, tareas y usuarios invitados.
- **Message**, **Note**, **Task**: Contenido creado por usuarios en canales específicos.
- **Invitation**: Maneja invitaciones a servidores.
- **ChannelUserRole**: Gestiona los roles específicos de los usuarios en cada canal.

## 2. **Flujo Actual de la Aplicación**

1. Cada usuario tiene un servidor personal.
2. Cada servidor tiene múltiples canales.
3. Los usuarios interactúan en canales enviando mensajes, creando notas y tareas.
4. Las invitaciones permiten a los usuarios unirse a nuevos servidores.
5. Cada usuario tiene un rol específico en cada canal (OWNER, ADMIN, MEMBER).

## 3. **Propuesta para el Flujo de Entrada Inicial**

### a) **Registro de Usuario**

1. Crear una nueva entrada en la tabla `User`.
2. Asignar roles globales (ROL_USER, opcionalmente ROL_ADMIN).

### b) **Creación del Dashboard Personal**

1. Crear un nuevo `Server` para el usuario con un nombre como "Dashboard Personal de [Username]".
2. Establecer una relación entre el usuario y este servidor.

### c) **Canal de Bienvenida**

1. Crear un nuevo `Channel` llamado "Bienvenida" en el servidor personal.
2. Añadir un mensaje de bienvenida en este canal.
3. Asignar al usuario el rol OWNER en este canal.

### d) **Creación de Canales por el Usuario**

- Implementar un método para crear nuevos canales en el servidor personal.
- El creador recibe automáticamente el rol OWNER en el nuevo canal.

### e) **Invitación de Usuarios**

- Utilizar la clase `Invitation` para manejar invitaciones a servidores específicos.
- Al invitar a un usuario, se le asigna un rol específico (por defecto MEMBER).

### f) **Aceptación de Invitaciones**

- Implementar un método para procesar la aceptación de invitaciones, añadiendo al usuario al servidor correspondiente.
- Crear la relación ChannelUserRole con el rol asignado.

## 4. **Relaciones entre Entidades**

### **User - Server**
- **Relación**: Uno a uno.
- **Descripción**: Un usuario tiene un servidor personal.

### **Server - Channel**
- **Relación**: Uno a muchos.
- **Descripción**: Un servidor puede tener muchos canales.

### **Channel - Message**
- **Relación**: Uno a muchos.
- **Descripción**: Un canal puede contener muchos mensajes.

### **User - Message**
- **Relación**: Uno a muchos.
- **Descripción**: Un usuario puede crear muchos mensajes.

### **User - Note**
- **Relación**: Uno a muchos.
- **Descripción**: Un usuario puede crear muchas notas.

### **Channel - Note**
- **Relación**: Uno a muchos.
- **Descripción**: Un canal puede contener muchas notas.

### **User - Task**
- **Relación**: Dos relaciones uno a muchos (creador y asignado).
- **Descripción**: Un usuario puede crear muchas tareas y un usuario puede estar asignado a muchas tareas.

### **Channel - Task**
- **Relación**: Uno a muchos.
- **Descripción**: Un canal puede contener muchas tareas.

### **User - Channel**
- **Relación**: Muchos a muchos.
- **Descripción**: Un usuario puede ser invitado a muchos canales y un canal puede tener muchos usuarios invitados.

### **User - Channel - Role**
- **Relación**: Uno a muchos a uno.
- **Descripción**: Un usuario tiene un rol específico en cada canal al que pertenece.
- **Entidad**: ChannelUserRole

### **User - Invitation**
- **Relación**: Uno a muchos (como invitado).
- **Descripción**: Un usuario puede recibir muchas invitaciones.

### **Server - Invitation**
- **Relación**: Uno a muchos.
- **Descripción**: Un servidor puede tener muchas invitaciones.

## 5. **Sistema de Seguridad**

### **Roles Globales (ERol)**
- **ROL_ADMIN**: Administrador de la plataforma con acceso a funcionalidades administrativas.
- **ROL_USER**: Usuario estándar de la plataforma.

### **Roles Contextuales (EChannelRole)**
- **OWNER**: Propietario del canal con control total.
- **ADMIN**: Administrador con permisos para gestionar el canal.
- **MEMBER**: Usuario con acceso básico al canal.

### **SecurityService**
Proporciona métodos para verificar los permisos:
- `canManageChannel`: Verifica si un usuario puede administrar un canal (OWNER, ADMIN).
- `canAccessChannel`: Verifica si un usuario puede acceder a un canal (cualquier rol).
- `canModifyResource`: Verifica si un usuario puede modificar un recurso específico (notas, mensajes).
- `isServerOwner`: Verifica si un usuario es el propietario de un servidor.

### **Asignación Automática de Roles**
- Al registrar un usuario, se le asigna automáticamente el rol OWNER en su canal de bienvenida.
- Al crear un canal, el creador recibe el rol OWNER.
- Al invitar a un usuario, se le asigna un rol específico (por defecto MEMBER).

### **Beneficios del Sistema de Seguridad Contextual**
- Permite granularidad fina de permisos por canal.
- Un administrador global no tiene acceso automático a todos los canales.
- Respeta la privacidad de los espacios personales de los usuarios.
- Permite diferentes niveles de privilegios en diferentes contextos.