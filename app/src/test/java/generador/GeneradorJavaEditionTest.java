package generador;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GeneradorJavaEditionTest {
    private GeneradorJavaEdition generador;
    private ValidadorDePlanos validador;

    @BeforeEach
    void inicializar() {
        generador = new GeneradorJavaEdition();
        validador = new ValidadorDePlanos();
    }

    @Test
    void rechazaJsonNuloOVacio() {
        assertThrows(IllegalArgumentException.class, () -> generador.generar(null));
        assertThrows(IllegalArgumentException.class, () -> generador.generar("   "));
    }

    @Test
    void rechazaJsonSinFilasOCamposFaltantes() {
        String sinFilas = """
                {
                  "version": 1,
                  "dificultad": "MEDIO",
                  "columnas": 20,
                  "semilla": 42,
                  "cofres": 2,
                  "monstruos": 2,
                  "conSalida": false
                }
                """;
        assertThrows(IllegalArgumentException.class, () -> generador.generar(sinFilas));
    }

    @Test
    void rechazaDimensionesMenoresACinco() {
        String filasTres = """
                {
                  "version": 1,
                  "dificultad": "FACIL",
                  "filas": 3,
                  "columnas": 10,
                  "semilla": 42,
                  "cofres": 1,
                  "monstruos": 1,
                  "conSalida": true
                }
                """;
        assertThrows(IllegalArgumentException.class, () -> generador.generar(filasTres));
    }

    @Test
    void rechazaCofresOMonstruosNegativos() {
        String cofresNegativos = """
                {
                  "version": 1,
                  "dificultad": "FACIL",
                  "filas": 10,
                  "columnas": 10,
                  "semilla": 42,
                  "cofres": -1,
                  "monstruos": 1,
                  "conSalida": true
                }
                """;
        assertThrows(IllegalArgumentException.class, () -> generador.generar(cofresNegativos));
    }

    @Test
    void rechazaMasCofresYMonstruosQueCeldasDisponibles() {
        String demasiadosCofres = """
                {
                  "version": 1,
                  "dificultad": "FACIL",
                  "filas": 6,
                  "columnas": 6,
                  "semilla": 42,
                  "cofres": 30,
                  "monstruos": 10,
                  "conSalida": true
                }
                """;
        assertThrows(IllegalArgumentException.class, () -> generador.generar(demasiadosCofres));
    }

    @Test
    void determinismoMismaSemillaGeneraElMismoPlano() {
        String entradaJson = armarJsonEntrada("MEDIO", 15, 20, 42L, 6, 6, true);
        String plano1 = generador.generar(entradaJson);
        String plano2 = generador.generar(entradaJson);
        assertEquals(plano1, plano2);
    }

    @Test
    void distintasSemillasGeneranDistintosPlanos() {
        String jsonSemilla1 = armarJsonEntrada("MEDIO", 15, 20, 1L, 6, 6, true);
        String jsonSemilla2 = armarJsonEntrada("MEDIO", 15, 20, 2L, 6, 6, true);
        String plano1 = generador.generar(jsonSemilla1);
        String plano2 = generador.generar(jsonSemilla2);
        assertNotEquals(plano1, plano2);
    }

    @Test
    void planosValidosEnFacilConVeinteSemillas() {
        for (long semilla = 1; semilla <= 20; semilla++) {
            String entradaJson = armarJsonEntrada("FACIL", 25, 40, semilla, 12, 4, true);
            String planoJson = generador.generar(entradaJson);
            assertDoesNotThrow(() -> validador.validar(planoJson),
                    "Fallo validacion en FACIL con semilla " + semilla);
        }
    }

    @Test
    void planosValidosEnMedioConVeinteSemillasConYSinSalida() {
        for (long semilla = 1; semilla <= 20; semilla++) {
            String conSalida = armarJsonEntrada("MEDIO", 15, 20, semilla, 2, 2, true);
            String sinSalida = armarJsonEntrada("MEDIO", 15, 20, semilla, 2, 2, false);
            assertDoesNotThrow(() -> validador.validar(generador.generar(conSalida)),
                    "Fallo validacion en MEDIO (con salida) con semilla " + semilla);
            assertDoesNotThrow(() -> validador.validar(generador.generar(sinSalida)),
                    "Fallo validacion en MEDIO (sin salida) con semilla " + semilla);
        }
    }

    @Test
    void planosValidosEnDificilConVeinteSemillas() {
        for (long semilla = 1; semilla <= 20; semilla++) {
            String entradaJson = armarJsonEntrada("DIFICIL", 15, 20, semilla, 3, 10, true);
            String planoJson = generador.generar(entradaJson);
            assertDoesNotThrow(() -> validador.validar(planoJson),
                    "Fallo validacion en DIFICIL con semilla " + semilla);
        }
    }

    private String armarJsonEntrada(String dificultad, int filas, int columnas,
                                    long semilla, int cofres, int monstruos, boolean conSalida) {
        return String.format("""
                {
                  "version": 1,
                  "dificultad": "%s",
                  "filas": %d,
                  "columnas": %d,
                  "semilla": %d,
                  "cofres": %d,
                  "monstruos": %d,
                  "conSalida": %b
                }
                """, dificultad, filas, columnas, semilla, cofres, monstruos, conSalida);
    }
}
