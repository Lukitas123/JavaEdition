# JavaEdition - TP2 La Fuga de la Mazmorra (CB100)

## 📋 Reglas de Organización del Grupo

### 1. Sistema de Ramas (Branches)
- **NUNCA trabajaMOS directo en `main`.** La rama `main` siempre debe compilar y tener una versión que pase las pruebas.
- Para desarrollar algo nuevo, crea una rama desde *main* con el formato: `tipo/nombre-de-la-tarea`.
  - Ejemplos: `feat/estructuras-pila`, `feat/vista-bmp`, `fix/bug-generador` o `docs/agregar-manual`.
- Cuando terminas, haces un *Pull Request* hacia main y avisas así otro lo revisa antes de mezclarlo.

### 2. Mensajes de Commit
La materia evaluará qué hizo cada uno. **Cada uno debe commitear con SU usuario.**
- Sean descriptivos: "Implementa ArbolAVL", "Corrige Dijkstra", "Agrega lectura json".
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

### 5. Estructura de código: ¿Qué archivo .java va en cada carpeta?
Dado que estamos aprendiendo POO, Interfaces y Herencias, así se dividen físicamente nuestras piezas del rompecabezas:

- `estructuras/` **(Herramientas genéricas)**
  Régimen V2 del enunciado: primero se usa la API de Java (`ArrayDeque` para pila y cola, `LinkedList`/`ArrayList` para listas, `PriorityQueue` para heap, `HashMap`/`HashSet` para hash). El juego solo usa de acá las estructuras que la API no tiene: `ArbolAVL<T>.java` y `GrafoConPesos<T>.java`. Las propias `Lista`, `Pila`, `Cola`, `ColaDePrioridad` y `TablaHash` se conservan con sus tests como trabajo de la materia, pero el juego no las usa.

- `entidades/` **(Herencia y Polimorfismo)**
  - `monstruos/`: Acá va la clase abstracta `Monstruo.java`, y una clase por cada hijo (ej: `Cazador.java extends Monstruo`, `Errante.java extends Monstruo`).
  - `cofres/`: Mismo concepto. Clase abstracta `Cofre.java`, y sus 10 clases hijas separadas (`ZapatosDeAgua.java extends Cofre`, etc).

- `generador/` **(Interfaces)**
  Acá va la interfaz obligatoria dictada por el profe `GeneradorDeLaberinto.java`, nuestra clase `NnuestroGenerador.java implements GeneradorDeLaberinto`, y las clases matemáticas como `ValidadorDePlanos.java` (el que hace los chequeos BFS R1-R11).

- `modelo/` **(Clases con Lógica y Estado)**
  Acá van las clases que representan la lógica del TP: `Mazmorra.java` (el TDA principal), `Plano.java`, `Heroe.java`, `Agenda.java` (eventos temporales).
  *⚠️ REGLA DE ORO: Las clases de esta carpeta NUNCA tienen código para dibujar en pantalla. Cero uso de `java.awt` o `BufferedImage`.*

- `vista/` **(Exportador gráfico)**
  Acá van las clases que literalmente "leen" los objetos de `modelo/` y dibujan píxeles. Ejemplo: `VistaBmp.java`.
  *⚠️ REGLA DE ORO: La vista jamás altera la vida del héroe, solo la muestra.*

- `persistencia/` **(Archivos externos)**
  Acá van los administradores de archivos del disco duro: `LectorDeConfiguracion.java` (que lee los .json y arma objetos), y el del ranking. 
