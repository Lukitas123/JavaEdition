package modelo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PuntajeTest {
    @Test
    void sigueLaFormulaDeLaCatedra() {
        // 3 cofres x 100 + 50 de energia x 2 - 20 pasos
        assertEquals(380, new Puntaje(3, 50, 20).valor());
    }

    @Test
    void sinCofresSoloCuentaLaEnergiaMenosLosPasos() {
        assertEquals(190, new Puntaje(0, 100, 10).valor());
    }

    @Test
    void puedeSerNegativoSiSeDieronMuchosPasos() {
        assertEquals(-10, new Puntaje(0, 0, 10).valor());
    }
}
