package com.cambistaonline.auth.domain.events;

import java.time.LocalDateTime;

/**
 * Evento de dominio inmutable publicado cuando un nuevo usuario
 * se registra exitosamente en la plataforma.
 */
public class UserRegisteredEvent {

    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private LocalDateTime occurredAt;

    public UserRegisteredEvent() {
        this.occurredAt = LocalDateTime.now();
    }

    public UserRegisteredEvent(String userId, String email,
                               String firstName, String lastName, String role) {
        this.userId = userId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.occurredAt = LocalDateTime.now();
    }

    public String getUserId()          { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getEmail()           { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstName()       { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName()        { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getRole()            { return role; }
    public void setRole(String role)   { this.role = role; }

    public LocalDateTime getOccurredAt(){ return occurredAt; }
    public void setOccurredAt(LocalDateTime occurredAt) { this.occurredAt = occurredAt; }
}
