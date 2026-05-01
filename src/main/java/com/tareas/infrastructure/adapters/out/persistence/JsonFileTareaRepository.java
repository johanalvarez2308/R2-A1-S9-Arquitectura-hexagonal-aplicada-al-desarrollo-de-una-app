package com.tareas.infrastructure.adapters.out.persistence;

import com.tareas.domain.model.EstadoTarea;
import com.tareas.domain.model.Tarea;
import com.tareas.domain.ports.TareaRepository;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Adaptador de salida: repositorio con persistencia en archivo JSON.
 *
 * Serializa/deserializa las tareas en un archivo JSON simple sin dependencias
 * externas. Implementa el mismo contrato (TareaRepository) que el adaptador
 * en memoria, lo que permite intercambiarlos sin modificar el dominio.
 */
public class JsonFileTareaRepository implements TareaRepository {

    private final Path archivoJson;
    private final Map<String, Tarea> cache = new LinkedHashMap<>();

    public JsonFileTareaRepository(String rutaArchivo) {
        this.archivoJson = Paths.get(rutaArchivo);
        cargarDesdeArchivo();
    }

    @Override
    public void guardar(Tarea tarea) {
        cache.put(tarea.getId(), tarea);
        persistir();
    }

    @Override
    public Optional<Tarea> buscarPorId(String id) {
        return Optional.ofNullable(cache.get(id));
    }

    @Override
    public List<Tarea> listarTodas() {
        return new ArrayList<>(cache.values());
    }

    @Override
    public List<Tarea> listarPendientes() {
        return cache.values().stream()
                .filter(Tarea::esPendiente)
                .collect(Collectors.toList());
    }

    @Override
    public void eliminar(String id) {
        if (!cache.containsKey(id)) {
            throw new IllegalArgumentException("No existe tarea con id: " + id);
        }
        cache.remove(id);
        persistir();
    }

    // ── Serialización manual a JSON ──────────────────────────────────────────

    private void persistir() {
        StringBuilder sb = new StringBuilder("[\n");
        List<Tarea> tareas = new ArrayList<>(cache.values());
        for (int i = 0; i < tareas.size(); i++) {
            sb.append(tareaAJson(tareas.get(i)));
            if (i < tareas.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("]");
        try {
            Files.writeString(archivoJson, sb.toString());
        } catch (IOException e) {
            throw new RuntimeException("Error al persistir tareas: " + e.getMessage(), e);
        }
    }

    private String tareaAJson(Tarea t) {
        return String.format(
                "  {\"id\":\"%s\",\"titulo\":\"%s\",\"descripcion\":\"%s\"," +
                "\"estado\":\"%s\",\"fechaCreacion\":\"%s\",\"fechaActualizacion\":\"%s\"}",
                t.getId(),
                escapar(t.getTitulo()),
                escapar(t.getDescripcion()),
                t.getEstado().name(),
                t.getFechaCreacion().toString(),
                t.getFechaActualizacion().toString()
        );
    }

    private String escapar(String texto) {
        return texto.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private void cargarDesdeArchivo() {
        if (!Files.exists(archivoJson)) return;
        try {
            String contenido = Files.readString(archivoJson).trim();
            if (contenido.isEmpty() || contenido.equals("[]")) return;
            // Parseo manual liviano para no requerir librerías externas
            String[] entradas = contenido
                    .replaceAll("^\\[\\n?", "")
                    .replaceAll("\\n?]$", "")
                    .split("\\},\\s*\\{");
            for (String entrada : entradas) {
                entrada = entrada.replaceAll("^\\s*\\{", "").replaceAll("\\}\\s*$", "");
                Map<String, String> campos = parsearCampos(entrada);
                Tarea tarea = Tarea.reconstruir(
                        campos.get("id"),
                        campos.get("titulo"),
                        campos.get("descripcion"),
                        EstadoTarea.valueOf(campos.get("estado")),
                        LocalDateTime.parse(campos.get("fechaCreacion")),
                        LocalDateTime.parse(campos.get("fechaActualizacion"))
                );
                cache.put(tarea.getId(), tarea);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al cargar tareas: " + e.getMessage(), e);
        }
    }

    private Map<String, String> parsearCampos(String json) {
        Map<String, String> mapa = new LinkedHashMap<>();
        String[] pares = json.split(",\"");
        for (String par : pares) {
            par = par.replaceAll("^\"|^", "");
            int sep = par.indexOf("\":\"");
            if (sep == -1) continue;
            String clave = par.substring(0, sep).replaceAll("\"", "");
            String valor = par.substring(sep + 3).replaceAll("\"$", "");
            mapa.put(clave, valor);
        }
        return mapa;
    }
}
