package com.tareas.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad del dominio: Tarea Académica
 * Encapsula el estado y las reglas de negocio propias de una tarea.
 */
public class Tarea {

    private final String id;
    private String titulo;
    private String descripcion;
    private EstadoTarea estado;
    private final LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    // Constructor privado — se crea mediante factory method
    private Tarea(String id, String titulo, String descripcion, LocalDateTime fechaCreacion) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.estado = EstadoTarea.PENDIENTE;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaCreacion;
    }

    /**
     * Factory method que aplica las reglas de negocio al crear una tarea.
     * Regla: el título no puede ser nulo ni vacío.
     */
    public static Tarea crear(String titulo, String descripcion) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("El título de la tarea no puede estar vacío.");
        }
        String id = UUID.randomUUID().toString();
        return new Tarea(id, titulo.trim(), descripcion != null ? descripcion.trim() : "", LocalDateTime.now());
    }

    /**
     * Constructor para reconstruir una tarea desde persistencia.
     */
    public static Tarea reconstruir(String id, String titulo, String descripcion,
                                     EstadoTarea estado, LocalDateTime fechaCreacion,
                                     LocalDateTime fechaActualizacion) {
        Tarea tarea = new Tarea(id, titulo, descripcion, fechaCreacion);
        tarea.estado = estado;
        tarea.fechaActualizacion = fechaActualizacion;
        return tarea;
    }

    /**
     * Regla de negocio: marcar como completada.
     * Una tarea ya completada no vuelve a cambiar de estado.
     */
    public void completar() {
        if (this.estado == EstadoTarea.COMPLETADA) {
            throw new IllegalStateException("La tarea ya se encuentra completada.");
        }
        this.estado = EstadoTarea.COMPLETADA;
        this.fechaActualizacion = LocalDateTime.now();
    }

    /**
     * Regla de negocio: archivar la tarea.
     */
    public void archivar() {
        if (this.estado == EstadoTarea.ARCHIVADA) {
            throw new IllegalStateException("La tarea ya se encuentra archivada.");
        }
        this.estado = EstadoTarea.ARCHIVADA;
        this.fechaActualizacion = LocalDateTime.now();
    }

    public boolean esPendiente() {
        return this.estado == EstadoTarea.PENDIENTE;
    }

    public boolean esCompletada() {
        return this.estado == EstadoTarea.COMPLETADA;
    }

    // ── Getters ──────────────────────────────────────────────────────────────
    public String getId()                        { return id; }
    public String getTitulo()                    { return titulo; }
    public String getDescripcion()               { return descripcion; }
    public EstadoTarea getEstado()               { return estado; }
    public LocalDateTime getFechaCreacion()      { return fechaCreacion; }
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }

    @Override
    public String toString() {
        return String.format("[%s] (%s) %s — %s",
                id.substring(0, 8), estado, titulo,
                descripcion.isEmpty() ? "sin descripción" : descripcion);
    }
}
