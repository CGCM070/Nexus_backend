# Relaciones entre Entidades

## User - Server
**Relación:** Muchos a muchos.  
**Tabla intermedia:** `user_servers`.  
**Descripción:** Un usuario puede estar en varios servidores y un servidor puede tener varios usuarios.

## Server - Channel
**Relación:** Uno a muchos.  
**Descripción:** Un servidor puede tener muchos canales.

## Channel - Message
**Relación:** Uno a muchos.  
**Descripción:** Un canal puede contener muchos mensajes.

## User - Message
**Relación:** Uno a muchos.  
**Descripción:** Un usuario puede crear muchos mensajes.

## User - Note
**Relación:** Uno a muchos.  
**Descripción:** Un usuario puede crear muchas notas.

## Channel - Note
**Relación:** Uno a muchos.  
**Descripción:** Un canal puede contener muchas notas.

## User - Task
**Relación:** Dos relaciones uno a muchos (creador y asignado).  
**Descripción:** Un usuario puede crear muchas tareas y un usuario puede estar asignado a muchas tareas.

## Channel - Task
**Relación:** Uno a muchos.  
**Descripción:** Un canal puede contener muchas tareas.

## User - Invitation
**Relación:** Uno a muchos (como invitador).  
**Descripción:** Un usuario puede invitar a muchas personas.

## Server - Invitation
**Relación:** Uno a muchos.  
**Descripción:** Un servidor puede tener muchas invitaciones.  
