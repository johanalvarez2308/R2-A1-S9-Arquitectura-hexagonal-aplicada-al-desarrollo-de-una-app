package com.tareas.domain.ports;

import com.tareas.domain.model.Tarea;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida: contrato que el dominio exige para persistencia.
 * La infraestructura (adaptadores de salida) implementa esta interfaz.
 */
public interface TareaRepository {

    /** Persiste una tarea nueva o actualiza una existente. */
    void guardar(Tarea tarea);

    /** Busca una tarea por su identificador único. */
    Optional<Tarea> buscarPorId(String id);

    /** Devuelve todas las tareas registradas. */
    List<Tarea> listarTodas();

    /** Devuelve únicamente las tareas en estado PENDIENTE. */
    List<Tarea> listarPendientes();

    /** Elimina físicamente una tarea del repositorio. */
    void eliminar(String id);
}
