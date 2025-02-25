
### Nexus `Cesar Castillo`
# Flujo de la Aplicación y Propuesta Inicial de Entrada

## 1. **Estructura de la Base de Datos**

- **User**: Centro de la aplicación, con relaciones a mensajes, notas, tareas y servidores.
- **Server**: Representa un espacio de trabajo, contiene canales y usuarios.
- **Channel**: Pertenece a un servidor, contiene mensajes, notas y tareas.
- **Message**, **Note**, **Task**: Contenido creado por usuarios en canales específicos.
- **Invitation**: Maneja invitaciones a servidores.

## 2. **Flujo Actual de la Aplicación**

1. Los usuarios pueden pertenecer a múltiples servidores.
2. Cada servidor tiene múltiples canales.
3. Los usuarios interactúan en canales enviando mensajes, creando notas y tareas.
4. Las invitaciones permiten a los usuarios unirse a nuevos servidores.

## 3. **Propuesta para el Flujo de Entrada Inicial**

### a) **Registro de Usuario**

1. Crear una nueva entrada en la tabla `User`.

### b) **Creación del Dashboard Personal**

1. Crear un nuevo `Server` para el usuario con un nombre como "Dashboard Personal de [Username]".
2. Establecer una relación entre el usuario y este servidor en `user_servers`.

### c) **Canal de Bienvenida**

1. Crear un nuevo `Channel` llamado "Bienvenida" en el servidor personal.
2. Añadir un mensaje de bienvenida en este canal.

### d) **Creación de Canales por el Usuario**

- Implementar un método para crear nuevos canales en el servidor personal.

### e) **Invitación de Usuarios**

-  la clase `Invitation`  para manejar invitaciones a canales específicos.
- Añadir un campo `channel` a la clase `Invitation` si es necesario.

### f) **Aceptación de Invitaciones**

- Implementar un método para procesar la aceptación de invitaciones, añadiendo al usuario al servidor y canal correspondientes.

### Enfoque para la Entrada Inicial

1. Crear un espacio personal para cada nuevo usuario.
2. Proporcionar un canal de bienvenida automático.
3. Permitir a los usuarios crear sus propios canales.
4. Invitar a otros usuarios por correo electrónico.
5. Procesar la aceptación de invitaciones.

---

## 4. **Relaciones entre Entidades**

### **User - Server**
- **Relación**: Muchos a muchos.
- **Tabla intermedia**: `user_servers`.
- **Descripción**: Un usuario puede estar en varios servidores y un servidor puede tener varios usuarios.

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

### **User - Invitation**
- **Relación**: Uno a muchos (como invitador).
- **Descripción**: Un usuario puede invitar a muchas personas.

### **Server - Invitation**
- **Relación**: Uno a muchos.
- **Descripción**: Un servidor puede tener muchas invitaciones.
