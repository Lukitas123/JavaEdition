package generador;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ValidadorDePlanosTest {
    private ValidadorDePlanos validador;

    private static final String PLANO_VALIDO_B3 = """
            {
              "version": 1,
              "generador": "Grupo 03",
              "parametros": {
                "version": 1, "dificultad": "MEDIO", "filas": 9, "columnas": 12,
                "semilla": 7, "cofres": 1, "monstruos": 1, "conSalida": true
              },
              "entrada": { "fila": 2, "columna": 2 },
              "salida": { "fila": 8, "columna": 10 },
              "plano": [
                "############",
                "#E..#......#",
                "#.#.#.##.#.#",
                "#.#...#..~.#",
                "#.###.#.##.#",
                "#...#...C..#",
                "#.#.#.#.##.#",
                "#.M.....#S.#",
                "############"
              ]
            }
            """;

    @BeforeEach
    void inicializar() {
        validador = new ValidadorDePlanos();
    }

    @Test
    void planoDeEjemploDelAnexoB3EsValido() {
        assertDoesNotThrow(() -> validador.validar(PLANO_VALIDO_B3));
    }

    @Test
    void jsonNuloOVacioLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> validador.validar(null));
        assertThrows(IllegalArgumentException.class, () -> validador.validar("   "));
    }

    @Test
    void rechazaPlanoQueViolaR1Dimensiones() {
        // El parametro dice 9 filas pero el arreglo tiene 8 filas
        String planoR1 = """
                {
                  "version": 1,
                  "generador": "Grupo 03",
                  "parametros": {
                    "version": 1, "dificultad": "MEDIO", "filas": 9, "columnas": 12,
                    "semilla": 7, "cofres": 1, "monstruos": 1, "conSalida": true
                  },
                  "entrada": { "fila": 2, "columna": 2 },
                  "salida": { "fila": 7, "columna": 10 },
                  "plano": [
                    "############",
                    "#E..#......#",
                    "#.#.#.##.#.#",
                    "#.#...#..~.#",
                    "#...#...C..#",
                    "#.#.#.#.##.#",
                    "#.M.....#S.#",
                    "############"
                  ]
                }
                """;
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> validador.validar(planoR1));
        assertTrue(ex.getMessage().contains("R1"));
    }

    @Test
    void rechazaPlanoQueViolaR2SimbolosInvalidos() {
        // Contiene una 'X' en la fila 2 columna 4 en lugar de '.'
        String planoR2 = """
                {
                  "version": 1,
                  "generador": "Grupo 03",
                  "parametros": {
                    "version": 1, "dificultad": "MEDIO", "filas": 9, "columnas": 12,
                    "semilla": 7, "cofres": 1, "monstruos": 1, "conSalida": true
                  },
                  "entrada": { "fila": 2, "columna": 2 },
                  "salida": { "fila": 8, "columna": 10 },
                  "plano": [
                    "############",
                    "#E.X#......#",
                    "#.#.#.##.#.#",
                    "#.#...#..~.#",
                    "#.###.#.##.#",
                    "#...#...C..#",
                    "#.#.#.#.##.#",
                    "#.M.....#S.#",
                    "############"
                  ]
                }
                """;
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> validador.validar(planoR2));
        assertTrue(ex.getMessage().contains("R2"));
    }

    @Test
    void rechazaPlanoQueViolaR3BordeDePared() {
        // Tiene un '.' en el borde superior
        String planoR3 = """
                {
                  "version": 1,
                  "generador": "Grupo 03",
                  "parametros": {
                    "version": 1, "dificultad": "MEDIO", "filas": 9, "columnas": 12,
                    "semilla": 7, "cofres": 1, "monstruos": 1, "conSalida": true
                  },
                  "entrada": { "fila": 2, "columna": 2 },
                  "salida": { "fila": 8, "columna": 10 },
                  "plano": [
                    "#####.######",
                    "#E..#......#",
                    "#.#.#.##.#.#",
                    "#.#...#..~.#",
                    "#.###.#.##.#",
                    "#...#...C..#",
                    "#.#.#.#.##.#",
                    "#.M.....#S.#",
                    "############"
                  ]
                }
                """;
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> validador.validar(planoR3));
        assertTrue(ex.getMessage().contains("R3"));
    }

    @Test
    void rechazaPlanoQueViolaR4EntradaOSalidaIncorrecta() {
        // La coordenada de entrada en el JSON (2, 3) no coincide con la 'E' en (2, 2)
        String planoR4 = """
                {
                  "version": 1,
                  "generador": "Grupo 03",
                  "parametros": {
                    "version": 1, "dificultad": "MEDIO", "filas": 9, "columnas": 12,
                    "semilla": 7, "cofres": 1, "monstruos": 1, "conSalida": true
                  },
                  "entrada": { "fila": 2, "columna": 3 },
                  "salida": { "fila": 8, "columna": 10 },
                  "plano": [
                    "############",
                    "#E..#......#",
                    "#.#.#.##.#.#",
                    "#.#...#..~.#",
                    "#.###.#.##.#",
                    "#...#...C..#",
                    "#.#.#.#.##.#",
                    "#.M.....#S.#",
                    "############"
                  ]
                }
                """;
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> validador.validar(planoR4));
        assertTrue(ex.getMessage().contains("R4"));
    }

    @Test
    void rechazaPlanoQueViolaR5NoConexo() {
        // Se bloquea el unico puente entre izquierda y derecha en (8, 5) con '#'
        String planoR5 = """
                {
                  "version": 1,
                  "generador": "Grupo 03",
                  "parametros": {
                    "version": 1, "dificultad": "MEDIO", "filas": 9, "columnas": 12,
                    "semilla": 7, "cofres": 1, "monstruos": 1, "conSalida": true
                  },
                  "entrada": { "fila": 2, "columna": 2 },
                  "salida": { "fila": 8, "columna": 10 },
                  "plano": [
                    "############",
                    "#E..#......#",
                    "#.#.#.##.#.#",
                    "#.#..##..~.#",
                    "#.###.#.##.#",
                    "#...#...C..#",
                    "#.#.#.#.##.#",
                    "#.M.#...#S.#",
                    "############"
                  ]
                }
                """;
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> validador.validar(planoR5));
        assertTrue(ex.getMessage().contains("R5"));
    }

    @Test
    void rechazaPlanoQueViolaR6SinBifurcaciones() {
        // Pasillo unico en serpentina sin ninguna celda con 3 o mas vecinos transitables
        String planoR6 = """
                {
                  "version": 1,
                  "generador": "Grupo 03",
                  "parametros": {
                    "version": 1, "dificultad": "MEDIO", "filas": 9, "columnas": 12,
                    "semilla": 7, "cofres": 1, "monstruos": 1, "conSalida": true
                  },
                  "entrada": { "fila": 2, "columna": 2 },
                  "salida": { "fila": 8, "columna": 11 },
                  "plano": [
                    "############",
                    "#E.........#",
                    "##########.#",
                    "#..........#",
                    "#.##########",
                    "#...C......#",
                    "##########.#",
                    "#.M.......S#",
                    "############"
                  ]
                }
                """;
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> validador.validar(planoR6));
        assertTrue(ex.getMessage().contains("R6"));
    }

    @Test
    void rechazaPlanoQueViolaR7ProporcionDeTransitables() {
        // Tiene menos del 30% de celdas transitables (12 de 108 = 11%), pero cumple R1..R6
        String planoR7 = """
                {
                  "version": 1,
                  "generador": "Grupo 03",
                  "parametros": {
                    "version": 1, "dificultad": "MEDIO", "filas": 9, "columnas": 12,
                    "semilla": 7, "cofres": 1, "monstruos": 1, "conSalida": true
                  },
                  "entrada": { "fila": 2, "columna": 2 },
                  "salida": { "fila": 4, "columna": 6 },
                  "plano": [
                    "############",
                    "#E...#######",
                    "#.C..#######",
                    "#.M..S######",
                    "############",
                    "############",
                    "############",
                    "############",
                    "############"
                  ]
                }
                """;
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> validador.validar(planoR7));
        assertTrue(ex.getMessage().contains("R7"));
    }
}
