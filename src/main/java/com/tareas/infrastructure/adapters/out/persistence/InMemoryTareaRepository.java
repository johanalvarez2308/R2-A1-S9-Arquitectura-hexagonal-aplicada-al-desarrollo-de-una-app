package com.tareas.infrastructure.adapters.out.persistence;

import com.tareas.domain.model.EstadoTarea;
import com.tareas.domain.model.Tarea;
import com.tareas.domain.ports.TareaRepository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Adaptador de salida: repositorio en memoria.
 *
 * Implementa el puerto de salida TareaRepository usando un simple HashMap.
 * Es el punto de conexión entre el dominio y el mecanismo de persistencia.
 * Puede reemplazarse por un adaptador JDBC, JPA o archivo JSON sin tocar
 * el dominio ni los casos de uso.
 */
public class InMemoryTareaRepository implements TareaRepository {

    private final Map<String, Tarea> almacen = new LinkedHashMap<>();

    @Override
    public void guardar(Tarea tarea) {
        almacen.put(tarea.getId(), tarea);
    }

    @Override
    public Optional<Tarea> buscarPorId(String id) {
        return Optional.ofNullable(almacen.get(id));
    }

    @Override
    public List<Tarea> listarTodas() {
        return new ArrayList<>(almacen.values());
    }

    @Override
    public List<Tarea> listarPendientes() {
        return almacen.values().stream()
                .filter(Tarea::esPendiente)
                .collect(Collectors.toList());
    }

    @Override
    public void eliminar(String id) {
        if (!almacen.containsKey(id)) {
            throw new IllegalArgumentException("No existe tarea con id: " + id);
        }
        almacen.remove(id);
    }
}
