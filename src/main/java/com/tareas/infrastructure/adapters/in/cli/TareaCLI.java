package com.tareas.infrastructure.adapters.in.cli;

import com.tareas.domain.model.Tarea;
import com.tareas.domain.ports.TareaService;

import java.util.List;
import java.util.Scanner;

/**
 * Adaptador de entrada: Interfaz de Línea de Comandos (CLI).
 *
 * Traduce las interacciones del usuario en consola a llamadas sobre el
 * puerto de entrada (TareaService). No contiene lógica de negocio.
 */
public class TareaCLI {

    private final TareaService tareaService;
    private final Scanner scanner;

    public TareaCLI(TareaService tareaService) {
        this.tareaService = tareaService;
        this.scanner = new Scanner(System.in);
    }

    public void iniciar() {
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║   GESTOR DE TAREAS ACADÉMICAS  v1.0      ║");
        System.out.println("╚══════════════════════════════════════════╝");

        boolean continuar = true;
        while (continuar) {
            mostrarMenu();
            String opcion = scanner.nextLine().trim();
            System.out.println();
            try {
                switch (opcion) {
                    case "1" -> registrarTarea();
                    case "2" -> listarTodas();
                    case "3" -> listarPendientes();
                    case "4" -> completarTarea();
                    case "5" -> consultarTarea();
                    case "6" -> archivarTarea();
                    case "7" -> eliminarTarea();
                    case "0" -> {
                        System.out.println("¡Hasta luego! Tus tareas han sido guardadas.");
                        continuar = false;
                    }
                    default -> System.out.println("⚠ Opción no válida. Intenta de nuevo.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("✘ Error: " + e.getMessage());
            }
        }
        scanner.close();
    }

    // ── Opciones del menú ────────────────────────────────────────────────────

    private void mostrarMenu() {
        System.out.println("\n┌──────────────────────────────────────────┐");
        System.out.println("│  1. Registrar nueva tarea                │");
        System.out.println("│  2. Listar todas las tareas              │");
        System.out.println("│  3. Listar tareas pendientes             │");
        System.out.println("│  4. Completar tarea                      │");
        System.out.println("│  5. Consultar detalle de tarea           │");
        System.out.println("│  6. Archivar tarea                       │");
        System.out.println("│  7. Eliminar tarea                       │");
        System.out.println("│  0. Salir                                │");
        System.out.println("└──────────────────────────────────────────┘");
        System.out.print("Selecciona una opción: ");
    }

    private void registrarTarea() {
        System.out.print("Título de la tarea: ");
        String titulo = scanner.nextLine().trim();
        System.out.print("Descripción (opcional, Enter para omitir): ");
        String descripcion = scanner.nextLine().trim();
        Tarea tarea = tareaService.registrarTarea(titulo, descripcion);
        System.out.println("✔ Tarea creada exitosamente:");
        imprimirTarea(tarea);
    }

    private void listarTodas() {
        List<Tarea> tareas = tareaService.listarTareas();
        if (tareas.isEmpty()) {
            System.out.println("ℹ No hay tareas registradas.");
            return;
        }
        System.out.println("── Todas las tareas (" + tareas.size() + ") ──────────────────────");
        tareas.forEach(this::imprimirTarea);
    }

    private void listarPendientes() {
        List<Tarea> pendientes = tareaService.listarPendientes();
        if (pendientes.isEmpty()) {
            System.out.println("✔ No hay tareas pendientes. ¡Excelente trabajo!");
            return;
        }
        System.out.println("── Tareas pendientes (" + pendientes.size() + ") ────────────────");
        pendientes.forEach(this::imprimirTarea);
    }

    private void completarTarea() {
        System.out.print("ID de la tarea a completar (primeros 8 caracteres): ");
        String idParcial = scanner.nextLine().trim();
        String idCompleto = resolverIdParcial(idParcial);
        Tarea tarea = tareaService.completarTarea(idCompleto);
        System.out.println("✔ Tarea marcada como completada:");
        imprimirTarea(tarea);
    }

    private void consultarTarea() {
        System.out.print("ID de la tarea (primeros 8 caracteres): ");
        String idParcial = scanner.nextLine().trim();
        String idCompleto = resolverIdParcial(idParcial);
        Tarea tarea = tareaService.consultarTarea(idCompleto);
        System.out.println("── Detalle de la tarea ─────────────────────");
        System.out.println("  ID:           " + tarea.getId());
        System.out.println("  Título:       " + tarea.getTitulo());
        System.out.println("  Descripción:  " + (tarea.getDescripcion().isEmpty() ? "—" : tarea.getDescripcion()));
        System.out.println("  Estado:       " + tarea.getEstado());
        System.out.println("  Creada:       " + tarea.getFechaCreacion());
        System.out.println("  Actualizada:  " + tarea.getFechaActualizacion());
    }

    private void archivarTarea() {
        System.out.print("ID de la tarea a archivar (primeros 8 caracteres): ");
        String idParcial = scanner.nextLine().trim();
        String idCompleto = resolverIdParcial(idParcial);
        tareaService.archivarTarea(idCompleto);
        System.out.println("✔ Tarea archivada correctamente.");
    }

    private void eliminarTarea() {
        System.out.print("ID de la tarea a eliminar (primeros 8 caracteres): ");
        String idParcial = scanner.nextLine().trim();
        String idCompleto = resolverIdParcial(idParcial);
        System.out.print("⚠ ¿Confirmas la eliminación? (s/n): ");
        String confirmacion = scanner.nextLine().trim();
        if (confirmacion.equalsIgnoreCase("s")) {
            tareaService.eliminarTarea(idCompleto);
            System.out.println("✔ Tarea eliminada correctamente.");
        } else {
            System.out.println("ℹ Eliminación cancelada.");
        }
    }

    // ── Utilidades ───────────────────────────────────────────────────────────

    private String resolverIdParcial(String prefijo) {
        return tareaService.listarTareas().stream()
                .filter(t -> t.getId().startsWith(prefijo))
                .map(Tarea::getId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró ninguna tarea con el prefijo: " + prefijo));
    }

    private void imprimirTarea(Tarea t) {
        String icono = switch (t.getEstado()) {
            case PENDIENTE   -> "○";
            case COMPLETADA  -> "✔";
            case ARCHIVADA   -> "✦";
        };
        System.out.printf("  %s [%s] %s%n", icono, t.getId().substring(0, 8), t.getTitulo());
        if (!t.getDescripcion().isEmpty()) {
            System.out.printf("       └─ %s%n", t.getDescripcion());
        }
    }
}
