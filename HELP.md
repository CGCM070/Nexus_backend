## Estructura Principal del Sistema
***Cesar Castillo***       
***Nexus*** es una plataforma de colaboración y comunicación similar a Discord o Slack, con algunas características interesantes.

### Arquitectura del Dominio

    User (1) ──┬── (1) Server (personal) ──── (N) Channel
               │
               └── (M) Channel (invitado, vía ChannelUserRole)

**User:** Entidad central con servidor personal y pertenencia a múltiples canales.  
**Server:** Espacio personal para cada usuario con múltiples canales.  
**Channel:** Contenedor de comunicación con mensajes, notas y tareas.  
**Message:** Comunicación en tiempo real vía WebSockets/RabbitMQ.  
**Note/Task/Message:** Contenido persistente en canales.

### Nexus implementa un sofisticado sistema de seguridad con dos niveles:

**Autenticación JWT**
- Filtrado de peticiones mediante `JwtAuthenticationFilter`.
- Generación y validación de tokens en `JwtService`.
- Almacenamiento de roles y permisos en el token.

**Autorización Contextual**
- Roles globales: `ROL_ADMIN`, `ROL_USER` (a nivel de plataforma).
- Roles por canal: `OWNER`, `ADMIN`, `MEMBER` (permisos específicos por contexto).
- Verificación mediante `SecurityService`:

```java
public boolean canManageChannel(Long channelId) {
    // Verifica si usuario es OWNER o ADMIN del canal
}

public boolean canAccessChannel(Long channelId) {
    // Verifica acceso básico al canal
}

```
### **SecurityService**
Proporciona métodos para verificar los permisos:
- `canManageChannel`: Verifica si un usuario puede administrar un canal (OWNER, ADMIN).
- `canAccessChannel`: Verifica si un usuario puede acceder a un canal (cualquier rol).
- `canModifyResource`: Verifica si un usuario puede modificar un recurso específico (notas, mensajes).
- `isServerOwner`: Verifica si un usuario es el propietario de un servidor.


### Registro e Inicialización
Usuario se registra → Se crea User → Se crea Server personal. Se crea un Channel de bienvenida → Se asigna rol OWNER.
Se crea una nota de bienvenida,
todo orquestrado por ***UserRegistrationService***.

###  Implementación en UserRegistrationService:

```java
@Transactional
public User registerUser(User newUser) {
    User savedUser = userService.createUser(newUser);
    Server personalServer = serverService.createPersonalServerForUser(savedUser);
    Channel welcomeChannel = channelService.createDefaultChannelForServer(personalServer);

    // Asignar rol OWNER...
    ChannelUserRole ownerRole = ChannelUserRole.builder()
            .user(savedUser)
            .channel(welcomeChannel)
            .role(EChannelRole.OWNER)
            .build();
    channelUserRoleRepository.save(ownerRole);

    noteService.createWelcomeNoteForUser(savedUser, welcomeChannel);
    return savedUser;
}
```

### Sistema de Invitaciones

**Generar invitación** :
Solo OWNER/ADMIN pueden invitar (verificado por SecurityService).
Se genera un token único con fecha de expiración. Se envía un email con el enlace.

**Aceptar invitación**: Verificación de token válido. Verificación de email coincidente. Asignación de rol específico en el canal.

```java
public void acceptInvitation(String token, Long userId) {
    // Verificaciones...
    if (!invitation.getEmail().equals(user.getEmail())) {
        throw new RuntimeException("El correo electrónico no coincide");
    }

    channelService.inviteUserToChannel(
        invitation.getChannel().getId(),
        userId,
        invitation.getRole()
    );
    invitation.setAccepted(true);
}
```
### Comunicación en Tiempo Real

**La arquitectura de mensajería combina**:

- **WebSockets** para comunicación cliente-servidor directa.

- **RabbitMQ** como broker de mensajes para distribución entre instancias.

### Persistencia en base de datos.

````java
@MessageMapping("/channel/{channelId}/send")
public void sendChannelMessage(@DestinationVariable Long channelId, MessageDTO messageDTO) {
    // Verificar permisos, guardar y distribuir mensaje
    Message savedMessage = messageService.saveMessage(user, channel, messageDTO.getContent());
    messageService.sendMessageToChannel(savedMessage);
}
````
### Aspectos Destacables
- **Seguridad contextual**: Permisos específicos por canal y recurso.

- **Onboarding automático**: Creación automática de espacios personales.

- **Integración WebSockets/RabbitMQ**: Comunicación en tiempo real escalable.

- **Separación clara de responsabilidades**: Servicios bien definidos.

- **Transaccionalidad**: Operaciones críticas marcadas con @Transactional.

### El proyecto está estructurado con una arquitectura robusta que implementa conceptos avanzados como:

- Autenticación/autorización multinivel.

- Comunicación en tiempo real.

- Permisos contextuales.

- Persistencia transaccional.
