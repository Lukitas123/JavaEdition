package modelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TableroTest {

    @Test
    void copiaLaGrillaSinCompartirLasFilas() {
        String[][] datos = {
                {"inicio", "libre"},
                {"pared", "salida"}
        };
        Tablero<String> tablero = new Tablero<>(datos);

        datos[0][0] = "externo";
        assertEquals("inicio", tablero.celdaEn(new Posicion(1, 1)));

        tablero.cambiarCelda(new Posicion(2, 2), "ocupado");
        assertEquals("salida", datos[1][1]);
        assertEquals("ocupado", tablero.celdaEn(new Posicion(2, 2)));
    }

    @Test
    void rechazaGrillasVaciasIrregularesONulas() {
        assertThrows(IllegalArgumentException.class,
                () -> new Tablero<String>(null));
        assertThrows(IllegalArgumentException.class,
                () -> new Tablero<>(new String[0][0]));
        assertThrows(IllegalArgumentException.class,
                () -> new Tablero<>(new String[][] {null}));
        assertThrows(IllegalArgumentException.class,
                () -> new Tablero<>(new String[][] {{"a"}, {"b", "c"}}));
        assertThrows(IllegalArgumentException.class,
                () -> new Tablero<>(new String[][] {{null}}));
    }

    @Test
    void validaLosLimitesAntesDeConsultarOModificar() {
        Tablero<String> tablero =
                new Tablero<>(new String[][] {{"a", "b"}});
        Posicion fuera = new Posicion(1, 3);

        assertFalse(tablero.estaDentro(fuera));
        assertFalse(tablero.estaDentro(null));
        assertThrows(IllegalArgumentException.class,
                () -> tablero.celdaEn(fuera));
        assertThrows(IllegalArgumentException.class,
                () -> tablero.cambiarCelda(fuera, "c"));
        assertThrows(IllegalArgumentException.class,
                () -> tablero.cambiarCelda(new Posicion(1, 1), null));
    }
}