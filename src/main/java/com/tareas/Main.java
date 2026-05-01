package com.tareas;

import com.tareas.infrastructure.adapters.in.cli.TareaCLI;
import com.tareas.infrastructure.config.AppConfig;

/**
 * Punto de entrada de la aplicación.
 *
 * Acepta un argumento opcional:
 *   java -jar tareas.jar memory  → repositorio en memoria (por defecto)
 *   java -jar tareas.jar json    → repositorio en archivo JSON
 */
public class Main {

    public static void main(String[] args) {
        String modo = (args.length > 0) ? args[0] : "memory";
        TareaCLI cli = AppConfig.crearCliApp(modo);
        cli.iniciar();
    }
}
