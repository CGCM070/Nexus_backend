### Relaciones entre Entidades

1. **User - Server**: Muchos a muchos a través de `user_servers`.
2. **Server - Channel**: Uno a muchos.
3. **Channel - Message**: Uno a muchos.
4. **User - Message**: Uno a muchos.
5. **User - Note**: Uno a muchos.
6. **Channel - Note**: Uno a muchos.
7. **User - Task**: Dos relaciones uno a muchos (creador y asignado).
8. **Channel - Task**: Uno a muchos.
9. **User - Invitation**: Uno a muchos (como invitador).
10. **Server - Invitation**: Uno a muchos.