package com.tareas.application.usecases;

import com.tareas.domain.model.Tarea;
import com.tareas.domain.ports.TareaRepository;
import com.tareas.domain.ports.TareaService;

import java.util.List;

/**
 * Implementación de los casos de uso de la aplicación.
 *
 * Esta clase pertenece a la capa de aplicación: orquesta las entidades del dominio
 * y delega la persistencia al puerto de salida (TareaRepository), sin depender
 * de ningún framework ni detalle de infraestructura.
 */
public class TareaServiceImpl implements TareaService {

    private final TareaRepository repository;

    public TareaServiceImpl(TareaRepository repository) {
        this.repository = repository;
    }

    // ── Caso de uso: Registrar tarea ─────────────────────────────────────────
    @Override
    public Tarea registrarTarea(String titulo, String descripcion) {
        // La entidad aplica su propia validación (título no vacío)
        Tarea tarea = Tarea.crear(titulo, descripcion);
        repository.guardar(tarea);
        System.out.println("✔ Tarea registrada: " + tarea.getId().substring(0, 8));
        return tarea;
    }

    // ── Caso de uso: Listar todas las tareas ─────────────────────────────────
    @Override
    public List<Tarea> listarTareas() {
        return repository.listarTodas();
    }

    // ── Caso de uso: Listar tareas pendientes ────────────────────────────────
    @Override
    public List<Tarea> listarPendientes() {
        return repository.listarPendientes();
    }

    // ── Caso de uso: Completar tarea ─────────────────────────────────────────
    @Override
    public Tarea completarTarea(String id) {
        Tarea tarea = obtenerOFallar(id);
        tarea.completar(); // La entidad aplica la regla de negocio
        repository.guardar(tarea);
        return tarea;
    }

    // ── Caso de uso: Consultar tarea por id ──────────────────────────────────
    @Override
    public Tarea consultarTarea(String id) {
        return obtenerOFallar(id);
    }

    // ── Caso de uso: Archivar tarea ──────────────────────────────────────────
    @Override
    public void archivarTarea(String id) {
        Tarea tarea = obtenerOFallar(id);
        tarea.archivar();
        repository.guardar(tarea);
    }

    // ── Caso de uso: Eliminar tarea ──────────────────────────────────────────
    @Override
    public void eliminarTarea(String id) {
        obtenerOFallar(id); // Verifica existencia antes de borrar
        repository.eliminar(id);
    }

    // ── Helper privado ───────────────────────────────────────────────────────
    private Tarea obtenerOFallar(String id) {
        return repository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró una tarea con id: " + id));
    }
}
