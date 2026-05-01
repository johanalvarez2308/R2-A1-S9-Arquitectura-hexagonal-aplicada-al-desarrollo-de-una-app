<<<<<<< HEAD
# 📚 Gestor de Tareas Académicas

Aplicación Java desarrollada con **Arquitectura Hexagonal (Ports & Adapters)** para gestionar tareas académicas de forma estructurada.

---

## 🗂 Estructura del Proyecto

```
tareas-academicas/
├── src/
│   ├── main/java/com/tareas/
│   │   ├── Main.java                                  ← Punto de entrada
│   │   ├── Demo.java                                  ← Demostración automatizada
│   │   │
│   │   ├── domain/                                    ← NÚCLEO (sin dependencias externas)
│   │   │   ├── model/
│   │   │   │   ├── Tarea.java                         ← Entidad del dominio
│   │   │   │   └── EstadoTarea.java                   ← Enum de estados
│   │   │   └── ports/
│   │   │       ├── TareaService.java                  ← Puerto de ENTRADA
│   │   │       └── TareaRepository.java               ← Puerto de SALIDA
│   │   │
│   │   ├── application/
│   │   │   └── usecases/
│   │   │       └── TareaServiceImpl.java              ← Casos de uso
│   │   │
│   │   └── infrastructure/
│   │       ├── adapters/
│   │       │   ├── in/cli/
│   │       │   │   └── TareaCLI.java                  ← Adaptador de ENTRADA (CLI)
│   │       │   └── out/persistence/
│   │       │       ├── InMemoryTareaRepository.java   ← Adaptador de SALIDA (memoria)
│   │       │       └── JsonFileTareaRepository.java   ← Adaptador de SALIDA (JSON)
│   │       └── config/
│   │           └── AppConfig.java                     ← Composition Root
│   │
│   └── test/java/com/tareas/
│       └── TareaServiceTest.java                      ← Pruebas funcionales
│
├── tareas-academicas.jar                              ← JAR ejecutable (CLI interactiva)
├── tareas-demo.jar                                    ← JAR de demostración automática
└── README.md
```

---

## 🏗 Arquitectura Hexagonal

```
         ┌─────────────────────────────────────────────────────────┐
         │                  INFRAESTRUCTURA                         │
         │                                                          │
         │   ┌──────────────┐         ┌────────────────────────┐   │
         │   │  TareaCLI    │         │ InMemoryTareaRepository │   │
         │   │ (Adaptador   │         │ JsonFileTareaRepository │   │
         │   │  de entrada) │         │  (Adaptadores de salida)│   │
         │   └──────┬───────┘         └───────────┬────────────┘   │
         └──────────┼─────────────────────────────┼────────────────┘
                    │                             │
              ┌─────▼─────────┐         ┌────────▼──────────┐
              │  TareaService │         │  TareaRepository   │
              │ (Puerto       │         │  (Puerto de        │
              │  de entrada)  │         │   salida)          │
              └─────┬─────────┘         └────────┬──────────┘
                    │                             │
         ┌──────────┼─────────────────────────────┼────────────────┐
         │          │       DOMINIO / APP          │                │
         │   ┌──────▼──────────────────────────────▼───────┐       │
         │   │           TareaServiceImpl                   │       │
         │   │           (Casos de uso)                     │       │
         │   │                    │                         │       │
         │   │              ┌─────▼───────┐                │       │
         │   │              │    Tarea    │                 │       │
         │   │              │  (Entidad)  │                 │       │
         │   │              └─────────────┘                 │       │
         │   └──────────────────────────────────────────────┘       │
         └───────────────────────────────────────────────────────────┘
```

### Capas y responsabilidades

| Capa | Componente | Responsabilidad |
|------|-----------|-----------------|
| **Dominio** | `Tarea`, `EstadoTarea` | Entidad con reglas de negocio (título obligatorio, transiciones de estado) |
| **Dominio** | `TareaService` | Puerto de entrada — contrato que expone los casos de uso |
| **Dominio** | `TareaRepository` | Puerto de salida — contrato de persistencia |
| **Aplicación** | `TareaServiceImpl` | Orquesta casos de uso sin depender de la infraestructura |
| **Infraestructura** | `TareaCLI` | Adaptador de entrada: traduce consola → casos de uso |
| **Infraestructura** | `InMemoryTareaRepository` | Adaptador de salida: persiste en memoria |
| **Infraestructura** | `JsonFileTareaRepository` | Adaptador de salida: persiste en archivo `tareas.json` |
| **Infraestructura** | `AppConfig` | Composition Root: ensambla los componentes |

---

## 📋 Funcionalidades

| # | Caso de uso | Regla de negocio |
|---|------------|-----------------|
| 1 | Registrar tarea | El título no puede estar vacío |
| 2 | Listar todas las tareas | Muestra pendientes, completadas y archivadas |
| 3 | Listar tareas pendientes | Solo devuelve tareas en estado PENDIENTE |
| 4 | Completar tarea | Solo puede completarse si está PENDIENTE |
| 5 | Consultar detalle | Busca por ID y lanza error si no existe |
| 6 | Archivar tarea | Cambia estado a ARCHIVADA |
| 7 | Eliminar tarea | Elimina definitivamente del repositorio |

---

## ⚙️ Requisitos

- **Java 17 o superior** (probado con Java 21)
- No requiere dependencias externas ni Maven/Gradle

---

## 🚀 Instalación y Ejecución

### Opción A — Usar los JARs precompilados

```bash
# Demo automática (no requiere input)
java -jar tareas-demo.jar

# CLI interactiva con persistencia en memoria
java -jar tareas-academicas.jar

# CLI interactiva con persistencia en archivo JSON
java -jar tareas-academicas.jar json
```

### Opción B — Compilar desde fuentes

```bash
# 1. Compilar
find src/main -name "*.java" > sources.txt
find src/test  -name "*.java" >> sources.txt
mkdir -p out
javac -d out @sources.txt

# 2. Ejecutar demo
java -cp out com.tareas.Demo

# 3. Ejecutar pruebas
java -cp out com.tareas.TareaServiceTest

# 4. Ejecutar CLI interactiva
java -cp out com.tareas.Main           # memoria
java -cp out com.tareas.Main json      # archivo JSON

# 5. (Opcional) Empaquetar JAR
jar --create --file tareas-academicas.jar --main-class com.tareas.Main -C out .
```

---

## 🧪 Pruebas

Las pruebas están en `src/test/java/com/tareas/TareaServiceTest.java` y cubren:

- ✔ Registrar tarea con título válido
- ✔ Registrar tarea con título vacío lanza excepción (`IllegalArgumentException`)
- ✔ Listar todas las tareas
- ✔ Listar pendientes solo muestra pendientes
- ✔ Completar tarea cambia su estado
- ✔ Completar tarea ya completada lanza excepción (`IllegalStateException`)
- ✔ Consultar tarea existente
- ✔ Consultar ID inexistente lanza excepción
- ✔ Archivar tarea
- ✔ Eliminar tarea
- ✔ Eliminar tarea inexistente lanza excepción

**Resultado: 11/11 pruebas exitosas.**

```bash
java -cp out com.tareas.TareaServiceTest
```

---

## 💾 Persistencia JSON

Al ejecutar con el modo `json`, las tareas se guardan en `tareas.json` junto al JAR:

```json
[
  {"id":"4fde715e-...","titulo":"Entregar laboratorio de POO",
   "descripcion":"Implementar patrón Strategy","estado":"COMPLETADA",
   "fechaCreacion":"2026-05-01T15:01:17","fechaActualizacion":"2026-05-01T15:02:05"},
  ...
]
```

---

## 🔄 Extensibilidad

La arquitectura hexagonal permite agregar nuevos adaptadores sin modificar el dominio:

| Adaptador nuevo | Qué implementa |
|----------------|----------------|
| API REST (Spring Boot) | `TareaService` como `@RestController` |
| Base de datos JDBC | `TareaRepository` con queries SQL |
| Interfaz gráfica (JavaFX) | `TareaService` desde los controladores de vista |
| Tests de integración | `TareaRepository` con H2 en memoria |
=======
# R2-A1-S9-Arquitectura-hexagonal-aplicada-al-desarrollo-de-una-app
>>>>>>> 71ab4b67546a4b63ba22ca0178ec242bab4f695f
