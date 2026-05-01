package com.tareas;

import com.tareas.domain.model.Tarea;
import com.tareas.domain.ports.TareaService;
import com.tareas.infrastructure.config.AppConfig;

import java.util.List;

/**
 * Demostración de ejecución funcional de todos los casos de uso.
 * Simula un flujo completo sin necesitar entrada interactiva del usuario.
 */
public class Demo {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║   DEMO — GESTOR DE TAREAS ACADÉMICAS     ║");
        System.out.println("╚══════════════════════════════════════════╝\n");

        TareaService service = AppConfig.crearServicio("memory");

        // ── 1. Registrar tareas ──────────────────────────────────────────────
        seccion("1. Registrar tareas académicas");
        Tarea t1 = service.registrarTarea("Entregar laboratorio de POO",
                "Implementar patrón Strategy");
        Tarea t2 = service.registrarTarea("Leer capítulo 5 de BD",
                "Normalización hasta 3FN");
        Tarea t3 = service.registrarTarea("Preparar exposición de Redes",
                "Modelo OSI vs TCP/IP");
        Tarea t4 = service.registrarTarea("Ejercicios de Cálculo",
                "Integrales páginas 120-135");
        imprimirLista(List.of(t1, t2, t3, t4));

        // ── 2. Listar todas ──────────────────────────────────────────────────
        seccion("2. Listar todas las tareas");
        imprimirLista(service.listarTareas());

        // ── 3. Completar una tarea ───────────────────────────────────────────
        seccion("3. Completar tarea: " + t1.getTitulo());
        Tarea completada = service.completarTarea(t1.getId());
        System.out.println("  Estado actualizado → " + completada.getEstado());

        // ── 4. Listar pendientes ─────────────────────────────────────────────
        seccion("4. Listar solo tareas PENDIENTES");
        imprimirLista(service.listarPendientes());

        // ── 5. Consultar detalle ─────────────────────────────────────────────
        seccion("5. Consultar detalle de tarea");
        Tarea detalle = service.consultarTarea(t2.getId());
        System.out.println("  ID:          " + detalle.getId());
        System.out.println("  Título:      " + detalle.getTitulo());
        System.out.println("  Descripción: " + detalle.getDescripcion());
        System.out.println("  Estado:      " + detalle.getEstado());
        System.out.println("  Creada:      " + detalle.getFechaCreacion());

        // ── 6. Archivar una tarea ────────────────────────────────────────────
        seccion("6. Archivar tarea: " + t3.getTitulo());
        service.archivarTarea(t3.getId());
        System.out.println("  Estado → " + service.consultarTarea(t3.getId()).getEstado());

        // ── 7. Eliminar una tarea ────────────────────────────────────────────
        seccion("7. Eliminar tarea: " + t4.getTitulo());
        service.eliminarTarea(t4.getId());
        System.out.println("  Total de tareas después de eliminar: "
                + service.listarTareas().size());

        // ── 8. Estado final ──────────────────────────────────────────────────
        seccion("8. Estado final de todas las tareas");
        imprimirLista(service.listarTareas());

        // ── 9. Validación de regla de negocio ────────────────────────────────
        seccion("9. Intentar registrar tarea con título vacío (regla de negocio)");
        try {
            service.registrarTarea("", null);
        } catch (IllegalArgumentException e) {
            System.out.println("  ✔ Excepción capturada correctamente: " + e.getMessage());
        }

        System.out.println("\n✔ Demo completada exitosamente.");
    }

    private static void seccion(String titulo) {
        System.out.println("\n── " + titulo + " " + "─".repeat(Math.max(0, 46 - titulo.length())));
    }

    private static void imprimirLista(List<Tarea> tareas) {
        if (tareas.isEmpty()) {
            System.out.println("  (lista vacía)");
            return;
        }
        tareas.forEach(t -> {
            String icono = switch (t.getEstado()) {
                case PENDIENTE  -> "○";
                case COMPLETADA -> "✔";
                case ARCHIVADA  -> "✦";
            };
            System.out.printf("  %s [%s] %-38s [%s]%n",
                    icono, t.getId().substring(0, 8), t.getTitulo(), t.getEstado());
        });
    }
}
