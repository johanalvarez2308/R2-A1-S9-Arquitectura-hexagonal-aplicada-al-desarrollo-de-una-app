package com.tareas.domain.ports;

import com.tareas.domain.model.Tarea;

import java.util.List;

/**
 * Puerto de entrada: contrato que expone los casos de uso al exterior.
 * Los adaptadores de entrada (CLI, REST, etc.) dependen de esta interfaz.
 */
public interface TareaService {

    /** Caso de uso: registrar una nueva tarea académica. */
    Tarea registrarTarea(String titulo, String descripcion);

    /** Caso de uso: listar todas las tareas (pendientes y completadas). */
    List<Tarea> listarTareas();

    /** Caso de uso: listar únicamente las tareas pendientes. */
    List<Tarea> listarPendientes();

    /** Caso de uso: marcar una tarea como completada. */
    Tarea completarTarea(String id);

    /** Caso de uso: consultar el detalle de una tarea por su id. */
    Tarea consultarTarea(String id);

    /** Caso de uso: archivar (eliminar del flujo activo) una tarea. */
    void archivarTarea(String id);

    /** Caso de uso: eliminar definitivamente una tarea. */
    void eliminarTarea(String id);
}
