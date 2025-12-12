package com.javafleet.personmanagement.entity;

/**
 * User Roles für Authorization
 * 
 * Definiert die verfügbaren Rollen im System:
 * - USER: Standard-User mit Basiszugriff
 * - ADMIN: Administrator mit vollem Zugriff
 * - MODERATOR: Moderator mit erweiterten Rechten
 * 
 * @author Elyndra Valen
 */
public enum UserRole {
    USER,
    ADMIN,
    MODERATOR
}
