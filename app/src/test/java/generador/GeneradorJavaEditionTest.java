package generador;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GeneradorJavaEditionTest {
    private GeneradorJavaEdition generador;

    @BeforeEach
    void inicializar() {
        generador = new GeneradorJavaEdition();
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
}
