# JavaEdition - TP2 La Fuga de la Mazmorra (CB100)

## 📋 Reglas de Organización del Grupo

### 1. Sistema de Ramas (Branches)
- **NUNCA trabajamos directo en `main`.** La rama `main` siempre debe compilar y tener una versión que pase las pruebas.
- Para desarrollar algo nuevo, crea una rama desde *main* con el formato: `tipo/nombre-de-la-tarea`.
  - Ejemplos: `feat/estructuras-pila`, `feat/vista-bmp`, `fix/bug-generador` o `docs/agregar-manual`.
- Cuando terminas, haces un *Pull Request* hacia main y avisas así otro lo revisa antes de mezclarlo.

### 2. Mensajes de Commit
La materia evaluará qué hizo cada uno. **Cada uno debe commitear con SU usuario.**
- Sean descriptivos: "Implementa Pila sin java.util", "Corrige Dijkstra", "Agrega lectura json".
- Nada de commits vacíos o inentendibles como "asdasd".

### 3. Asignación de Roles 
- **Modelo Base (Mazmorra, Héroe, reglas, agenda, conexiones):** Sabri
- **Estructuras Personales (Lista, pila, cola, heap, hash, ABB, tests):** Franco
- **Generación y grafo (Laberinto, validador, BFS, Dijkstra):** Lukas
- **Monstruos y Cofres (Jerarquías, 10 tipos, catálogo, tests):** Sofia Toledo
- **Persistencia y vista (Configuración, guardado, ranking, VistaBmp):** Sofi R
- **Integración general (Main, tests E2E, Git, informe):** Santi

### 4. Tests
Antes de subir algo, corran de local `./gradlew test` o verifiquen que todo compile en IntelliJ.

### 5. Estructura de Carpetas (`grupo03/tp2/`)
- `estructuras/` (Franco): Estructuras propias **SIN java.util** (Pila, Cola, Lista, ColaDePrioridad, TablaHash, ABB).
- `entidades/` (Sofia Toledo): `monstruos/` y `cofres/` (Las clases abstractas y sus 5/10 implementaciones polimórficas).
- `generador/` (Lukas): Implementación del generador usando Backtracking y el validador de las 11 reglas usando BFS/Dijkstra.
- `modelo/` (Sabri): Núcleo del juego (`Mazmorra`, `Heroe`, `Agenda`, terrenos).
- `persistencia/` y `vista/` (Sofi R): Lectura/Escritura de JSON (Gson permitido), ranking y generación obligatoria de la ventana u archivos `.bmp`.
- **Integración general** (Santi): `main` del juego (consola `w a s d`), E2E tests, y pegamento de todas las piezas.
