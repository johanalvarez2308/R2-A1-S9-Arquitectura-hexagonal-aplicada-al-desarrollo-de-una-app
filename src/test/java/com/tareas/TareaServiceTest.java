package com.tareas;

import com.tareas.application.usecases.TareaServiceImpl;
import com.tareas.domain.model.EstadoTarea;
import com.tareas.domain.model.Tarea;
import com.tareas.domain.ports.TareaRepository;
import com.tareas.domain.ports.TareaService;
import com.tareas.infrastructure.adapters.out.persistence.InMemoryTareaRepository;

import java.util.List;

/**
 * Pruebas funcionales de los casos de uso y reglas de negocio.
 * Se ejecutan sin framework de testing para no requerir dependencias externas.
 */
public class TareaServiceTest {

    private static int exitosos = 0;
    private static int fallidos = 0;

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║   PRUEBAS FUNCIONALES — DOMINIO      ║");
        System.out.println("╚══════════════════════════════════════╝\n");

        testRegistrarTareaConTituloValido();
        testRegistrarTareaConTituloVacio();
        testListarTareas();
        testListarPendientesSoloMuestraPendientes();
        testCompletarTarea();
        testCompletarTareaYaCompletadaFalla();
        testConsultarTareaExistente();
        testConsultarTareaInexistenteFalla();
        testArchivarTarea();
        testEliminarTarea();
        testEliminarTareaInexistenteFalla();

        System.out.println("\n──────────────────────────────────────");
        System.out.printf("Resultado: %d exitosas | %d fallidas%n", exitosos, fallidos);
        System.out.println(fallidos == 0 ? "✔ TODAS LAS PRUEBAS PASARON" : "✘ EXISTEN FALLAS");
    }

    // ── Tests ────────────────────────────────────────────────────────────────

    static void testRegistrarTareaConTituloValido() {
        TareaService service = nuevoServicio();
        Tarea t = service.registrarTarea("Leer capítulo 3", "POO en Java");
        afirmar("Registrar tarea con título válido",
                t != null && t.getTitulo().equals("Leer capítulo 3")
                && t.getEstado() == EstadoTarea.PENDIENTE);
    }

    static void testRegistrarTareaConTituloVacio() {
        TareaService service = nuevoServicio();
        afirmarExcepcion("Registrar tarea con título vacío lanza excepción",
                IllegalArgumentException.class,
                () -> service.registrarTarea("  ", null));
    }

    static void testListarTareas() {
        TareaService service = nuevoServicio();
        service.registrarTarea("Tarea A", "");
        service.registrarTarea("Tarea B", "");
        afirmar("Listar todas devuelve 2 tareas", service.listarTareas().size() == 2);
    }

    static void testListarPendientesSoloMuestraPendientes() {
        TareaService service = nuevoServicio();
        Tarea t1 = service.registrarTarea("Pendiente", "");
        Tarea t2 = service.registrarTarea("A completar", "");
        service.completarTarea(t2.getId());
        List<Tarea> pendientes = service.listarPendientes();
        afirmar("Listar pendientes devuelve solo las pendientes",
                pendientes.size() == 1 && pendientes.get(0).getId().equals(t1.getId()));
    }

    static void testCompletarTarea() {
        TareaService service = nuevoServicio();
        Tarea t = service.registrarTarea("Entregar práctica", "");
        Tarea completada = service.completarTarea(t.getId());
        afirmar("Completar tarea cambia su estado a COMPLETADA",
                completada.getEstado() == EstadoTarea.COMPLETADA);
    }

    static void testCompletarTareaYaCompletadaFalla() {
        TareaService service = nuevoServicio();
        Tarea t = service.registrarTarea("Ya completada", "");
        service.completarTarea(t.getId());
        afirmarExcepcion("Completar tarea ya completada lanza excepción",
                IllegalStateException.class,
                () -> service.completarTarea(t.getId()));
    }

    static void testConsultarTareaExistente() {
        TareaService service = nuevoServicio();
        Tarea t = service.registrarTarea("Consultar esta", "desc");
        Tarea encontrada = service.consultarTarea(t.getId());
        afirmar("Consultar tarea existente la devuelve correctamente",
                encontrada.getId().equals(t.getId()));
    }

    static void testConsultarTareaInexistenteFalla() {
        TareaService service = nuevoServicio();
        afirmarExcepcion("Consultar id inexistente lanza excepción",
                IllegalArgumentException.class,
                () -> service.consultarTarea("id-que-no-existe"));
    }

    static void testArchivarTarea() {
        TareaService service = nuevoServicio();
        Tarea t = service.registrarTarea("Archivar esta", "");
        service.archivarTarea(t.getId());
        Tarea archivada = service.consultarTarea(t.getId());
        afirmar("Archivar tarea cambia su estado a ARCHIVADA",
                archivada.getEstado() == EstadoTarea.ARCHIVADA);
    }

    static void testEliminarTarea() {
        TareaService service = nuevoServicio();
        Tarea t = service.registrarTarea("Eliminar esta", "");
        service.eliminarTarea(t.getId());
        afirmar("Eliminar tarea la quita del listado",
                service.listarTareas().isEmpty());
    }

    static void testEliminarTareaInexistenteFalla() {
        TareaService service = nuevoServicio();
        afirmarExcepcion("Eliminar id inexistente lanza excepción",
                IllegalArgumentException.class,
                () -> service.eliminarTarea("no-existe"));
    }

    // ── Helpers de aserción ──────────────────────────────────────────────────

    static TareaService nuevoServicio() {
        TareaRepository repo = new InMemoryTareaRepository();
        return new TareaServiceImpl(repo);
    }

    static void afirmar(String nombre, boolean condicion) {
        if (condicion) {
            System.out.println("  ✔ " + nombre);
            exitosos++;
        } else {
            System.out.println("  ✘ FALLO: " + nombre);
            fallidos++;
        }
    }

    static void afirmarExcepcion(String nombre, Class<? extends Throwable> tipo, Runnable accion) {
        try {
            accion.run();
            System.out.println("  ✘ FALLO (no lanzó excepción): " + nombre);
            fallidos++;
        } catch (Throwable e) {
            if (tipo.isInstance(e)) {
                System.out.println("  ✔ " + nombre);
                exitosos++;
            } else {
                System.out.println("  ✘ FALLO (excepción incorrecta " + e.getClass().getSimpleName() + "): " + nombre);
                fallidos++;
            }
        }
    }
}
