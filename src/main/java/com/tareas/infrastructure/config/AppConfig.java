package com.tareas.infrastructure.config;

import com.tareas.application.usecases.TareaServiceImpl;
import com.tareas.domain.ports.TareaRepository;
import com.tareas.domain.ports.TareaService;
import com.tareas.infrastructure.adapters.in.cli.TareaCLI;
import com.tareas.infrastructure.adapters.out.persistence.InMemoryTareaRepository;
import com.tareas.infrastructure.adapters.out.persistence.JsonFileTareaRepository;

/**
 * Configuración de la aplicación (Composition Root).
 *
 * Es el único lugar donde se instancian y conectan los adaptadores con los
 * puertos del dominio. Aquí se decide qué implementación concreta usar para
 * cada puerto sin que el dominio lo sepa.
 */
public class AppConfig {

    private static final String MODO_JSON    = "json";
    private static final String ARCHIVO_JSON = "tareas.json";

    /**
     * Construye y devuelve el adaptador de entrada CLI completamente cableado.
     *
     * @param modo "memory" para repositorio en memoria, "json" para archivo JSON.
     */
    public static TareaCLI crearCliApp(String modo) {
        TareaRepository repositorio = crearRepositorio(modo);
        TareaService    servicio    = new TareaServiceImpl(repositorio);
        return new TareaCLI(servicio);
    }

    /**
     * Expone el TareaService para uso programático (ej.: pruebas o integraciones).
     */
    public static TareaService crearServicio(String modo) {
        return new TareaServiceImpl(crearRepositorio(modo));
    }

    // ── Privados ─────────────────────────────────────────────────────────────

    private static TareaRepository crearRepositorio(String modo) {
        if (MODO_JSON.equalsIgnoreCase(modo)) {
            System.out.println("ℹ Usando persistencia en archivo: " + ARCHIVO_JSON);
            return new JsonFileTareaRepository(ARCHIVO_JSON);
        }
        System.out.println("ℹ Usando persistencia en memoria.");
        return new InMemoryTareaRepository();
    }
}
